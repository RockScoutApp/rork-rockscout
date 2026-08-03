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

/** A single angle photo captured by the user. Used in the 3-angle capture
 *  flow — the user takes 1-3 photos (top, side, bottom) with optional
 *  per-angle descriptions. */
@Serializable
data class AngleImage(
    val imageBase64: String,
    val mimeType: String = "image/jpeg",
    val angle: String,
    val description: String = "",
)

@Serializable
data class IdentifyRequest(
    /** Legacy single-image field — kept for backward compatibility.
     *  If `images` is non-empty, this is ignored by the backend. */
    val imageBase64: String = "",
    val mimeType: String = "image/jpeg",
    val entitlement: String = "free",
    val searchMode: String = "rocks",
    /** Multi-angle images array — 1-3 photos with per-angle descriptions.
     *  When non-empty, the backend uses this instead of `imageBase64`. */
    val images: List<AngleImage> = emptyList(),
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
    val modelsUsed: List<String> = emptyList(),
    val assemblage: AssemblageResult? = null,
    val uncertainArtifact: Boolean = false,
)

@Serializable
data class ClarifyAnswer(
    val questionId: String,
    val answer: String,
)

@Serializable
data class ClarifyRequest(
    /** Legacy single-image field — kept for backward compatibility. */
    val imageBase64: String = "",
    val mimeType: String = "image/jpeg",
    val answers: Map<String, String>,
    val preliminaryMatches: List<IdentifyMatch>,
    val summary: String,
    /** Multi-angle images for the clarify re-rank — allows the backend
     *  to re-examine all viewpoints with the user's answers. */
    val images: List<AngleImage> = emptyList(),
)

@Serializable
data class ArtifactDetectionResult(
    val isArtifact: Boolean = false,
    val confidence: Int = 0,
    val error: String? = null,
)

/**
 * Calls the Cloudflare Worker backend to identify a specimen from photos.
 * Supports the 3-angle capture flow: the client sends 1-3 photos (top,
 * side, bottom) with optional per-angle descriptions.
 */
object IdentifyApi {
    private val BASE_URL: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_FUNCTIONS_URL", BuildSecrets.RORK_FUNCTIONS_URL)
            .removeSuffix("/")
    private val APP_KEY: String =
        BuildSecrets.resolve("EXPO_PUBLIC_RORK_APP_KEY", BuildSecrets.RORK_APP_KEY)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Dedicated HTTP client for identification calls. The backend may chain
     * up to 5 sequential AI model calls (Haiku describe+detect + Haiku visual
     * + Sonnet re-rank + Gemini third opinion + clarification) for premium
     * users, which can take 45-90 seconds.
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

    /**
     * Multi-angle identify call. Sends 1-3 photos with per-angle descriptions
     * to the backend. The backend runs the full pipeline: combined describe+
     * artifact detection → embedding search → web search → Haiku visual →
     * Sonnet re-rank (premium) → Gemini third opinion (on disagreement).
     *
     * @param angleImages List of 1-3 AngleImage objects (top/side/bottom)
     * @param entitlement "free" or "premium"
     * @param searchMode "rocks" or "artifacts"
     */
    suspend fun identifyMultiAngle(
        angleImages: List<AngleImage>,
        entitlement: String = "free",
        searchMode: String = "rocks",
    ): IdentifyResponse {
        val response = identifyClient.post("$BASE_URL/identify") {
            contentType(ContentType.Application.Json)
            if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
            setBody(
                json.encodeToString(
                    IdentifyRequest.serializer(),
                    IdentifyRequest(
                        images = angleImages,
                        entitlement = entitlement,
                        searchMode = searchMode,
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
     * Legacy single-image identify call — kept for backward compatibility.
     * Prefer [identifyMultiAngle] for the 3-angle capture flow.
     */
    suspend fun identify(
        imageBase64: String,
        mimeType: String = "image/jpeg",
        entitlement: String = "free",
        searchMode: String = "rocks",
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
                        searchMode = searchMode,
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
     * Sends user answers to clarification questions along with the original
     * angle photos and preliminary matches to get refined identification
     * results. Now supports multi-angle images.
     */
    suspend fun clarify(
        angleImages: List<AngleImage>,
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
                        images = angleImages,
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

    /**
     * Legacy single-image clarify call — kept for backward compatibility.
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

    /**
     * Lightweight artifact-detection pre-pass (Haiku-only, fast, cheap).
     * DEPRECATED — artifact detection is now handled internally by the main
     * /identify endpoint via the combined describe+detect Haiku call.
     * Kept for backward compatibility but no longer called from the UI.
     */
    suspend fun detectArtifact(imageBase64: String, mimeType: String = "image/jpeg"): ArtifactDetectionResult {
        return try {
            val response = identifyClient.post("$BASE_URL/identify/artifact-detect") {
                contentType(ContentType.Application.Json)
                if (APP_KEY.isNotBlank()) header("X-App-Key", APP_KEY)
                setBody(
                    json.encodeToString(
                        IdentifyRequest.serializer(),
                        IdentifyRequest(
                            imageBase64 = imageBase64,
                            mimeType = mimeType,
                            entitlement = "free",
                            searchMode = "detect-artifact",
                        ),
                    )
                )
            }
            val body = response.body<String>()
            json.decodeFromString(ArtifactDetectionResult.serializer(), body)
        } catch (e: Exception) {
            ArtifactDetectionResult(isArtifact = false, confidence = 0)
        }
    }
}
