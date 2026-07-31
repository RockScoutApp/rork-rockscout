package com.rork.rockscout

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okhttp3.Cache
import okhttp3.OkHttpClient
import okio.Path.Companion.toPath
import java.io.File
import java.util.concurrent.TimeUnit
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AchievementsRepository
import com.rork.rockscout.data.AdAnalyticsTracker
import com.rork.rockscout.data.AffiliateClickTracker
import com.rork.rockscout.data.BugLogger
import com.rork.rockscout.data.IdentifyAccessManager
import com.rork.rockscout.data.IdentifyCache
import com.rork.rockscout.data.NotificationHelper
import com.rork.rockscout.data.PersistenceManager
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.CustomDigLocationStore
import com.rork.rockscout.data.DigSiteDiscoveryStore
import com.rork.rockscout.data.UserTimezoneProvider
import com.rork.rockscout.data.EmailComposerDraftStore
import com.rork.rockscout.data.CustomSpecimenStore
import com.rork.rockscout.data.LocationSubmissionStore
import com.rork.rockscout.data.LocalDataStore
import com.rork.rockscout.data.NightModeManager
import com.rork.rockscout.data.SpecimenSubmissionStore
import com.rork.rockscout.data.MockDataSeeder
import com.rork.rockscout.data.NotificationRepository
import com.rork.rockscout.data.ReferralRepository
import com.rork.rockscout.data.SubscriptionAdminManager
import com.rork.rockscout.data.ExportCleanupWorker
import com.rork.rockscout.data.UpdateManager
import com.rork.rockscout.data.WorkScheduler
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.osmdroid.config.Configuration

class RockScoutApplication : Application() {

    private val crashLogDir by lazy { File(filesDir, "crash_logs") }
    private val startupTag = "RockScoutStartup"

    /** Write the uncaught exception to a file so we can diagnose hard crashes. */
    private fun writeCrashLog(thread: Thread, throwable: Throwable) {
        runCatching {
            crashLogDir.mkdirs()
            File(crashLogDir, "last_crash.txt").writer().use { writer ->
                writer.append("Thread: ${thread.name}\n")
                writer.append("Exception: ${throwable.javaClass.name}\n")
                writer.append("Message: ${throwable.message}\n\n")
                throwable.printStackTrace(java.io.PrintWriter(writer))
            }
        }
    }

    /** Safely run a startup step. If it fails, log the error but keep the app alive. */
    private fun safeInit(label: String, block: () -> Unit) {
        try {
            Log.d(startupTag, "Starting: $label")
            block()
            Log.d(startupTag, "Finished: $label")
        } catch (e: Throwable) {
            Log.e(startupTag, "FAILED startup step '$label' — continuing", e)
            runCatching { BugLogger.logMessage(this, "Startup", "FAILED: $label", false) }
        }
    }

    override fun onCreate() {
        // Log as early as possible, even before super.onCreate(). Use Log.e with the
        // startup tag so it survives logcat's default filter and is visible in the
        // Rork runtime logs even if the process dies immediately afterward.
        Log.e(startupTag, "Application.onCreate() entered")

        super.onCreate()
        Log.e(startupTag, "super.onCreate() completed")

        // Install a global crash handler first. Keep it minimal and safe.
        runCatching {
            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                Log.e("RockScoutCrash", "Uncaught exception on ${thread.name}", throwable)
                writeCrashLog(thread, throwable)
                runCatching { previousHandler?.uncaughtException(thread, throwable) }
            }
            Log.e(startupTag, "Crash handler installed")
        }.onFailure {
            Log.e(startupTag, "Could not install crash handler", it)
        }

        // Wrap the entire startup sequence in a last-resort catch so that even
        // an unexpected failure during initialization leaves the app process alive.
        try {
            runStartup()
        } catch (e: Throwable) {
            Log.e(startupTag, "CRITICAL: entire startup sequence failed — continuing anyway", e)
            runCatching { BugLogger.logMessage(this, "Startup", "CRITICAL: runStartup failed", true) }
        }

        Log.e(startupTag, "Application.onCreate() completed")
    }

    private fun runStartup() {
        safeInit("osmdroid-config") {
            Configuration.getInstance().userAgentValue = "$packageName/${BuildConfig.VERSION_NAME}"
            Configuration.getInstance().osmdroidBasePath = filesDir.resolve("osmdroid")
            Configuration.getInstance().osmdroidTileCache = filesDir.resolve("osmdroid/tiles")
        }

        safeInit("persistence-manager") {
            PersistenceManager.initialize(this)
        }

        safeInit("app-repository-mark-loaded") {
            AppRepository.instance.markLoaded()
        }

        safeInit("sunspot-history-tracker") {
            com.rork.rockscout.data.SunspotHistoryTracker.initialize(this)
        }

        safeInit("night-mode") {
            NightModeManager.initialize()
        }

        safeInit("heal-osmdroid-tile-cache") {
            healOsmDroidTileCache()
        }

        safeInit("cache-size-config") {
            val cacheMode = PersistenceManager.loadCacheSizeMode()
            val (tileMax, tileTrim, imageMax) = if (cacheMode == "max") {
                Triple(2_000L * 1024 * 1024, 1_600L * 1024 * 1024, 2_000L * 1024 * 1024)
            } else {
                Triple(150L * 1024 * 1024, 120L * 1024 * 1024, 150L * 1024 * 1024)
            }
            Configuration.getInstance().setTileFileSystemCacheMaxBytes(tileMax)
            Configuration.getInstance().setTileFileSystemCacheTrimBytes(tileTrim)

            val imageCacheDir = filesDir.resolve("image_cache").apply { mkdirs() }
            val networkCacheDir = filesDir.resolve("network_cache").apply { mkdirs() }
            val okHttpClient = OkHttpClient.Builder()
                .cache(Cache(networkCacheDir, 100L * 1024 * 1024))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            SingletonImageLoader.setSafe { context ->
                ImageLoader.Builder(context)
                    .crossfade(150)
                    .components {
                        add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
                    }
                    .memoryCache {
                        MemoryCache.Builder()
                            .maxSizePercent(context, 0.30)
                            .maxSizeBytes(192L * 1024 * 1024)
                            .build()
                    }
                    .diskCache {
                        DiskCache.Builder()
                            .directory(imageCacheDir.absolutePath.toPath())
                            .maxSizeBytes(imageMax)
                            .build()
                    }
                    .build()
            }
        }

        safeInit("local-datastore") {
            LocalDataStore.initialize(this)
        }

        safeInit("mock-data-seeder") {
            MockDataSeeder.seedIfNeeded()
        }

        safeInit("ad-analytics") {
            AdAnalyticsTracker.initialize(this)
        }

        safeInit("affiliate-click-tracker") {
            AffiliateClickTracker.initialize(this)
        }

        safeInit("bug-logger") {
            BugLogger.initialize(this)
        }

        safeInit("dig-site-discovery-store") {
            DigSiteDiscoveryStore.initialize()
        }

        safeInit("email-composer-draft") {
            EmailComposerDraftStore.initialize(this)
        }

        safeInit("user-timezone-provider") {
            UserTimezoneProvider.initialize()
        }

        safeInit("custom-dig-location-store") {
            CustomDigLocationStore.initialize()
        }

        safeInit("specimen-submission-store") {
            SpecimenSubmissionStore.initialize()
        }

        safeInit("custom-specimen-store") {
            CustomSpecimenStore.initialize()
        }

        safeInit("location-submission-store") {
            LocationSubmissionStore.initialize()
        }

        safeInit("subscription-admin-manager") {
            SubscriptionAdminManager.initialize(this)
        }

        safeInit("purchase-manager") {
            PurchaseManager.instance.initialize(this)
        }

        safeInit("identify-access-manager") {
            IdentifyAccessManager.instance.initialize(this)
        }

        safeInit("identify-cache") {
            IdentifyCache.initialize(this)
        }

        safeInit("achievements-repository") {
            AchievementsRepository.initialize(this)
        }

        safeInit("referral-repository") {
            ReferralRepository.initialize(this)
        }

        safeInit("auth-repository") {
            AuthRepository.instance.initialize()
        }

        safeInit("notification-channels") {
            NotificationHelper.createChannels(this)
        }

        safeInit("notification-repository") {
            NotificationRepository.instance.initialize(this)
        }

        safeInit("work-scheduler") {
            WorkScheduler.schedule(this)
        }

        safeInit("nightly-sync") {
            // Only keep the nightly offline-database sync scheduled if the
            // user has explicitly enabled it in Storage settings. Uses KEEP
            // policy so this doesn't reset the schedule on every cold start.
            if (PersistenceManager.isNightlySyncEnabled()) {
                WorkScheduler.scheduleNightlySync(this)
            }
        }

        safeInit("export-cache-cleanup") {
            ExportCleanupWorker.schedule(this)
            ExportCleanupWorker.runNow(this)
        }

        safeInit("settings-backup") {
            WorkScheduler.scheduleSettingsBackup(this)
        }

        safeInit("proximity-check-now") {
            WorkScheduler.runProximityCheckNow(this)
        }

        safeInit("update-manager") {
            UpdateManager.checkForUpdate(this)
        }

        safeInit("admob") {
            MobileAds.initialize(this) {}
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED)
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .build()
            )
        }
    }

    /**
     * Checks the osmdroid SQLite tile cache database for corruption. If the DB
     * file exists but can't be opened (e.g. the app was force-killed mid-write),
     * deletes it so osmdroid recreates a fresh, empty cache on next access.
     * This prevents the "cache" error dialog from appearing after a crash.
     */
    private fun healOsmDroidTileCache() {
        val tileCacheDir = filesDir.resolve("osmdroid/tiles")
        if (!tileCacheDir.exists()) return

        val dbFile = File(tileCacheDir, "cache.db")
        if (!dbFile.exists()) return

        var db: android.database.sqlite.SQLiteDatabase? = null
        try {
            db = android.database.sqlite.SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                android.database.sqlite.SQLiteDatabase.OPEN_READONLY,
            )
            db.rawQuery("SELECT COUNT(*) FROM tiles", null).use { cursor ->
                cursor.moveToFirst()
            }
        } catch (e: Exception) {
            Log.w(startupTag, "Corrupt osmdroid tile cache detected, rebuilding", e)
            runCatching { db?.close() }
            runCatching { dbFile.delete() }
            runCatching { File(tileCacheDir, "cache.db-wal").delete() }
            runCatching { File(tileCacheDir, "cache.db-shm").delete() }
            runCatching { BugLogger.logMessage(this, "Application", "Healed corrupt osmdroid tile cache DB") }
        } finally {
            runCatching { db?.close() }
        }
    }

    /**
     * Respond to system memory pressure by clearing the Coil in-memory image
     * cache. This prevents OOM crashes on low-RAM devices when the user scrolls
     * through many specimen images or the OS needs to reclaim memory for other
     * apps. The disk cache is preserved so images reload quickly from disk.
     *
     * - TRIM_MEMORY_RUNNING_LOW / CRITICAL: clear the entire memory cache.
     * - TRIM_MEMORY_MODERATE: clear the memory cache.
     * - TRIM_MEMORY_UI_HIDDEN: clear memory cache (app went to background).
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL,
            TRIM_MEMORY_UI_HIDDEN,
            TRIM_MEMORY_MODERATE -> {
                runCatching {
                    SingletonImageLoader.get(this).memoryCache?.clear()
                }
                Log.d("RockScoutApp", "Memory cache cleared (level=$level)")
            }
        }
    }

    companion object {
        /**
         * Reconfigure both the Coil image loader and osmdroid tile cache at
         * runtime when the user toggles between Standard and Maximum cache.
         * Called from SocialSettingsScreen after the user confirms the switch.
         */
        /**
         * Permanently delete every downloaded map tile and every cached image
         * stored on-device, returning the total number of bytes freed. Used by
         * the "Clear all offline cache" button in Social Settings so users can
         * reclaim storage without uninstalling.
         *
         * Clears:
         *  - osmdroid SQLite tile cache (cache.sqlite) and any loose tile files
         *  - Coil disk image cache (specimen / field-capture / community photos)
         *  - Coil in-memory image cache
         *
         * Does NOT touch:
         *  - Saved trips, specimen markers, or the persisted "cached" flags on
         *    trips (those track whether the user *chose* to cache an area, not
         *    the actual tile bytes — tiles simply re-download next time).
         *  - User profile, collection, wishlist, or any app state.
         *
         * Best-effort: wrapped in try/catch, never crashes the caller.
         */
        fun clearAllOfflineCaches(context: android.content.Context): Long {
            var freed = 0L

            // 1) osmdroid tile cache: SQLite DB + loose tile files.
            runCatching {
                val tileCacheDir = Configuration.getInstance().osmdroidTileCache
                if (tileCacheDir != null) {
                    val dbFile = java.io.File(tileCacheDir, "cache.db")
                    if (dbFile.exists()) {
                        freed += dbFile.length()
                        // Open & drop the tiles table first so any in-progress
                        // SqlTileWriter releases its lock, then delete the file.
                        runCatching {
                            val db = android.database.sqlite.SQLiteDatabase
                                .openOrCreateDatabase(dbFile, null)
                            db.execSQL("DELETE FROM tiles;")
                            db.execSQL("VACUUM;")
                            db.close()
                        }
                        runCatching { dbFile.delete() }
                    }
                    // Also remove any -wal / -shm sidecars and archives dir.
                    runCatching { java.io.File(tileCacheDir, "cache.db-wal").delete() }
                    runCatching { java.io.File(tileCacheDir, "cache.db-shm").delete() }
                    runCatching {
                        java.io.File(tileCacheDir, "tiles").takeIf { it.exists() }?.let { dir ->
                            dir.walkBottomUp().forEach { f ->
                                freed += f.length()
                                f.delete()
                            }
                        }
                    }
                }
            }

            // 2) Coil disk cache (specimen / field-capture / community images).
            runCatching {
                val loader = coil3.SingletonImageLoader.get(context)
                loader.diskCache?.let { disk ->
                    freed += disk.size
                    disk.clear()
                }
                loader.memoryCache?.clear()
            }

            // 3) Best-effort: also wipe the image_cache directory directly in
            //    case some files are orphaned outside Coil's tracking.
            runCatching {
                val imageCacheDir = context.filesDir.resolve("image_cache")
                if (imageCacheDir.exists()) {
                    imageCacheDir.walkBottomUp().forEach { f ->
                        if (f.isFile) freed += f.length()
                        f.delete()
                    }
                    imageCacheDir.mkdirs()
                }
            }

            return freed
        }

        /**
         * Permanently delete every downloaded specimen photo and educational /
         * guide illustration cached on-device (the Coil disk + memory image
         * cache), returning the total number of bytes freed. Used by the
         * "Clear Offline Data" button in the Storage settings panel so users
         * can wipe the bulk-downloaded image set without touching map tiles.
         *
         * Clears:
         *  - Coil disk image cache (specimen / field-capture / community / guide images)
         *  - Coil in-memory image cache
         *  - Any orphaned files left in the image_cache directory
         *
         * Does NOT touch:
         *  - Cached satellite / topo map tiles (use [clearAllOfflineCaches] for that)
         *  - Saved trips, specimen markers, collection, wishlist, or any app state
         *
         * Best-effort: wrapped in try/catch, never crashes the caller.
         */
        fun clearOfflineImageData(context: android.content.Context): Long {
            var freed = 0L

            // 1) Coil disk cache (specimen / guide / field-capture images).
            runCatching {
                val loader = coil3.SingletonImageLoader.get(context)
                loader.diskCache?.let { disk ->
                    freed += disk.size
                    disk.clear()
                }
                loader.memoryCache?.clear()
            }

            // 2) Best-effort: also wipe the image_cache directory directly in
            //    case some files are orphaned outside Coil's tracking.
            runCatching {
                val imageCacheDir = context.filesDir.resolve("image_cache")
                if (imageCacheDir.exists()) {
                    imageCacheDir.walkBottomUp().forEach { f ->
                        if (f.isFile) freed += f.length()
                        f.delete()
                    }
                    imageCacheDir.mkdirs()
                }
            }

            return freed
        }

        fun reconfigureCacheSize(context: android.content.Context, mode: String) {
            val (tileMax, tileTrim, imageMax) = if (mode == "max") {
                Triple(2_000L * 1024 * 1024, 1_600L * 1024 * 1024, 2_000L * 1024 * 1024)
            } else {
                Triple(150L * 1024 * 1024, 120L * 1024 * 1024, 150L * 1024 * 1024)
            }
            Configuration.getInstance().setTileFileSystemCacheMaxBytes(tileMax)
            Configuration.getInstance().setTileFileSystemCacheTrimBytes(tileTrim)

            val imageCacheDir = context.filesDir.resolve("image_cache")
            val networkCacheDir = context.filesDir.resolve("network_cache").apply { mkdirs() }
            val okHttpClient = OkHttpClient.Builder()
                .cache(Cache(networkCacheDir, 100L * 1024 * 1024))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            SingletonImageLoader.setSafe { ctx ->
                ImageLoader.Builder(ctx)
                    .crossfade(150)
                    .components {
                        add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
                    }
                    .memoryCache {
                        MemoryCache.Builder()
                            .maxSizePercent(ctx, 0.30)
                            .maxSizeBytes(192L * 1024 * 1024)
                            .build()
                    }
                    .diskCache {
                        DiskCache.Builder()
                            .directory(imageCacheDir.absolutePath.toPath())
                            .maxSizeBytes(imageMax)
                            .build()
                    }
                    .build()
            }
        }
    }
}
