package com.rork.rockscout.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

/**
 * Manages the 3-device limit for Premium subscribers.
 *
 * On sign-in, upserts this device into the Supabase `rockscout_installed_devices`
 * table. On every launch, queries all devices for the user ordered by
 * `installed_at` ascending. If the current device is at index 3+ (0-based),
 * [deviceOverLimit] is set to true, which blocks premium features via
 * [PurchaseManager.effectiveIsPremium].
 *
 * - FORCE_PREMIUM builds skip all checks.
 * - Non-premium users still register their device (for tracking) but
 *   [deviceOverLimit] stays false.
 * - Offline / network failure: [deviceOverLimit] defaults to false — a network
 *   error never blocks a paying user.
 */
object DeviceManager {

    private const val TAG = "DeviceManager"
    private const val MAX_DEVICES = 3

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var appContext: Context

    private val _deviceOverLimit = MutableStateFlow(false)
    val deviceOverLimit: StateFlow<Boolean> = _deviceOverLimit.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceInfo>>(emptyList())
    val devices: StateFlow<List<DeviceInfo>> = _devices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = NetworkClient.client

    private fun baseUrl(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)

    private fun anonKey(): String =
        BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)

    private fun accessToken(): String? =
        LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)

    /** Initialize with app context. Call from Application.onCreate(). */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /** Stable device fingerprint using ANDROID_ID. */
    fun getDeviceFingerprint(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun fingerprint(): String {
        return if (::appContext.isInitialized) getDeviceFingerprint(appContext) else "unknown"
    }

    /** Human-readable device label, e.g. "Pixel 8 Pro" or "Samsung Galaxy S24". */
    private fun getDeviceLabel(): String {
        val manufacturer = Build.MANUFACTURER?.replaceFirstChar { it.uppercase() } ?: ""
        val model = Build.MODEL ?: ""
        return if (model.startsWith(manufacturer, ignoreCase = true)) {
            model.replaceFirstChar { it.uppercase() }
        } else {
            "$manufacturer $model".trim().replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Upsert this device into `rockscout_installed_devices`.
     * Called after successful sign-in / session restore.
     */
    suspend fun registerDevice(context: Context, userId: String) {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) return
        if (userId.isBlank()) return
        registerDeviceInternal(userId, getDeviceFingerprint(context))
    }

    /** Register using the stored app context (no Context parameter needed). */
    suspend fun registerDevice(userId: String) {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) return
        if (userId.isBlank()) return
        if (!::appContext.isInitialized) return
        registerDeviceInternal(userId, fingerprint())
    }

    private suspend fun registerDeviceInternal(userId: String, fingerprint: String) {
        val token = accessToken() ?: return
        val label = getDeviceLabel()

        try {
            val body = json.encodeToString(
                UpsertRequest.serializer(),
                UpsertRequest(
                    user_id = userId,
                    device_fingerprint = fingerprint,
                    device_label = label,
                    device_platform = "android",
                    user_agent = "Android ${Build.VERSION.RELEASE} / ${Build.MODEL}",
                    last_seen_at = nowIso(),
                ),
            )

            val response = client.post("${baseUrl()}/rest/v1/rockscout_installed_devices?on_conflict=user_id,device_fingerprint") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                header("apikey", anonKey())
                header("Prefer", "resolution=merge-duplicates,return=minimal")
                setBody(body)
            }

            if (response.status.isSuccess()) {
                Log.i(TAG, "Device registered: $label ($fingerprint)")
            } else {
                Log.w(TAG, "Device register failed: ${response.status}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Device register error: ${e.message}")
        }
    }

    /**
     * Query all devices for the user and check if this device is over the limit.
     * Sets [deviceOverLimit] to true only when the server confirms the device
     * is at index 3+ in the installed_at ascending ordering.
     */
    suspend fun checkDeviceAccess(context: Context, userId: String) {
        checkDeviceAccessInternal(userId, getDeviceFingerprint(context))
    }

    /** Check using the stored app context (no Context parameter needed). */
    suspend fun checkDeviceAccess(userId: String) {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
            _deviceOverLimit.value = false
            return
        }
        if (!::appContext.isInitialized) return
        checkDeviceAccessInternal(userId, fingerprint())
    }

    private suspend fun checkDeviceAccessInternal(userId: String, fingerprint: String) {
        if (userId.isBlank()) return

        val token = accessToken() ?: run {
            _deviceOverLimit.value = false
            return
        }

        _isLoading.value = true
        try {
            val response = client.get("${baseUrl()}/rest/v1/rockscout_installed_devices") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header("apikey", anonKey())
                url.parameters.append("user_id", "eq.$userId")
                url.parameters.append("order", "installed_at.asc")
                url.parameters.append("select", "id,device_fingerprint,device_label,device_platform,installed_at,last_seen_at")
            }

            if (!response.status.isSuccess()) {
                Log.w(TAG, "Device check failed: ${response.status}")
                _deviceOverLimit.value = false
                return
            }

            val bodyText = response.body<String>()
            val deviceList = json.decodeFromString(
                ListSerializer(DeviceInfo.serializer()),
                bodyText,
            )

            _devices.value = deviceList

            val myIndex = deviceList.indexOfFirst { it.device_fingerprint == fingerprint }
            if (myIndex >= MAX_DEVICES) {
                _deviceOverLimit.value = true
                IdentifyAccessManager.instance.setDeviceOverLimit(true)
                Log.i(TAG, "Device over limit: index=$myIndex, max=$MAX_DEVICES")
            } else {
                _deviceOverLimit.value = false
                IdentifyAccessManager.instance.setDeviceOverLimit(false)
                Log.i(TAG, "Device within limit: index=$myIndex, max=$MAX_DEVICES")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Device check error: ${e.message} — defaulting to not blocked")
            _deviceOverLimit.value = false
            IdentifyAccessManager.instance.setDeviceOverLimit(false)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Refresh the device list without changing [deviceOverLimit].
     * Used by the Manage Devices screen.
     */
    suspend fun refreshDevices(userId: String) {
        if (userId.isBlank()) return
        val token = accessToken() ?: return

        try {
            val response = client.get("${baseUrl()}/rest/v1/rockscout_installed_devices") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header("apikey", anonKey())
                url.parameters.append("user_id", "eq.$userId")
                url.parameters.append("order", "installed_at.asc")
                url.parameters.append("select", "id,device_fingerprint,device_label,device_platform,installed_at,last_seen_at")
            }

            if (response.status.isSuccess()) {
                val bodyText = response.body<String>()
                val deviceList = json.decodeFromString(
                    ListSerializer(DeviceInfo.serializer()),
                    bodyText,
                )
                _devices.value = deviceList
            }
        } catch (e: Exception) {
            Log.w(TAG, "Refresh devices error: ${e.message}")
        }
    }

    /**
     * Remove a device by its row ID. Used by the Manage Devices screen.
     * Returns true if the deletion succeeded.
     */
    suspend fun removeDevice(deviceId: String): Boolean {
        val token = accessToken() ?: return false
        return try {
            val response = client.delete("${baseUrl()}/rest/v1/rockscout_installed_devices") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header("apikey", anonKey())
                url.parameters.append("id", "eq.$deviceId")
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.w(TAG, "Remove device error: ${e.message}")
            false
        }
    }

    /** Reset state on sign-out. */
    fun reset() {
        _deviceOverLimit.value = false
        IdentifyAccessManager.instance.setDeviceOverLimit(false)
        _devices.value = emptyList()
        _isLoading.value = false
    }

    private fun nowIso(): String =
        java.time.Instant.now().toString()

    @Serializable
    private data class UpsertRequest(
        val user_id: String,
        val device_fingerprint: String,
        val device_label: String,
        val device_platform: String,
        val user_agent: String,
        val last_seen_at: String,
    )

    @Serializable
    data class DeviceInfo(
        val id: String,
        val device_fingerprint: String,
        val device_label: String? = null,
        val device_platform: String? = null,
        val installed_at: String? = null,
        val last_seen_at: String? = null,
    )
}
