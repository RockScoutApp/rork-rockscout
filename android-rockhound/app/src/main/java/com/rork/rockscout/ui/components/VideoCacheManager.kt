package com.rork.rockscout.ui.components

import android.content.Context
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * Singleton manager for a local ExoPlayer cache.
 *
 * Wraps the remote video URL with a [CacheDataSource] so that downloaded
 * segments are stored on-device. If the network drops mid-playback, already
 * cached segments play back seamlessly without re-fetching. When the network
 * recovers, the player resumes fetching from where the cache left off.
 *
 * The cache is limited to 300 MB (more than enough for the ~170 MB tutorial MKV)
 * and uses an LRU eviction policy to stay within that bound.
 */
object VideoCacheManager {

    private const val MAX_CACHE_BYTES: Long = 300L * 1024 * 1024

    @Volatile
    private var cache: SimpleCache? = null

    /**
     * Lazily initialises the on-disk cache. Must be called on a background
     * thread (ExoPlayer uses a background thread for data source creation, so
     * this is safe inside [CacheDataSource] factory lambdas).
     */
    private fun getCache(context: Context): SimpleCache {
        cache?.let { return it }
        synchronized(this) {
            cache?.let { return it }
            val cacheDir = File(context.cacheDir, "exoplayer_video_cache")
            cacheDir.mkdirs()
            cache = SimpleCache(
                cacheDir,
                LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES),
                StandaloneDatabaseProvider(context),
            )
            return cache!!
        }
    }

    /**
     * Builds a [CacheDataSource] that reads from the local cache first and
     * falls back to the network for uncached segments. Writes are enabled so
     * every fetched byte is stored for future use.
     */
    fun buildCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val upstreamFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(context, "RockScout"))
            .setConnectTimeoutMs(8_000)
            .setReadTimeoutMs(15_000)
            .setAllowCrossProtocolRedirects(true)

        val simpleCache = getCache(context)

        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    /**
     * Releases the cache. Call when the player is permanently destroyed to
     * release the database provider and file handles.
     */
    fun release() {
        synchronized(this) {
            cache?.release()
            cache = null
        }
    }
}
