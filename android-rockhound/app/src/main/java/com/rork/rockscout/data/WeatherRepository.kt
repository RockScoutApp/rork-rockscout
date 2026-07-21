package com.rork.rockscout.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlinx.serialization.SerialName
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.math.atan
import kotlin.math.asin
import kotlin.math.roundToInt

/**
 * Snapshot of current weather + today's daylight window for a dig site.
 *
 * Sourced from Open-Meteo (free, no API key) for current conditions and
 * computed locally via astronomical formulas for sunrise/sunset — so the
 * daylight window still works offline.
 */
@Serializable
data class WeatherSnapshot(
    val temperatureF: Int,
    val weatherCode: Int,
    val conditionLabel: String,
    val conditionEmoji: String,
    val precipProbability: Int,        // 0..100
    val windMph: Int,
    val sunriseEpochSec: Long,         // UTC seconds
    val sunsetEpochSec: Long,          // UTC seconds
    val hourly: List<HourlyTrend>,     // next ~3 hours (excluding current hour)
    val fetchedEpochMs: Long,
) {
    /** True once the cached snapshot is older than [WeatherRepository.CACHE_TTL_MS]. */
    val isStale: Boolean
        get() = System.currentTimeMillis() - fetchedEpochMs > WeatherRepository.CACHE_TTL_MS

    val cachedMinutesAgo: Long
        get() = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - fetchedEpochMs)
}

@Serializable
data class HourlyTrend(
    val hourEpochSec: Long,
    val temperatureF: Int,
    val precipProbability: Int,
    val weatherCode: Int,
)

/**
 * Pre-cached weather for a dig site, with the timestamp it was fetched.
 * Entries older than [WeatherRepository.CACHE_TTL_MS] are refreshed on next fetch.
 */
private data class CachedEntry(val snapshot: WeatherSnapshot, val fetchedAtMs: Long)

/**
 * Tiny Open-Meteo client + sunrise/sunset calculator for dig site cards.
 * No API key, no auth, no new permissions. 30-minute per-site cache.
 *
 * Open-Meteo endpoint used:
 *   https://api.open-meteo.com/v1/forecast
 *     ?latitude=..&longitude=..
 *     &current=temperature_2m,weather_code,precipitation_probability,wind_speed_10m
 *     &hourly=temperature_2m,precipitation_probability,weather_code
 *     &forecast_days=1
 *     &temperature_unit=fahrenheit
 *     &wind_speed_unit=mph
 *     &timezone=auto
 */
object WeatherRepository {

    private const val ENDPOINT = "https://api.open-meteo.com/v1/forecast"
    const val CACHE_TTL_MS = 30L * 60_000L  // 30 minutes (public so WeatherSnapshot.isStale can reference it)

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    /** In-memory per-site cache, keyed by dig-site id. */
    private val cache = java.util.concurrent.ConcurrentHashMap<String, CachedEntry>()

    /** Latest snapshot per site, observable by Compose. */
    private val _snapshots = MutableStateFlow<Map<String, WeatherSnapshot>>(emptyMap())
    val snapshots: StateFlow<Map<String, WeatherSnapshot>> = _snapshots.asStateFlow()

    /**
     * Fetch weather for [digSiteId] at [lat],[lon]. Returns cached entry if fresh;
     * otherwise hits Open-Meteo on [Dispatchers.IO]. Returns null on network failure
     * (caller falls back to last cached snapshot or shows an offline state).
     */
    suspend fun fetch(digSiteId: String, lat: Double, lon: Double): WeatherSnapshot? =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            cache[digSiteId]?.let { entry ->
                if (now - entry.fetchedAtMs < CACHE_TTL_MS) {
                    return@withContext entry.snapshot
                }
            }
            val snapshot = runCatching { fetchRemote(lat, lon) }.getOrNull()
            if (snapshot != null) {
                cache[digSiteId] = CachedEntry(snapshot, now)
                _snapshots.update { it + (digSiteId to snapshot) }
            } else {
                // network failed — return stale cache if present
                cache[digSiteId]?.let { return@withContext it.snapshot }
            }
            snapshot
        }

    /** Cached snapshot for [digSiteId] if present, regardless of freshness. */
    fun cached(digSiteId: String): WeatherSnapshot? = cache[digSiteId]?.snapshot

    private suspend fun fetchRemote(lat: Double, lon: Double): WeatherSnapshot {
        // Use the shared NetworkClient — gets retry/backoff on flaky cellular,
        // connection pooling, and managed timeouts for free.
        val raw = NetworkClient.client.get(ENDPOINT) {
            parameter("latitude", formatCoord(lat))
            parameter("longitude", formatCoord(lon))
            parameter("current", "temperature_2m,weather_code,precipitation_probability,wind_speed_10m")
            parameter("hourly", "temperature_2m,precipitation_probability,weather_code")
            parameter("forecast_days", "1")
            parameter("temperature_unit", "fahrenheit")
            parameter("wind_speed_unit", "mph")
            parameter("precipitation_probability", "true")
            parameter("timezone", "auto")
        }.body<String>()
        val resp = json.decodeFromString(OpenMeteoResponse.serializer(), raw)

        val current = resp.current
        val zoneOffset = ZoneOffset.ofTotalSeconds(resp.utcOffsetSeconds)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val nowEpoch = LocalDateTime.parse(current.timeIso, dateFormatter).toEpochSecond(zoneOffset)
        val (sunrise, sunset) = computeSunriseSunset(lat, lon, nowEpoch)

        // Pick the next ~3 hours *after* the current hour from the hourly array.
        val timeIso = resp.hourly.timeIso
        val temps = resp.hourly.temperature2m
        val precip = resp.hourly.precipitationProbability
        val codes = resp.hourly.weatherCode

        val trend = timeIso.indices.map { i ->
            val epoch = LocalDateTime.parse(timeIso[i], dateFormatter).toEpochSecond(zoneOffset)
            HourlyTrend(
                hourEpochSec = epoch,
                temperatureF = temps[i].roundToInt(),
                precipProbability = precip.getOrElse(i) { 0 } ?: 0,
                weatherCode = codes.getOrElse(i) { 0 },
            )
        }.filter { it.hourEpochSec > nowEpoch }.take(3)

        return WeatherSnapshot(
            temperatureF = current.temperature2m.roundToInt(),
            weatherCode = current.weatherCode,
            conditionLabel = weatherCodeLabel(current.weatherCode),
            conditionEmoji = weatherCodeEmoji(current.weatherCode),
            precipProbability = current.precipitationProbability ?: 0,
            windMph = current.windSpeed10m.roundToInt(),
            sunriseEpochSec = sunrise,
            sunsetEpochSec = sunset,
            hourly = trend,
            fetchedEpochMs = System.currentTimeMillis(),
        )
    }

    private fun formatCoord(v: Double): String =
        String.format(java.util.Locale.US, "%.4f", v)

    /**
     * Sunrise/sunset (UTC seconds) for [lat],[lon] on the date of [epochSecUtc].
     * Standard NOAA astronomical algorithm — accurate to within ~1 minute.
     * No network call: works offline.
     */
    fun computeSunriseSunset(lat: Double, lon: Double, epochSecUtc: Long): Pair<Long, Long> {
        val cal = java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = epochSecUtc * 1000L
        val year = cal.get(java.util.Calendar.YEAR)
        val month = cal.get(java.util.Calendar.MONTH) + 1
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)

        val sunrise = computeEvent(lat, lon, year, month, day, isSunrise = true)
        val sunset = computeEvent(lat, lon, year, month, day, isSunrise = false)
        return sunrise to sunset
    }

        /**
         * NOAA sunrise/sunset algorithm. Returns the event time as UTC epoch seconds,
         * or 0 if the event doesn't occur on this date (polar day/night).
         */
        private fun computeEvent(
            lat: Double, lon: Double,
            year: Int, month: Int, day: Int,
            isSunrise: Boolean,
        ): Long {
            // 1. Day-of-year
            val n = dayOfYear(year, month, day)

            // 2. Approximate solar longitude angle (degrees)
            val lambda = 280.46 + 0.9856474 * n
            val lambdaRad = Math.toRadians(normalize360(lambda))

            // 3. Mean anomaly
            val g = 357.528 + 0.9856003 * n
            val gRad = Math.toRadians(normalize360(g))

            // 4. Ecliptic longitude of the sun
            val ecLon = lambda + 1.915 * sin(gRad) + 0.020 * sin(2 * gRad)
            val ecLonRad = Math.toRadians(normalize360(ecLon))

            // 5. Obliquity of the ecliptic
            val epsilon = 23.439 - 0.0000004 * n
            val epsilonRad = Math.toRadians(epsilon)

            // 6. Sun declination
            val sinDec = sin(epsilonRad) * sin(ecLonRad)
            val cosDec = cos(asin(sinDec))

            // 7. Hour angle for sunrise/sunset (zenith = 90.833°, accounts for refraction + semidiameter)
            val latRad = Math.toRadians(lat)
            val cosH = (cos(Math.toRadians(90.833)) - sinDec * sin(latRad)) / (cosDec * cos(latRad))
            if (cosH > 1.0 || cosH < -1.0) return 0L  // no sunrise/sunset today (polar)

            val hourAngle = Math.toDegrees(acos(cosH))

            // 8. Julian date at 0 UTC for the given date
            val jd0 = 367.0 * year - (7.0 * (year + (month + 9) / 12)) / 4 + (275 * month) / 9 + day + 1721013.5

            // 9. Equation of time (minutes)
            val eotMin = 9.87 * sin(2 * lambdaRad) - 7.53 * cos(lambdaRad) - 1.5 * sin(gRad)

            // 10. Solar noon (UTC hours) = 12 - lon/15 - eotMin/60
            val solarNoonUtcHours = 12.0 - lon / 15.0 - eotMin / 60.0
            val eventUtcHours = if (isSunrise) {
                solarNoonUtcHours - hourAngle / 15.0
            } else {
                solarNoonUtcHours + hourAngle / 15.0
            }

            // 11. Convert UTC hours-of-day to epoch seconds on the given date
            val sec = (eventUtcHours * 3600.0).toLong()
            val cal = java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"))
            cal.clear()
            cal.set(year, month - 1, day, 0, 0, 0)
            return cal.timeInMillis / 1000L + sec
        }

        private fun dayOfYear(year: Int, month: Int, day: Int): Int {
            val cal = java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"))
            cal.clear()
            cal.set(year, month - 1, day)
            return cal.get(java.util.Calendar.DAY_OF_YEAR)
        }

        private fun normalize360(deg: Double): Double {
            val m = deg % 360.0
            return if (m < 0) m + 360.0 else m
        }

        private fun acos(x: Double): Double = kotlin.math.acos(x.coerceIn(-1.0, 1.0))

    /** Human-readable label for an Open-Meteo WMO weather code. */
    fun weatherCodeLabel(code: Int): String = when (code) {
        0 -> "Clear"
        1 -> "Mostly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm + hail"
        else -> "—"
    }

    /** Emoji glyph for an Open-Meteo WMO weather code. */
    fun weatherCodeEmoji(code: Int): String = when (code) {
        0 -> "☀️"
        1 -> "🌤️"
        2 -> "⛅"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 56, 57 -> "🌦️"
        61, 63, 65, 66, 67, 80, 81, 82 -> "🌧️"
        71, 73, 75, 77, 85, 86 -> "❄️"
        95, 96, 99 -> "⛈️"
        else -> "•"
    }

    /**
     * Format a daylight event time as a local time string (e.g. "6:42 AM"),
     * or "—" if the event doesn't occur today.
     */
    fun formatLocalTime(epochSec: Long): String {
        if (epochSec == 0L) return "—"
        val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getDefault()
        return sdf.format(java.util.Date(epochSec * 1000L))
    }

    /** Label for the current daylight state at [epochSec] between [sunrise] and [sunset]. */
    fun daylightLabel(epochSec: Long, sunrise: Long, sunset: Long): String = when {
        sunrise == 0L || sunset == 0L -> "Polar day/night"
        epochSec < sunrise -> "Before sunrise"
        epochSec in sunrise..sunset -> "Daylight now"
        else -> "After sunset"
    }

    // ---- Open-Meteo JSON shape (only the fields we read) ----
    @Serializable
    private data class OpenMeteoResponse(
        @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
        @SerialName("current") val current: CurrentBlock,
        @SerialName("hourly") val hourly: HourlyBlock,
    )

    @Serializable
    private data class CurrentBlock(
        @SerialName("time") val timeIso: String,
        @SerialName("temperature_2m") val temperature2m: Double,
        @SerialName("weather_code") val weatherCode: Int,
        @SerialName("precipitation_probability") val precipitationProbability: Int? = 0,
        @SerialName("wind_speed_10m") val windSpeed10m: Double,
    )

    @Serializable
    private data class HourlyBlock(
        @SerialName("time") val timeIso: List<String>,
        @SerialName("temperature_2m") val temperature2m: List<Double>,
        @SerialName("precipitation_probability") val precipitationProbability: List<Int?> = emptyList(),
        @SerialName("weather_code") val weatherCode: List<Int> = emptyList(),
    )
}
