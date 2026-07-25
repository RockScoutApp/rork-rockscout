package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class IdentifyRequest(
    val imageBase64: String,
    val mimeType: String = "image/jpeg",
    /** Caller's entitlement tier — drives the accuracy ladder on the backend.
     *  "free" = Haiku only, "premium" = Haiku + Sonnet re-rank on ambiguous,
     *  "pro" = Haiku + Sonnet + Gemini third opinion on the hardest cases. */
    val entitlement: String = "free",
)

@Serializable
data class IdentifyMatch(
    val id: String,
    val name: String,
    val confidence: Int,
    val reasoning: String,
)

@Serializable
data class ClarificationQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
)

@Serializable
data class WebReference(
    val title: String = "",
    val url: String = "",
    val snippet: String = "",
    val source: String = "",
)

@Serializable
data class AssemblageComponent(
    val name: String = "",
    val percentage: Int = 0,
    val evidence: String = "",
)

@Serializable
data class AssemblageResult(
    val hostRock: String = "",
    val components: List<AssemblageComponent> = emptyList(),
    val summary: String = "",
)

@Serializable
data class IdentifyResponse(
    val matches: List<IdentifyMatch> = emptyList(),
    val summary: String = "",
    val needsClarification: Boolean = false,
    val clarificationQuestions: List<ClarificationQuestion> = emptyList(),
    val webReferences: List<WebReference> = emptyList(),
    val error: String? = null,
    /** Which AI models contributed to this result (Phase 8 accuracy ladder). */
    val modelsUsed: List<String> = emptyList(),
    /** Assemblage analysis — present when the specimen is a multi-mineral assemblage. */
    val assemblage: AssemblageResult? = null,
)

@Serializable
data class ClarifyAnswer(
    val questionId: String,
    val answer: String,
)

@Serializable
data class ClarifyRequest(
    val imageBase64: String,
    val mimeType: String = "image/jpeg",
    val answers: Map<String, String>,
    val preliminaryMatches: List<IdentifyMatch>,
    val summary: String,
)

/**
 * Calls the Cloudflare Worker backend to identify a specimen from a base64-encoded photo.
 * Uses the Rork AI proxy with Claude Sonnet 5 for vision analysis.
 * When top confidence is below 85%, the backend returns clarification questions
 * and web search references for cross-referencing.
 */
object IdentifyApi {
    // The backend URL — read from Config so it works across environments.
    private val BASE_URL: String =
        com.rork.rockscout.Config.allValues["EXPO_PUBLIC_RORK_FUNCTIONS_URL"]
            ?.removeSuffix("/")
            ?: "https://rockscout-finder-backend.rork.app"
    // App key for backend auth — sent as X-App-Key header.
    private val APP_KEY: String =
        com.rork.rockscout.Config.allValues["EXPO_PUBLIC_RORK_APP_KEY"] ?: ""

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Dedicated HTTP client for identification calls. The backend may chain
     * up to 6 sequential AI model calls (Haiku + Sonnet + Gemini + questions
     * + web search + assemblage) for premium/pro users, which can take 60-90
     * seconds. The shared NetworkClient has a 30s request timeout that is too
     * short for these multi-model calls.
     *
     * Configuration:
     * - 120s request + socket timeout (allows multi-model chains to complete)
     * - 2 retries on IOException only (network drops), NOT on 5xx (retrying a
     *   failed AI identification wastes 2+ minutes and will likely fail again)
     */
    private val identifyClient = HttpClient {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 120_000
            socketTimeoutMillis = 120_000
        }

        install(HttpRequestRetry) {
            maxRetries = 2
            retryOnExceptionIf { _, cause ->
                cause is java.io.IOException
            }
            exponentialDelay()
        }
    }

    suspend fun identify(
        imageBase64: String,
        mimeType: String = "image/jpeg",
        entitlement: String = "free",
    ): IdentifyResponse {
        val response = identifyClient.post("$BASE_URL/identify") {
            contentType(ContentType.Application.Json)
            if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
            setBody(
                json.encodeToString(
                    IdentifyRequest.serializer(),
                    IdentifyRequest(
                        imageBase64 = imageBase64,
                        mimeType = mimeType,
                        entitlement = entitlement,
                    ),
                )
            )
        }

        val body = response.body<String>()
        return try {
            json.decodeFromString(IdentifyResponse.serializer(), body)
        } catch (e: Exception) {
            IdentifyResponse(error = "Failed to parse identification results: ${e.message}")
        }
    }

    /**
     * Sends user answers to clarification questions along with the original photo
     * and preliminary matches to get refined identification results.
     */
    suspend fun clarify(
        imageBase64: String,
        mimeType: String,
        answers: Map<String, String>,
        preliminaryMatches: List<IdentifyMatch>,
        summary: String,
    ): IdentifyResponse {
        val response = identifyClient.post("$BASE_URL/identify/clarify") {
            contentType(ContentType.Application.Json)
            if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
            setBody(
                json.encodeToString(
                    ClarifyRequest.serializer(),
                    ClarifyRequest(
                        imageBase64 = imageBase64,
                        mimeType = mimeType,
                        answers = answers,
                        preliminaryMatches = preliminaryMatches,
                        summary = summary,
                    ),
                )
            )
        }

        val body = response.body<String>()
        return try {
            json.decodeFromString(IdentifyResponse.serializer(), body)
        } catch (e: Exception) {
            IdentifyResponse(error = "Failed to parse clarification results: ${e.message}")
        }
    }
}
