package com.rork.rockscout.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Result of an image moderation scan.
 *
 * @param allowed true if the image passed all checks and may be used.
 * @param reason human-readable explanation when [allowed] is false.
 */
data class ModerationResult(
    val allowed: Boolean,
    val reason: String = "",
    val triState: ModerationTriState = if (allowed) ModerationTriState.CLEAN else ModerationTriState.EXPLICIT,
)

/** Three-way moderation verdict for images that need human review. */
enum class ModerationTriState {
    CLEAN,
    EXPLICIT,
    QUESTIONABLE,
}

/**
 * Client-side image content moderator.
 *
 * Scans a base64-encoded photo for two policy violations before the image is
 * accepted into the field-capture / identify flow:
 *
 * 1. **Sexually explicit content** — nudity, sexual acts, or suggestive imagery.
 * 2. **Profanity in the image** — offensive words rendered in the photo (e.g.
 *    on a sign, shirt, or handwritten label). The words "hell" and "damn" are
 *    explicitly allowed per app policy, matching [ProfanityFilter].
 *
 * Uses the Rork AI proxy (OpenAI-compatible chat completions endpoint) with a
 * vision-capable model. The scan runs on a background thread and returns a
 * [ModerationResult]; callers should block the capture when [allowed] is false
 * and surface [reason] to the user.
 */
object ImageModerator {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = NetworkClient.client

    @Serializable
    private data class ModerationResponse(
        val choices: List<Choice> = emptyList(),
    )

    @Serializable
    private data class Choice(
        val message: Message? = null,
    )

    @Serializable
    private data class Message(
        val content: String? = null,
    )

    /**
     * Scan [imageBase64] (raw base64, no data-URI prefix) for policy violations.
     * Returns [ModerationResult] — never throws; on any error the image is
     * allowed through (fail-open) so moderation hiccups never block legit use.
     */
    suspend fun scan(imageBase64: String, mimeType: String = "image/jpeg"): ModerationResult =
        withContext(Dispatchers.IO) {
            val allVals = runCatching { com.rork.rockscout.Config.allValues }.getOrDefault(emptyMap())
            val toolkitUrl = allVals["EXPO_PUBLIC_TOOLKIT_URL"] ?: ""
            val secret = allVals["EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY"] ?: ""
            if (toolkitUrl.isBlank() || secret.isBlank()) {
                // No toolkit configured — fail open.
                return@withContext ModerationResult(allowed = true)
            }

            val systemPrompt = "You are a strict content moderator for a family-friendly rock and mineral identification app. " +
                "You examine photos and decide whether they violate the app's content policy. " +
                "You respond with ONLY a compact JSON object — no markdown, no explanation."

            val userPrompt = """Examine this image carefully and check for two policy violations:

1. SEXUALLY EXPLICIT CONTENT — nudity, partial nudity, sexual acts, or sexually suggestive imagery.
2. PROFANITY IN THE IMAGE — offensive or vulgar words visible in the photo (printed on a sign, shirt, label, handwritten note, etc.).

IMPORTANT EXCEPTIONS — these are ALLOWED and must NOT be flagged:
- The words "hell" and "damn" are acceptable.
- Rocks, minerals, crystals, fossils, and natural earth formations are always fine.

Respond with ONLY this JSON shape:
{"verdict": "clean", "reason": ""}

If the image is clean, verdict="clean" and reason="".
If the image clearly contains sexually explicit content, verdict="explicit" and reason="This photo contains sexually explicit content and cannot be used."
If the image clearly contains profanity (other than hell/damn), verdict="explicit" and reason="This photo contains profanity and cannot be used."
If the image is borderline or questionable (might contain explicit content or profanity but you are not certain), verdict="questionable" and reason="Image flagged for manual review."
Be conservative — when in doubt between clean and questionable, prefer clean. Only use "explicit" for clear violations."""

            // Use the OpenAI-compatible chat completions endpoint with a vision model.
            val proxyUrl = "$toolkitUrl/v2/vercel/v1/chat/completions"
            val bodyPayload = buildJsonObject {
                put("model", "anthropic/claude-haiku-4.5")
                put("max_tokens", 256)
                put("temperature", 0.0)
                put(
                    "messages",
                    kotlinx.serialization.json.JsonArray(
                        listOf(
                            buildJsonObject {
                                put("role", "system")
                                put("content", systemPrompt)
                            },
                            buildJsonObject {
                                put(
                                    "role",
                                    "user",
                                )
                                put(
                                    "content",
                                    kotlinx.serialization.json.JsonArray(
                                        listOf(
                                            buildJsonObject {
                                                put("type", "image_url")
                                                put(
                                                    "image_url",
                                                    buildJsonObject {
                                                        put("url", "data:$mimeType;base64,$imageBase64")
                                                    },
                                                )
                                            },
                                            buildJsonObject {
                                                put("type", "text")
                                                put("text", userPrompt)
                                            },
                                        ),
                                    ),
                                )
                            },
                        ),
                    ),
                )
            }.toString()

            try {
                val response = client.post(proxyUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(bodyPayload)
                    headers.append("Authorization", "Bearer $secret")
                }
                val raw: String = response.body()
                val parsed = json.decodeFromString(ModerationResponse.serializer(), raw)
                val content = parsed.choices.firstOrNull()?.message?.content ?: return@withContext ModerationResult(allowed = true)
                parseVerdict(content)
            } catch (e: Exception) {
                // Fail open on any error — never block legit identification.
                ModerationResult(allowed = true)
            }
        }

    /**
     * Parse the model's JSON verdict. Tolerant of markdown fences or extra text.
     */
    private fun parseVerdict(content: String): ModerationResult {
        var s = content.trim()
        // Strip markdown code fences if present.
        val fenceMatch = Regex("```(?:json)?\\s*([\\s\\S]*?)```").find(s)
        if (fenceMatch != null) s = fenceMatch.groupValues[1].trim()
        // Extract the first {...} block.
        val first = s.indexOf('{')
        val last = s.lastIndexOf('}')
        if (first in 0..<last) s = s.substring(first, last + 1)
        return try {
            val obj = json.parseToJsonElement(s) as JsonObject
            val verdictStr = obj["verdict"]?.toString()?.trim('"') ?: ""
            val reason = obj["reason"]?.toString()?.trim('"') ?: ""
            when (verdictStr.lowercase()) {
                "explicit" -> ModerationResult(allowed = false, reason = reason, triState = ModerationTriState.EXPLICIT)
                "questionable" -> ModerationResult(allowed = false, reason = reason, triState = ModerationTriState.QUESTIONABLE)
                else -> ModerationResult(allowed = true, reason = "", triState = ModerationTriState.CLEAN)
            }
        } catch (_: Exception) {
            // Unparseable — fail open.
            ModerationResult(allowed = true)
        }
    }
}
