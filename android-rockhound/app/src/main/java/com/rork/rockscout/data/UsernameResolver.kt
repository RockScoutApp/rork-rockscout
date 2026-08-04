package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Ensures display names are unique across the platform by querying
 * the Supabase `rockscout_profiles` table for existing names and
 * appending a number suffix if a collision is found.
 *
 * Examples:
 *   "Rock Scout"  → "Rock Scout"     (if no collision)
 *   "Rock Scout"  → "Rock Scout2"    (if 1 collision)
 *   "Rock Scout"  → "Rock Scout3"    (if 2 collisions)
 */
object UsernameResolver {

    private const val TAG = "UsernameResolver"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val client = NetworkClient.client

    private fun baseUrl() = BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)
    private fun anonKey() = BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)
    private fun accessToken(): String? = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)

    /**
     * Query Supabase for all display names that case-insensitively match
     * [desiredName] (excluding the current user). Returns the list of
     * exact-match display names that already exist.
     */
    private suspend fun findExistingNames(desiredName: String, excludeUserId: String?): Set<String> {
        val token = accessToken() ?: return emptySet()
        return try {
            val ilikeParam = desiredName.replace("'", "''")
            val resp = client.get("${baseUrl()}/rest/v1/rockscout_profiles?display_name=ilike.${ilikeParam}&select=display_name") {
                header("apikey", anonKey())
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (!resp.status.isSuccess()) return emptySet()
            val raw = resp.body<String>()
            val arr = json.parseToJsonElement(raw).jsonArray
            arr.mapNotNull { it.jsonObject["display_name"]?.jsonPrimitive?.contentOrNull }
                .filterNot { it == excludeUserId }
                .toSet()
        } catch (e: Exception) {
            Log.w(TAG, "findExistingNames failed", e)
            emptySet()
        }
    }

    /**
     * Given a [desiredName], return a unique version by appending a number
     * suffix if the name is already taken by another user.
     *
     * @param desiredName The name the user wants.
     * @param excludeUserId The current user's ID (so they can keep their own name).
     * @return A unique display name.
     */
    suspend fun ensureUnique(desiredName: String, excludeUserId: String? = null): String {
        val trimmed = desiredName.trim()
        if (trimmed.isBlank()) return "Rockhound"

        val existing = findExistingNames(trimmed, excludeUserId)
        if (existing.isEmpty()) return trimmed

        // Check if the exact name is taken (case-insensitive)
        val isExactTaken = existing.any { it.equals(trimmed, ignoreCase = true) }
        if (!isExactTaken) return trimmed

        // Find the next available suffix number
        var suffix = 2
        while (true) {
            val candidate = "$trimmed$suffix"
            val candidateTaken = existing.any { it.equals(candidate, ignoreCase = true) }
            // Also do a fresh check for names like "RockScout2" that might exist
            // but weren't in the initial ilike query (which matches the base name)
            if (!candidateTaken) {
                val additional = findExistingNames(candidate, excludeUserId)
                if (additional.isEmpty()) return candidate
            }
            suffix++
            if (suffix > 9999) return "$trimmed-${System.currentTimeMillis() % 10000}"
        }
    }

    /**
     * Check whether a display name is already taken by another user
     * (checks both local users and Supabase profiles).
     */
    suspend fun isTaken(name: String, excludeUserId: String? = null): Boolean {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return false

        // Check local users first (fast, no network)
        if (SocialRepository.instance.isDisplayNameTaken(trimmed, excludeUserId)) {
            return true
        }

        // Check Supabase profiles
        val existing = findExistingNames(trimmed, excludeUserId)
        return existing.any { it.equals(trimmed, ignoreCase = true) }
    }
}
