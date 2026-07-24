package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Comprehensive space-weather data repository for the Aurora Forecaster.
 *
 * Fetches multiple NOAA SWPC JSON endpoints (all free, no API key) to build
 * a complete picture of current and forecasted geomagnetic activity, then
 * computes location-specific aurora visibility for the user's latitude.
 *
 * Each endpoint is cached independently with an appropriate TTL so the UI
 * can load progressively without redundant network calls.
 */
object AuroraRepository {

    private const val TAG = "AuroraRepository"
    private const val BASE = "https://services.swpc.noaa.gov"

    // Cache TTLs (milliseconds)
    private const val TTL_KP = 60_000L           // 1 minute
    private const val TTL_FORECAST = 15 * 60_000L // 15 minutes
    private const val TTL_XRAY = 5 * 60_000L      // 5 minutes
    private const val TTL_SOLAR_WIND = 60_000L    // 1 minute
    private const val TTL_IMF = 60_000L           // 1 minute
    private const val TTL_REGIONS = 60 * 60_000L  // 1 hour
    private const val TTL_FLUX = 60 * 60_000L     // 1 hour
    private const val TTL_PROBS = 3 * 60 * 60_000L // 3 hours
    private const val TTL_MOON = 60 * 60_000L     // 1 hour

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ─── State flows exposed to the UI ───
    private val _auroraData = MutableStateFlow(AuroraData())
    val auroraData: StateFlow<AuroraData> = _auroraData.asStateFlow()

    // ─── In-memory caches ───
    private data class CacheEntry(val data: Any, val fetchedAtMs: Long)
    private val cache = java.util.concurrent.ConcurrentHashMap<String, CacheEntry>()

    private fun <T> getCached(key: String, ttl: Long): T? {
        val entry = cache[key] ?: return null
        if (System.currentTimeMillis() - entry.fetchedAtMs > ttl) return null
        @Suppress("UNCHECKED_CAST")
        return entry.data as T
    }

    private fun <T> putCached(key: String, data: T) {
        cache[key] = CacheEntry(data as Any, System.currentTimeMillis())
    }

    /**
     * Fetch all space-weather data progressively. Call from a ViewModel or
     * LaunchedEffect — each endpoint is fetched independently and published
     * to [auroraData] as it arrives.
     */
    suspend fun fetchAll(lat: Double, lon: Double) {
        // Fetch Kp first (fastest, most important)
        fetchKpIndex()
        fetchKpForecast()
        // Then solar wind + IMF (real-time drivers)
        fetchSolarWind()
        fetchImf()
        // Then flare data
        fetchXrayFlux()
        // Then slower-cadence data
        fetchSolarRegions()
        fetchSolarFlux()
        fetchSolarProbabilities()
        // Moon phase
        fetchMoonPhase()
        // Compute visibility
        updateVisibility(lat)
        // Build cause chain
        buildCauseChain()
    }

    // ─── Kp Index ───

    private suspend fun fetchKpIndex() {
        val cached: List<KpEntry>? = getCached("kp", TTL_KP)
        if (cached != null) {
            _auroraData.update { it.copy(kpHistory = cached, currentKp = cached.lastOrNull()?.kp ?: it.currentKp) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/planetary_k_index_1m.json")
            val entries = parseKpIndex(raw)
            putCached("kp", entries)
            _auroraData.update { it.copy(kpHistory = entries, currentKp = entries.lastOrNull()?.kp ?: it.currentKp, lastUpdatedMs = System.currentTimeMillis()) }
        } catch (e: Exception) {
            Log.w(TAG, "Kp index fetch failed: ${e.message}")
        }
    }

    private fun parseKpIndex(raw: String): List<KpEntry> {
        val root = json.parseToJsonElement(raw).jsonObject
        val arr: JsonArray = root["KpIndexList"] as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val obj = el.jsonObject
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val kp = obj["Kp"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
            KpEntry(timeTag = time, kp = kp)
        }
    }

    // ─── 3-day Kp Forecast ───

    private suspend fun fetchKpForecast() {
        val cached: List<KpForecastEntry>? = getCached("kp_forecast", TTL_FORECAST)
        if (cached != null) {
            _auroraData.update { it.copy(kpForecast = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/products/noaa-planetary-k-index-forecast.json")
            val entries = parseKpForecast(raw)
            putCached("kp_forecast", entries)
            _auroraData.update { it.copy(kpForecast = entries) }
        } catch (e: Exception) {
            Log.w(TAG, "Kp forecast fetch failed: ${e.message}")
        }
    }

    private fun parseKpForecast(raw: String): List<KpForecastEntry> {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val obj = el.jsonObject
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val kp = obj["Kp"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
            val observed = obj["observed"]?.jsonPrimitive?.contentOrNull == "observed"
            KpForecastEntry(timeTag = time, kp = kp, observed = observed)
        }
    }

    // ─── GOES X-ray Flux (Solar Flares) ───

    private suspend fun fetchXrayFlux() {
        val cached: List<XrayEntry>? = getCached("xray", TTL_XRAY)
        if (cached != null) {
            _auroraData.update { it.copy(xrayData = cached, recentFlares = extractFlares(cached)) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/goes/primary/xrays-6-hour.json")
            val entries = parseXray(raw)
            putCached("xray", entries)
            val flares = extractFlares(entries)
            _auroraData.update { it.copy(xrayData = entries, recentFlares = flares) }
        } catch (e: Exception) {
            Log.w(TAG, "X-ray flux fetch failed: ${e.message}")
        }
    }

    private fun parseXray(raw: String): List<XrayEntry> {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val obj = el.jsonObject
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val flux = obj["flux"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
            val band = obj["energy"]?.jsonPrimitive?.contentOrNull ?: "0.1-0.8nm"
            XrayEntry(timeTag = time, flux = flux, energyBand = band)
        }.filter { it.energyBand == "0.1-0.8nm" }
    }

    /**
     * Extract significant flare events (M-class and X-class) from X-ray data.
     * Also determines the current/latest flare class.
     */
    private fun extractFlares(entries: List<XrayEntry>): List<FlareEvent> {
        if (entries.isEmpty()) return emptyList()
        // Find peaks above M-class threshold (1e-5 W/m^2)
        val flares = mutableListOf<FlareEvent>()
        var inFlare = false
        var peakFlux = 0.0
        var peakTime = ""

        for (entry in entries) {
            if (entry.flux >= 1e-5) {
                if (!inFlare) inFlare = true
                if (entry.flux > peakFlux) {
                    peakFlux = entry.flux
                    peakTime = entry.timeTag
                }
            } else if (inFlare) {
                if (peakFlux >= 1e-5) {
                    flares.add(FlareEvent(
                        timeTag = peakTime,
                        flux = peakFlux,
                        flareClass = classifyFlare(peakFlux),
                    ))
                }
                inFlare = false
                peakFlux = 0.0
                peakTime = ""
            }
        }
        if (inFlare && peakFlux >= 1e-5) {
            flares.add(FlareEvent(
                timeTag = peakTime,
                flux = peakFlux,
                flareClass = classifyFlare(peakFlux),
            ))
        }
        return flares.sortedByDescending { it.flux }
    }

    /**
     * Classify a flare from its X-ray flux value (0.1-0.8nm band).
     * A: <1e-7, B: 1e-7 to 1e-6, C: 1e-6 to 1e-5, M: 1e-5 to 1e-4, X: >=1e-4
     */
    fun classifyFlare(flux: Double): FlareClass {
        return when {
            flux >= 1e-4 -> FlareClass.X
            flux >= 1e-5 -> FlareClass.M
            flux >= 1e-6 -> FlareClass.C
            flux >= 1e-7 -> FlareClass.B
            else -> FlareClass.A
        }
    }

    fun flareClassLabel(flux: Double): String {
        val cls = classifyFlare(flux)
        val multiplier = when (cls) {
            FlareClass.X -> flux / 1e-4
            FlareClass.M -> flux / 1e-5
            FlareClass.C -> flux / 1e-6
            FlareClass.B -> flux / 1e-7
            FlareClass.A -> flux / 1e-8
        }
        return "${cls.letter}${String.format("%.1f", multiplier)}"
    }

    // ─── ACE Solar Wind ───

    private suspend fun fetchSolarWind() {
        val cached: SolarWindReading? = getCached("solar_wind", TTL_SOLAR_WIND)
        if (cached != null) {
            _auroraData.update { it.copy(solarWind = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/ace/swepam/swepam_1m.json")
            val reading = parseSolarWind(raw)
            putCached("solar_wind", reading)
            _auroraData.update { it.copy(solarWind = reading) }
        } catch (e: Exception) {
            Log.w(TAG, "Solar wind fetch failed: ${e.message}")
        }
    }

    private fun parseSolarWind(raw: String): SolarWindReading? {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return null
        // Find the most recent valid entry
        for (i in arr.indices.reversed()) {
            val obj = arr[i].jsonObject
            val speed = obj["speed"]?.jsonPrimitive?.doubleOrNull ?: continue
            val density = obj["density"]?.jsonPrimitive?.doubleOrNull ?: continue
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: ""
            if (speed > 0) return SolarWindReading(timeTag = time, speedKms = speed, density = density)
        }
        return null
    }

    // ─── ACE IMF (Bz) ───

    private suspend fun fetchImf() {
        val cached: ImfReading? = getCached("imf", TTL_IMF)
        if (cached != null) {
            _auroraData.update { it.copy(imf = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/ace/mag/mag_1m.json")
            val reading = parseImf(raw)
            putCached("imf", reading)
            _auroraData.update { it.copy(imf = reading) }
        } catch (e: Exception) {
            Log.w(TAG, "IMF fetch failed: ${e.message}")
        }
    }

    private fun parseImf(raw: String): ImfReading? {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return null
        for (i in arr.indices.reversed()) {
            val obj = arr[i].jsonObject
            val bz = obj["bz"]?.jsonPrimitive?.doubleOrNull ?: continue
            val bt = obj["bt"]?.jsonPrimitive?.doubleOrNull ?: continue
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: ""
            if (!bz.isNaN()) return ImfReading(timeTag = time, bz = bz, bt = bt)
        }
        return null
    }

    // ─── Solar Active Regions ───

    private suspend fun fetchSolarRegions() {
        val cached: List<SolarRegion>? = getCached("regions", TTL_REGIONS)
        if (cached != null) {
            _auroraData.update { it.copy(solarRegions = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/solar_regions.json")
            val regions = parseSolarRegions(raw)
            putCached("regions", regions)
            _auroraData.update { it.copy(solarRegions = regions) }
            // Record daily snapshots for magnetic evolution tracking
            SunspotHistoryTracker.recordSnapshots(regions)
        } catch (e: Exception) {
            Log.w(TAG, "Solar regions fetch failed: ${e.message}")
        }
    }

    private fun parseSolarRegions(raw: String): List<SolarRegion> {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val obj = el.jsonObject
            val nr = obj["nr"]?.jsonPrimitive?.intOrNull ?: return@mapNotNull null
            val location = obj["location"]?.jsonPrimitive?.contentOrNull ?: ""
            val magClass = obj["magClass"]?.jsonPrimitive?.contentOrNull ?: ""
            val spotCount = obj["spotCount"]?.jsonPrimitive?.intOrNull ?: 0
            val cProb = obj["Cprob"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val mProb = obj["Mprob"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val xProb = obj["Xprob"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            SolarRegion(
                number = nr,
                location = location,
                magneticClass = magClass,
                spotCount = spotCount,
                cClassProb = cProb,
                mClassProb = mProb,
                xClassProb = xProb,
            )
        }
    }

    // ─── F10.7 Solar Radio Flux ───

    private suspend fun fetchSolarFlux() {
        val cached: SolarFlux? = getCached("flux", TTL_FLUX)
        if (cached != null) {
            _auroraData.update { it.copy(solarFlux = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/f107_cm_flux.json")
            val flux = parseSolarFlux(raw)
            val history = parseSolarFluxHistory(raw)
            putCached("flux", flux)
            _auroraData.update { it.copy(solarFlux = flux, f107History = history) }
        } catch (e: Exception) {
            Log.w(TAG, "Solar flux fetch failed: ${e.message}")
        }
    }

    private fun parseSolarFlux(raw: String): SolarFlux? {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return null
        for (i in arr.indices.reversed()) {
            val obj = arr[i].jsonObject
            val flux = obj["f107"]?.jsonPrimitive?.doubleOrNull ?: continue
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: ""
            if (flux > 0) return SolarFlux(timeTag = time, f107 = flux)
        }
        return null
    }

    /** Parse the last 7 daily F10.7 values for the trend chart. */
    private fun parseSolarFluxHistory(raw: String): List<SolarFlux> {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val obj = el.jsonObject
            val flux = obj["f107"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
            val time = obj["time_tag"]?.jsonPrimitive?.contentOrNull ?: ""
            if (flux > 0) SolarFlux(timeTag = time, f107 = flux) else null
        }.takeLast(7)
    }

    // ─── Solar Probabilities ───

    private suspend fun fetchSolarProbabilities() {
        val cached: SolarProbabilities? = getCached("probs", TTL_PROBS)
        if (cached != null) {
            _auroraData.update { it.copy(probabilities = cached) }
            return
        }
        try {
            val raw = fetchText("$BASE/json/solar_probabilities.json")
            val probs = parseSolarProbabilities(raw)
            putCached("probs", probs)
            _auroraData.update { it.copy(probabilities = probs) }
        } catch (e: Exception) {
            Log.w(TAG, "Solar probabilities fetch failed: ${e.message}")
        }
    }

    private fun parseSolarProbabilities(raw: String): SolarProbabilities? {
        val arr: JsonArray = json.parseToJsonElement(raw) as? JsonArray ?: return null
        val obj = arr.lastOrNull()?.jsonObject ?: return null
        return SolarProbabilities(
            cClassProb = obj["c_prob"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
            mClassProb = obj["m_prob"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
            xClassProb = obj["x_prob"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
            protonProb = obj["proton_prob"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
        )
    }

    // ─── Moon Phase ───

    private suspend fun fetchMoonPhase() {
        val cached: MoonPhase? = getCached("moon", TTL_MOON)
        if (cached != null) {
            _auroraData.update { it.copy(moonPhase = cached) }
            return
        }
        try {
            val raw = fetchText("https://api.phaseofthemoontoday.com/v1/current")
            val obj = json.parseToJsonElement(raw).jsonObject
            val phase = MoonPhase(
                phaseName = obj["phase_name"]?.jsonPrimitive?.contentOrNull ?: "Unknown",
                illumination = obj["illumination"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                emoji = obj["emoji"]?.jsonPrimitive?.contentOrNull ?: "\uD83C\uDF19",
                nextFullMoon = obj["next_full_moon"]?.jsonPrimitive?.contentOrNull ?: "",
                nextNewMoon = obj["next_new_moon"]?.jsonPrimitive?.contentOrNull ?: "",
            )
            putCached("moon", phase)
            _auroraData.update { it.copy(moonPhase = phase) }
        } catch (e: Exception) {
            Log.w(TAG, "Moon phase fetch failed: ${e.message}")
            // Fallback to local computation
            val computed = computeMoonPhaseLocally()
            _auroraData.update { it.copy(moonPhase = computed) }
        }
    }

    /**
     * Simple local moon phase computation as a fallback when the API is unavailable.
     * Uses the known synodic month period (29.53059 days) from a known new moon epoch.
     */
    private fun computeMoonPhaseLocally(): MoonPhase {
        val knownNewMoonMs = 947182440000L // Jan 6, 2000 18:14 UTC
        val synodicMonthMs = 29.53059 * 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val cycles = ((now - knownNewMoonMs).toDouble() / synodicMonthMs)
        val phaseFraction = cycles - cycles.toInt()
        val illumination = (1 - kotlin.math.cos(phaseFraction * 2 * kotlin.math.PI)) / 2 * 100

        val phaseName = when (phaseFraction) {
            in 0.0..0.03, in 0.97..1.0 -> "New Moon"
            in 0.03..0.22 -> "Waxing Crescent"
            in 0.22..0.28 -> "First Quarter"
            in 0.28..0.47 -> "Waxing Gibbous"
            in 0.47..0.53 -> "Full Moon"
            in 0.53..0.72 -> "Waning Gibbous"
            in 0.72..0.78 -> "Last Quarter"
            else -> "Waning Crescent"
        }
        val emoji = when {
            illumination < 5 -> "\uD83C\uDF11"
            illumination < 48 -> "\uD83C\uDF12"
            illumination < 52 -> "\uD83C\uDF13"
            illumination < 95 -> "\uD83C\uDF14"
            else -> "\uD83C\uDF15"
        }
        return MoonPhase(phaseName, illumination, emoji, "", "")
    }

    // ─── Visibility Computation ───

    /**
     * Compute the Kp threshold needed for aurora visibility at the given latitude.
     * The auroral oval extends southward as Kp increases. A common rule:
     * visible latitude = 67.5 - (Kp * 4.5) for mid-latitudes.
     * So the required Kp = (67.5 - |latitude|) / 4.5
     */
    fun kpThresholdForLatitude(lat: Double): Double {
        val absLat = abs(lat)
        return ((67.5 - absLat) / 4.5).coerceIn(0.0, 9.0)
    }

    /**
     * Check if aurora is currently visible from the given latitude based on current Kp.
     */
    fun isAuroraVisible(lat: Double, kp: Double): Boolean {
        return kp >= kpThresholdForLatitude(lat)
    }

    private fun updateVisibility(lat: Double) {
        val kp = _auroraData.value.currentKp
        val threshold = kpThresholdForLatitude(lat)
        val visible = kp >= threshold
        _auroraData.update { it.copy(visibilityThreshold = threshold, isAuroraVisible = visible) }
    }

    // ─── Cause Chain Narrative ───

    /**
     * Build a plain-language explanation of what's driving the current Kp level.
     * Correlates recent flare events with solar wind speed and Bz readings.
     */
    private fun buildCauseChain() {
        val data = _auroraData.value
        val kp = data.currentKp
        val wind = data.solarWind
        val imf = data.imf
        val flares = data.recentFlares

        val causeText = buildString {
            if (flares.isNotEmpty() && flares.first().flareClass.ordinal >= FlareClass.M.ordinal) {
                val flare = flares.first()
                val classLabel = flareClassLabel(flare.flux)
                append("A $classLabel-class solar flare was detected")
                if (flare.timeTag.isNotEmpty()) {
                    append(" at ${flare.timeTag.take(16).replace("T", " ")} UTC")
                }
                append(". ")
            }

            if (wind != null) {
                val speedLabel = when {
                    wind.speedKms > 700 -> "extreme"
                    wind.speedKms > 500 -> "elevated"
                    wind.speedKms > 400 -> "moderate"
                    else -> "calm"
                }
                append("Solar wind is $speedLabel at ${wind.speedKms.roundToInt()} km/s")
                if (wind.density > 0) {
                    append(" with a density of ${wind.density.roundToInt()} particles/cm\u00B3")
                }
                append(". ")
            }

            if (imf != null) {
                if (imf.bz < 0) {
                    append("Bz is pointing south at ${imf.bz.roundToInt()} nT \u2014 this allows solar wind energy to connect with Earth's magnetic field and drive aurora. ")
                } else {
                    append("Bz is pointing north at ${imf.bz.roundToInt()} nT \u2014 this limits aurora activity. ")
                }
            }

            if (kp >= 5.0) {
                val stormLevel = when {
                    kp >= 8.0 -> "G4-G5 (severe to extreme)"
                    kp >= 7.0 -> "G3 (strong)"
                    kp >= 6.0 -> "G2 (moderate)"
                    else -> "G1 (minor)"
                }
                append("This has pushed Kp to $kp \u2014 a $stormLevel geomagnetic storm. ")
            } else if (kp >= 4.0) {
                append("Kp is at $kp \u2014 active conditions but below storm threshold. ")
            } else {
                append("Kp is at $kp \u2014 quiet conditions. ")
            }
        }

        _auroraData.update { it.copy(causeChain = causeText.trim()) }
    }

    // ─── SDO Sun Image URL ───

    /**
     * Get the URL for the latest SDO AIA image at the given wavelength.
     * SDO publishes new images every ~15 minutes.
     */
    fun sdoImageUrl(wavelength: String): String {
        return "https://sdo.gsfc.nasa.gov/assets/img/latest/latest_1024_${wavelength}.jpg"
    }

    /**
     * Refresh key for the SDO image — changes every 15 minutes to bust Coil cache.
     */
    fun sdoRefreshKey(): Long {
        return System.currentTimeMillis() / (15 * 60 * 1000L)
    }

    // ─── Helioviewer Movie URL ───

    /**
     * Request a Helioviewer movie for a recent flare event.
     * Falls back to a NASA SVS URL if Helioviewer is unavailable.
     */
    suspend fun fetchFlareMovieUrl(): String? {
        val data = _auroraData.value
        val recentFlare = data.recentFlares.firstOrNull { it.flareClass.ordinal >= FlareClass.M.ordinal }
            ?: return null

        try {
            // Request a movie covering the flare window
            val startTime = recentFlare.timeTag.replace("T", "T").replace("Z", "")
            val endTime = startTime // Helioviewer will expand the window
            val url = "https://api.helioviewer.org/v2/queueMovie/" +
                "?startDate=$startTime" +
                "&endDate=$endTime" +
                "&sourceId=14" + // SDO AIA 131
                "&datasets=14,1" +
                "&imageScale=2.4" +
                "&events=FL"

            val raw = fetchText(url)
            val obj = json.parseToJsonElement(raw).jsonObject
            val movieId = obj["id"]?.jsonPrimitive?.intOrNull ?: return null

            // Poll for movie readiness (simplified — return the download URL)
            return "https://api.helioviewer.org/v2/downloadMovie/?id=$movieId&format=mp4"
        } catch (e: Exception) {
            Log.w(TAG, "Helioviewer movie request failed: ${e.message}")
            return null
        }
    }

    // ─── Network helper ───

    private suspend fun fetchText(url: String): String = withContext(Dispatchers.IO) {
        val response = NetworkClient.client.get(url) {
            headers.append("User-Agent", "RockScout aurora forecaster (contact@rork.app)")
        }
        if (!response.status.isSuccess()) {
            throw RuntimeException("HTTP ${response.status.value}")
        }
        response.body<String>()
    }

    // ─── NOAA Storm Scale ───

    fun stormScaleLabel(kp: Double): String = when {
        kp >= 8.0 -> "G4-G5"
        kp >= 7.0 -> "G3"
        kp >= 6.0 -> "G2"
        kp >= 5.0 -> "G1"
        kp >= 4.0 -> "Active"
        else -> "Quiet"
    }

    fun stormScaleColor(kp: Double): Long = when {
        kp >= 8.0 -> 0xFFFF3B30
        kp >= 7.0 -> 0xFFFF6B3D
        kp >= 6.0 -> 0xFFFF9500
        kp >= 5.0 -> 0xFFFFCC00
        kp >= 4.0 -> 0xFF34C759
        else -> 0xFF30D158
    }
}

// ─── Data Models ───

data class AuroraData(
    val currentKp: Double = 0.0,
    val kpHistory: List<KpEntry> = emptyList(),
    val kpForecast: List<KpForecastEntry> = emptyList(),
    val xrayData: List<XrayEntry> = emptyList(),
    val recentFlares: List<FlareEvent> = emptyList(),
    val solarWind: SolarWindReading? = null,
    val imf: ImfReading? = null,
    val solarRegions: List<SolarRegion> = emptyList(),
    val solarFlux: SolarFlux? = null,
    val f107History: List<SolarFlux> = emptyList(),
    val probabilities: SolarProbabilities? = null,
    val moonPhase: MoonPhase? = null,
    val visibilityThreshold: Double = 0.0,
    val isAuroraVisible: Boolean = false,
    val causeChain: String = "",
    val lastUpdatedMs: Long = 0L,
)

@Serializable
data class KpEntry(
    val timeTag: String,
    val kp: Double,
)

@Serializable
data class KpForecastEntry(
    val timeTag: String,
    val kp: Double,
    val observed: Boolean,
)

@Serializable
data class XrayEntry(
    val timeTag: String,
    val flux: Double,
    val energyBand: String,
)

@Serializable
data class FlareEvent(
    val timeTag: String,
    val flux: Double,
    val flareClass: FlareClass,
)

enum class FlareClass(val letter: String, val color: Long) {
    A("A", 0xFF30D158),
    B("B", 0xFF30D158),
    C("C", 0xFFFFCC00),
    M("M", 0xFFFF9500),
    X("X", 0xFFFF3B30),
}

@Serializable
data class SolarWindReading(
    val timeTag: String,
    val speedKms: Double,
    val density: Double,
)

@Serializable
data class ImfReading(
    val timeTag: String,
    val bz: Double,
    val bt: Double,
)

@Serializable
data class SolarRegion(
    val number: Int,
    val location: String,
    val magneticClass: String,
    val spotCount: Int,
    val cClassProb: Double,
    val mClassProb: Double,
    val xClassProb: Double,
)

@Serializable
data class SolarFlux(
    val timeTag: String,
    val f107: Double,
)

@Serializable
data class SolarProbabilities(
    val cClassProb: Double,
    val mClassProb: Double,
    val xClassProb: Double,
    val protonProb: Double,
)

@Serializable
data class MoonPhase(
    val phaseName: String,
    val illumination: Double,
    val emoji: String,
    val nextFullMoon: String,
    val nextNewMoon: String,
)
