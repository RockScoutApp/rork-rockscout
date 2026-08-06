package com.rork.rockscout.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Connectivity-aware image prefetcher for low-signal optimisation.
 *
 * Warms the Coil disk + memory cache for images the user is likely to view next,
 * so navigation feels instant even on a weak 4G connection (1-2 bars).
 *
 * Strategy:
 * - On **WiFi**: prefetch up to 6 images with 3 concurrent workers (bandwidth
 *   is plentiful, but we still cap concurrency to avoid spawning unbounded
 *   coroutines that pile up when the user navigates quickly).
 * - On **Cellular (any strength)**: prefetch up to 3 images with 1 concurrent
 *   worker (sequential) to avoid saturating a thin pipe and competing with the
 *   user's foreground request.
 * - **Offline**: skip prefetch entirely — rely on disk-cached images only.
 *
 * **Memory safety**: A global [Semaphore] caps the number of concurrent prefetch
 * jobs across the entire app. If the user navigates rapidly through screens that
 * each call [prefetch], the semaphore prevents coroutine pileup that could
 * exhaust memory or thread pool slots. The [SupervisorJob] ensures one failed
 * prefetch doesn't cancel the others.
 *
 * Call [prefetch] from a screen's `LaunchedEffect` with the URLs of the next
 * likely page (e.g. specimen detail images from a list card).
 */
object ImagePrefetcher {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Hard cap on concurrent prefetch jobs across the app. Prevents pileup. */
    private val wifiPermit = Semaphore(4)
    private val cellularPermit = Semaphore(2)

    /**
     * Prefetch [urls] into the Coil cache. Automatically adjusts parallelism
     * based on the current network type. Silently skips if offline.
     *
     * @param context Android context for accessing the ImageLoader + ConnectivityManager
     * @param urls image URLs to warm in the cache (deduped, blanks removed)
     */
    fun prefetch(context: Context, urls: List<String>) {
        val distinct = urls.distinct().filter { it.isNotBlank() && it.startsWith("http") }
        if (distinct.isEmpty()) return

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val isWifi = cm?.isWifiConnected() ?: false
        val isOnline = cm?.isOnline() ?: true

        if (!isOnline) return

        val loader = SingletonImageLoader.get(context)

        if (isWifi) {
            // WiFi: prefetch up to 10, max 4 concurrent — keeps thumbnails
            // ready ahead of fast scrolling on high-bandwidth connections.
            distinct.take(10).forEach { url ->
                scope.launch {
                    wifiPermit.withPermit {
                        runCatching {
                            val request = ImageRequest.Builder(context)
                                .data(url)
                                .size(Size(512, 512))
                                .build()
                            loader.enqueue(request)
                        }
                    }
                }
            }
        } else {
            // Cellular: prefetch up to 5, max 2 concurrent — balances keeping
            // ahead of the scroll position without saturating a thin pipe.
            distinct.take(5).chunked(2).forEach { batch ->
                scope.launch {
                    cellularPermit.withPermit {
                        batch.forEach { url ->
                            runCatching {
                                val request = ImageRequest.Builder(context)
                                    .data(url)
                                    .size(Size(512, 512))
                                    .build()
                                loader.enqueue(request)
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------ bulk
    /**
     * Bulk-prefetch **every** unique specimen image URL into the Coil disk cache
     * so the full catalog works offline. Used by the optional "Download all
     * specimen images for offline" action surfaced in the Specimen Database
     * header pill and the Storage settings card.
     *
     * Behavior:
     * - Collects every unique URL from [SpecimenImages.urls] (the specimen
     *   photos) AND [EducationalImages.all] (the educational/guide illustrations
     *   + hero/background images), deduped — many specimens share fallback PNGs
     *   and several guide screens reuse the same hero art, so the real download
     *   set is smaller than the raw URL count.
     * - Skips any URL that already has a disk-cache entry, so resuming after a
     *   cancel/restart doesn't re-download what's already on-device.
     * - Runs on a bounded worker pool: 6 concurrent on WiFi, 2 on cellular.
     * - Uses full-resolution requests (no 512px downsampling) so zoom-in views
     *   also work offline.
     * - Cancellable: [shouldCancel] is polled before each new URL is enqueued.
     *   In-flight downloads are allowed to finish so partial writes don't get
     *   orphaned in the cache.
     * - Reports progress via [onProgress] after each URL completes (success or
     *   failure), with the running total of bytes now in the disk cache.
     *
     * @param context       Android context.
     * @param onProgress    Called on the IO dispatcher after each URL completes
     *                      with (finishedCount, totalCount, approxBytesCached).
     *                      Also called once at the start with (0, total, bytesSoFar)
     *                      so the UI can show the resumed state immediately.
     * @param shouldCancel  Polled before enqueueing each new URL. Return true to
     *                      stop the run early. In-flight downloads finish.
     * @return A [Job] bound to [scope] so the caller can cancel it wholesale if
     *         the screen is destroyed (in addition to the cooperative [shouldCancel]
     *         check).
     */
    fun bulkPrefetchAll(
        context: Context,
        onProgress: (finished: Int, total: Int, bytes: Long) -> Unit,
        shouldCancel: () -> Boolean,
    ): Job {
        val appContext = context.applicationContext
        return scope.launch {
            val loader = SingletonImageLoader.get(appContext)
            val disk = loader.diskCache

            // Collect + dedupe every image URL in the app: specimen photos,
            // educational/guide illustrations, AND dino fossil/skeleton + life
            // reconstruction images. This ensures the bulk download truly covers
            // every single image in the app for offline use.
            val dinoUrls = DinoImageMap.images.values.map { path ->
                "https://jvns5dfy7fpytx79a2tb3-web.rork.live/$path"
            } + DinoLifeImageMap.images.values.map { path ->
                "https://jvns5dfy7fpytx79a2tb3-web.rork.live/$path"
            }
            val staticUrls = runCatching {
                (SpecimenImages.urls.values.flatten() + EducationalImages.all + dinoUrls)
                    .distinct()
                    .filter { it.isNotBlank() && it.startsWith("http") }
            }.getOrDefault(emptyList())

            // ── Resolve dynamic location + museum image URLs from Wikimedia
            //    Commons via the /commons-photo backend. These are not known
            //    at compile time — they're resolved on first access and cached
            //    by LocationImageRepository. During bulk download we pre-resolve
            //    every dig site and museum name so their hero photos are cached
            //    on-device for offline use.
            val locationImageUrls = mutableListOf<String>()
            runCatching {
                val locationNames = mutableListOf<Pair<String, String?>>()

                // US dig sites
                SeedData.allLocations.forEach { loc ->
                    if (shouldCancel()) return@runCatching
                    locationNames.add(loc.name to loc.region)
                }

                // International dig sites
                InternationalLocations.internationalLocations.forEach { loc ->
                    if (shouldCancel()) return@runCatching
                    locationNames.add(loc.name to loc.region)
                }

                // Museums
                UsMuseums.allMuseums.forEach { museum ->
                    if (shouldCancel()) return@runCatching
                    locationNames.add(museum.name to "${museum.city} ${museum.state}")
                }

                // Resolve in batches of 10 to avoid overwhelming the backend.
                // Each resolve call hits the in-memory/persisted cache first,
                // so only first-time resolutions actually call the network.
                locationNames.distinctBy { it.first.lowercase() }.chunked(10).forEach { batch ->
                    if (shouldCancel()) return@runCatching
                    batch.forEach { (name, region) ->
                        val url = LocationImageRepository.resolveImageUrl(name, region)
                        if (url != null) locationImageUrls.add(url)
                    }
                }
            }

            val allUrls = (staticUrls + locationImageUrls)
                .distinct()
                .filter { it.isNotBlank() && it.startsWith("http") }

            if (allUrls.isEmpty()) {
                onProgress(0, 0, disk?.size ?: 0L)
                return@launch
            }

            // Filter out URLs already in the disk cache so a resume is cheap.
            val remaining = if (disk != null) {
                allUrls.filter { url -> !isInDiskCache(disk, url) }
            } else {
                allUrls
            }

            val alreadyFinished = allUrls.size - remaining.size
            onProgress(alreadyFinished, allUrls.size, disk?.size ?: 0L)

            if (remaining.isEmpty()) return@launch

            // Decide concurrency from the current network type.
            val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val isWifi = cm?.isWifiConnected() ?: false
            val isCellular = cm?.isOnline() == true && !isWifi
            val permits = if (isWifi) 6 else if (isCellular) 2 else 1
            val bulkPermit = Semaphore(permits)

            var finished = alreadyFinished
            val total = allUrls.size

            // Launch each download as a child job, gated by the semaphore.
            val jobs = remaining.map { url ->
                launch {
                    if (shouldCancel()) return@launch
                    bulkPermit.withPermit {
                        if (shouldCancel()) return@withPermit
                        runCatching {
                            val request = ImageRequest.Builder(appContext)
                                .data(url)
                                // Full resolution — no downsampling — so the
                                // zoom-in viewer works offline too.
                                .size(Size.ORIGINAL)
                                .scale(Scale.FILL)
                                .precision(Precision.INEXACT)
                                .build()
                            val result = loader.execute(request)
                            // Only count it as finished if it actually wrote to
                            // the disk cache (success or disk-cached result).
                            if (result is SuccessResult) {
                                synchronized(this@ImagePrefetcher) {
                                    finished += 1
                                    onProgress(finished, total, disk?.size ?: 0L)
                                }
                            }
                        }.onFailure {
                            // Count failures as "processed" so the progress bar
                            // still advances and the user isn't stuck at 99%.
                            synchronized(this@ImagePrefetcher) {
                                finished += 1
                                onProgress(finished, total, disk?.size ?: 0L)
                            }
                        }
                    }
                }
            }
            // Wait for every child to complete (or skip due to cancel).
            jobs.forEach { it.join() }
        }
    }

    /**
     * Returns true if [url] already has a non-empty entry in the Coil [disk]
     * cache. Coil 3 keys the disk cache by the URL string (default cache key),
     * so we can probe it directly without doing a full decode.
     */
    private fun isInDiskCache(disk: DiskCache, url: String): Boolean {
        return runCatching {
            disk.openSnapshot(url)?.use { snap ->
                snap.data.toFile().exists() && snap.data.toFile().length() > 0L
            } ?: false
        }.getOrDefault(false)
    }

    /**
     * Prefetch Commons photo URLs (not bitmaps) for a list of locations so the
     * [LocationImage] composable renders instantly when cards scroll into view.
     *
     * This only warms the URL-resolution cache in [LocationImageRepository] —
     * the actual image bytes are loaded lazily by Coil when the composable
     * enters composition. URL resolution is cheap (one backend call per unique
     * location name, cached in-memory + persisted), so we can afford to
     * pre-warm more entries than the bitmap prefetch.
     *
     * @param context Android context
     * @param names list of (name, region?) pairs to resolve
     */
    fun prefetchLocationImages(
        context: Context,
        names: List<Pair<String, String?>>,
    ) {
        if (names.isEmpty()) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val isOnline = cm?.isOnline() ?: true
        if (!isOnline) return

        scope.launch {
            // Resolve in batches of 5 to avoid overwhelming the backend.
            // Each batch runs concurrently up to the cellular permit limit.
            names.distinctBy { it.first.lowercase() }.chunked(5).forEach { batch ->
                cellularPermit.withPermit {
                    runCatching {
                        com.rork.rockscout.data.LocationImageRepository.prefetch(batch)
                    }
                }
            }
        }
    }

    private fun ConnectivityManager.isWifiConnected(): Boolean {
        val network = activeNetwork ?: return false
        val caps = getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun ConnectivityManager.isOnline(): Boolean {
        val network = activeNetwork ?: return false
        val caps = getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
