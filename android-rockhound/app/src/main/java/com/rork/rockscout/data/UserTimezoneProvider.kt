package com.rork.rockscout.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.TimeZone

/**
 * Resolves and caches the user's effective [TimeZone] so every displayed
 * timestamp is accurate to the user's actual timezone — not just the device's
 * possibly-wrong system timezone.
 *
 * Resolution priority:
 * 1. Fresh (<24h) cached GPS timezone (from Open-Meteo lookup)
 * 2. Profile region mapped to an IANA timezone via [profileToTimezone]
 * 3. [TimeZone.getDefault] as a last-resort fallback
 *
 * The cached GPS timezone is refreshed on app cold-start (if location
 * permission is granted) and as a free byproduct of every weather fetch
 * via [updateFromWeather].
 */
object UserTimezoneProvider {

    private const val PREFS_KEY_GPS_TZ = "cached_gps_timezone"
    private const val PREFS_KEY_GPS_TZ_AT = "cached_gps_timezone_at"
    private const val CACHE_TTL_MS = 24L * 60 * 60 * 1000 // 24 hours

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val _effectiveTimeZone = MutableStateFlow(TimeZone.getDefault())
    val effectiveTimeZone: StateFlow<TimeZone> = _effectiveTimeZone.asStateFlow()

    /** Effective [java.time.ZoneId] for APIs that need it (e.g. ZonedDateTime). */
    val effectiveZoneId: java.time.ZoneId
        get() = _effectiveTimeZone.value.toZoneId()

    private var initialized = false

    /** Must be called once on app startup (from RockScoutApplication.onCreate). */
    fun initialize() {
        if (initialized) return
        initialized = true
        resolveImmediate()
    }

    /**
     * Synchronous resolution — checks the cached GPS timezone first, then
     * the profile region, then the device default. Does not make network
     * calls. Call [refreshFromGps] for the async GPS-based lookup.
     */
    fun resolveImmediate() {
        // (1) Fresh cached GPS timezone
        val cachedTzId = LocalDataStore.getString(PREFS_KEY_GPS_TZ)
        val cachedAt = LocalDataStore.getString(PREFS_KEY_GPS_TZ_AT)?.toLongOrNull() ?: 0L
        val now = System.currentTimeMillis()
        if (!cachedTzId.isNullOrBlank() && cachedAt > 0 && (now - cachedAt) < CACHE_TTL_MS) {
            val tz = runCatching { TimeZone.getTimeZone(cachedTzId) }.getOrNull()
            if (tz != null) {
                _effectiveTimeZone.value = tz
                return
            }
        }
        // (2) Profile region
        val profileTz = profileToTimezone(AppRepository.instance.profile.value.homeRegion)
        if (profileTz != null) {
            _effectiveTimeZone.value = profileTz
            return
        }
        // (3) Device default
        _effectiveTimeZone.value = TimeZone.getDefault()
    }

    /**
     * Map a parsed "State, Country" or "Country" home region string to an
     * IANA timezone. Returns null for unmapped regions (caller falls back).
     */
    fun profileToTimezone(homeRegion: String): TimeZone? {
        val (subdivision, country) = RegionData.parse(homeRegion)
        if (country.isNullOrBlank()) return null

        // First try country + subdivision for split-zone countries
        if (subdivision != null) {
            subDivisionToTimezone(subdivision, country)?.let { return it }
        }
        // Fall back to country-level primary zone
        return countryToTimezone(country)
    }

    /**
     * Async GPS-based timezone lookup via Open-Meteo's forecast API.
     * Caches the result with a 24-hour TTL. Safe to call from any coroutine.
     */
    suspend fun refreshFromGps(lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        try {
            val tzId = gpsToTimezone(lat, lng) ?: return@withContext
            // Cache it
            LocalDataStore.setString(PREFS_KEY_GPS_TZ, tzId)
            LocalDataStore.setString(PREFS_KEY_GPS_TZ_AT, System.currentTimeMillis().toString())
            // Update effective zone immediately
            val tz = TimeZone.getTimeZone(tzId)
            _effectiveTimeZone.value = tz
        } catch (_: Throwable) {
            // Silent failure — fall back to existing resolution
        }
    }

    /**
     * Side-channel from [WeatherRepository] — every weather fetch already
     * receives the IANA timezone string from Open-Meteo. Push it here as a
     * free byproduct so the zone stays fresh with zero extra network calls.
     */
    fun updateFromWeather(timezoneId: String, utcOffsetSeconds: Int) {
        if (timezoneId.isBlank()) return
        // Only update if it's a valid, non-GMT fallback
        val tz = runCatching { TimeZone.getTimeZone(timezoneId) }.getOrNull() ?: return
        if (tz.id == "GMT" && timezoneId != "GMT" && timezoneId != "Etc/UTC") {
            // getTimeZone falls back to GMT for unknown IDs — skip
            return
        }
        LocalDataStore.setString(PREFS_KEY_GPS_TZ, timezoneId)
        LocalDataStore.setString(PREFS_KEY_GPS_TZ_AT, System.currentTimeMillis().toString())
        _effectiveTimeZone.value = tz
    }

    /**
     * Called when the user changes their home region. Re-resolves from
     * the new region immediately (unless a fresh GPS cache overrides it).
     */
    fun onHomeRegionChanged() {
        val cachedAt = LocalDataStore.getString(PREFS_KEY_GPS_TZ_AT)?.toLongOrNull() ?: 0L
        val now = System.currentTimeMillis()
        // If GPS cache is fresh, keep it — GPS is more accurate than profile
        if (cachedAt > 0 && (now - cachedAt) < CACHE_TTL_MS) return
        // Otherwise re-resolve from the new profile region
        val profileTz = profileToTimezone(AppRepository.instance.profile.value.homeRegion)
        if (profileTz != null) {
            _effectiveTimeZone.value = profileTz
        }
    }

    // ── Region → IANA timezone mapping ────────────────────────────────────

    /**
     * Calls Open-Meteo with minimal params and reads the `timezone` IANA name.
     * Returns null on any failure.
     */
    private suspend fun gpsToTimezone(lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        try {
            val raw = NetworkClient.client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", "%.4f".format(lat))
                parameter("longitude", "%.4f".format(lng))
                parameter("current", "temperature_2m")
                parameter("timezone", "auto")
                parameter("forecast_days", "1")
            }.body<String>()
            val resp = json.decodeFromString(TzResponse.serializer(), raw)
            resp.timezone.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }
    }

    @Serializable
    private data class TzResponse(
        val timezone: String = "",
        @kotlinx.serialization.SerialName("utc_offset_seconds")
        val utcOffsetSeconds: Int = 0,
    )

    /** Country short name → primary IANA timezone. */
    private fun countryToTimezone(country: String): TimeZone? {
        val zoneId = when (country) {
            "USA" -> "America/New_York"
            "Canada" -> "America/Toronto"
            "Australia" -> "Australia/Sydney"
            "UK" -> "Europe/London"
            "Mexico" -> "America/Mexico_City"
            "Germany" -> "Europe/Berlin"
            "France" -> "Europe/Paris"
            "Italy" -> "Europe/Rome"
            "Spain" -> "Europe/Madrid"
            "Brazil" -> "America/Sao_Paulo"
            "Argentina" -> "America/Argentina/Buenos_Aires"
            "South Africa" -> "Africa/Johannesburg"
            "India" -> "Asia/Kolkata"
            "China" -> "Asia/Shanghai"
            "Japan" -> "Asia/Tokyo"
            "New Zealand" -> "Pacific/Auckland"
            "Ireland" -> "Europe/Dublin"
            "Norway" -> "Europe/Oslo"
            "Sweden" -> "Europe/Stockholm"
            "Finland" -> "Europe/Helsinki"
            "Portugal" -> "Europe/Lisbon"
            "Netherlands" -> "Europe/Amsterdam"
            "Switzerland" -> "Europe/Zurich"
            "Austria" -> "Europe/Vienna"
            "Poland" -> "Europe/Warsaw"
            "Czech Republic" -> "Europe/Prague"
            "Russia" -> "Europe/Moscow"
            else -> return null
        }
        return TimeZone.getTimeZone(zoneId)
    }

    /**
     * Subdivision → IANA timezone for split-zone countries.
     * Returns null if the country has no split zones or the subdivision
     * is not in the lookup table (caller falls back to country-level zone).
     */
    private fun subDivisionToTimezone(subdivision: String, country: String): TimeZone? {
        return when (country) {
            "USA" -> usStateToTimezone(subdivision)
            "Canada" -> canadianProvinceToTimezone(subdivision)
            "Australia" -> australianStateToTimezone(subdivision)
            "Brazil" -> brazilianStateToTimezone(subdivision)
            "Mexico" -> mexicanStateToTimezone(subdivision)
            "Russia" -> russianRegionToTimezone(subdivision)
            else -> null
        }
    }

    private fun usStateToTimezone(state: String): TimeZone? {
        val zoneId = when (state) {
            "Eastern time zones" -> "America/New_York"
            // Eastern
            "Connecticut", "Delaware", "District of Columbia", "Florida",
            "Georgia", "Maine", "Maryland", "Massachusetts", "New Hampshire",
            "New Jersey", "New York", "North Carolina", "Ohio", "Pennsylvania",
            "Rhode Island", "South Carolina", "Vermont", "Virginia",
            "West Virginia", "Puerto Rico",
            -> "America/New_York"
            // Central
            "Alabama", "Arkansas", "Illinois", "Indiana", "Iowa", "Kansas",
            "Kentucky", "Louisiana", "Minnesota", "Mississippi", "Missouri",
            "Nebraska", "North Dakota", "Oklahoma", "South Dakota",
            "Tennessee", "Texas", "Wisconsin",
            -> "America/Chicago"
            // Mountain
            "Arizona", "Colorado", "Idaho", "Montana", "New Mexico",
            "Utah", "Wyoming",
            -> "America/Denver"
            // Pacific
            "California", "Nevada", "Oregon", "Washington",
            -> "America/Los_Angeles"
            // Alaska / Hawaii
            "Alaska" -> "America/Anchorage"
            "Hawaii" -> "Pacific/Honolulu"
            else -> return null
        }
        return TimeZone.getTimeZone(zoneId)
    }

    private fun canadianProvinceToTimezone(province: String): TimeZone? {
        val zoneId = when (province) {
            "Ontario", "Quebec" -> "America/Toronto"
            "British Columbia" -> "America/Vancouver"
            "Alberta" -> "America/Edmonton"
            "Saskatchewan" -> "America/Regina"
            "Manitoba" -> "America/Winnipeg"
            "Nova Scotia", "New Brunswick",
            "Prince Edward Island", "Newfoundland and Labrador" -> "America/Halifax"
            "Northwest Territories", "Nunavut" -> "America/Yellowknife"
            "Yukon" -> "America/Whitehorse"
            else -> return null
        }
        return TimeZone.getTimeZone(zoneId)
    }

    private fun australianStateToTimezone(state: String): TimeZone? {
        val zoneId = when (state) {
            "New South Wales", "Australian Capital Territory",
            "Victoria", "Tasmania" -> "Australia/Sydney"
            "Queensland" -> "Australia/Brisbane"
            "South Australia" -> "Australia/Adelaide"
            "Western Australia" -> "Australia/Perth"
            "Northern Territory" -> "Australia/Darwin"
            else -> return null
        }
        return TimeZone.getTimeZone(zoneId)
    }

    private fun brazilianStateToTimezone(state: String): TimeZone? {
        val zoneId = when (state) {
            "São Paulo", "Rio de Janeiro", "Minas Gerais", "Espírito Santo",
            "Rio Grande do Sul", "Santa Catarina", "Paraná", "Bahia",
            "Sergipe", "Alagoas", "Pernambuco", "Paraíba",
            "Rio Grande do Norte", "Ceará", "Piauí", "Maranhão",
            "Distrito Federal", "Goiás", "Tocantins" -> "America/Sao_Paulo"
            "Amazonas", "Rondônia", "Roraima", "Acre", "Amapá",
            "Pará" -> "America/Manaus"
            "Mato Grosso", "Mato Grosso do Sul" -> "America/Cuiaba"
            else -> return null
        }
        return TimeZone.getTimeZone(zoneId)
    }

    private fun mexicanStateToTimezone(state: String): TimeZone? {
        val zoneId = when (state) {
            "Baja California" -> "America/Tijuana"
            "Sonora", "Chihuahua", "Sinaloa", "Nayarit" -> "America/Mazatlan"
            else -> "America/Mexico_City"
        }
        return TimeZone.getTimeZone(zoneId)
    }

    private fun russianRegionToTimezone(region: String): TimeZone? {
        val zoneId = when (region) {
            "Moscow", "Saint Petersburg" -> "Europe/Moscow"
            "Kaliningrad" -> "Europe/Kaliningrad"
            "Sverdlovsk", "Chelyabinsk", "Tyumen" -> "Asia/Yekaterinburg"
            "Novosibirsk", "Omsk", "Tomsk" -> "Asia/Novosibirsk"
            "Krasnoyarsk", "Irkutsk" -> "Asia/Krasnoyarsk"
            "Yakutia", "Sakha" -> "Asia/Yakutsk"
            "Vladivostok", "Primorsky" -> "Asia/Vladivostok"
            "Kamchatka", "Chukotka" -> "Asia/Kamchatka"
            else -> "Europe/Moscow"
        }
        return TimeZone.getTimeZone(zoneId)
    }
}
