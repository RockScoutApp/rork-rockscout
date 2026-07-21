package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import android.util.Log

/**
 * Shared HttpClient singleton optimized for low-signal / unreliable cellular connections.
 *
 * Features:
 * - **Connection pooling** — reuses TCP connections across requests (keep-alive),
 *   cutting handshake overhead on repeated calls to the same backend.
 * - **Reasonable timeouts** — 15s connect, 20s socket, 30s overall request.
 *   Prevents indefinite hangs on degraded connections while still allowing large
 *   payloads (e.g. base64 image uploads) to complete on slow networks.
 * - **Exponential-backoff retry** — up to 3 retries on transient failures
 *   (IO exceptions or HTTP 5xx). Backoff starts at 500ms and doubles each attempt.
 *   This dramatically improves success rate on flaky 4G / 1-2 bar connections
 *   where a single dropped packet would otherwise kill the request permanently.
 * - **Single shared instance** — all API objects use [client] instead of
 *   creating their own HttpClient, maximising connection reuse and
 *   minimising memory footprint.
 */
object NetworkClient {

    private const val TAG = "NetworkClient"

    /** Shared HTTP client — use this everywhere instead of creating new instances. */
    val client: HttpClient = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 20_000
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                response.status.value in 500..599
            }
            retryOnExceptionIf { _, cause ->
                cause is java.io.IOException
            }
            exponentialDelay()
        }
    }
}
