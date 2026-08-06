package com.rork.rockscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Public
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.DigLocation
import com.rork.rockscout.data.InternationalLocations
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.data.LocationRefresher
import com.rork.rockscout.data.LocationType
import com.rork.rockscout.data.UserPinSubmissionStore
import com.rork.rockscout.data.PurchaseManager
import com.rork.rockscout.data.SeedData

import com.rork.rockscout.ui.components.AlphabetIndex
import com.rork.rockscout.ui.components.DarkCard
import com.rork.rockscout.ui.components.LocationImage
import com.rork.rockscout.ui.components.NewBadge
import com.rork.rockscout.ui.components.DigSitesMapView
import com.rork.rockscout.ui.components.CompactSearchPill
import com.rork.rockscout.ui.components.InterstitialAdTrigger
import com.rork.rockscout.ui.components.AddLocationSheet
import com.rork.rockscout.ui.components.ScreenScaffold
import com.rork.rockscout.ui.components.SculptedIconButton
import com.rork.rockscout.ui.components.SponsoredSpecimenPrompt
import com.rork.rockscout.ui.components.SpecimenGlyph
import com.rork.rockscout.ui.components.TagChip
import com.rork.rockscout.ui.components.YooperliteHeart
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.CardPlusDropdown
import com.rork.rockscout.ui.components.CardAction
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Success
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.theme.TextMid
import com.rork.rockscout.ui.components.SculptedIconButton
import androidx.compose.material.icons.filled.AddLocation
import com.rork.rockscout.ui.components.glowingBorder

private enum class ViewMode { LIST, MAP }

/** Flat list item for the state-grouped All Sites section. */
private sealed interface SiteListItem {
    data class StateHeader(val state: String, val count: Int) : SiteListItem
    data class SiteCard(val loc: DigLocation, val miles: Double) : SiteListItem
}

/** Extract the state from a region string like "Murfreesboro, Arkansas, USA".
 *  Handles multi-part cities (e.g. "Ka Lae, Big Island, Hawaii, USA") and
 *  non-standard entries (e.g. "USA (national federation)"). */
private fun extractState(region: String): String {
    val withoutCountry = region.substringBeforeLast(", ").trim()
    return withoutCountry.substringAfterLast(", ").trim().ifEmpty { region }
}

@Composable
fun LocationsScreen(navController: NavController) {
    val repo = AppRepository.instance
    val profile by repo.profile.collectAsStateWithLifecycle()
    val current by repo.currentLocation.collectAsStateWithLifecycle()
    val favorites by repo.favoriteSpots.collectAsStateWithLifecycle()
    val locationRefresh by repo.locationRefreshTrigger.collectAsStateWithLifecycle()
    val wishlistIds by repo.wishlist.collectAsStateWithLifecycle()
    val purchaseManager = PurchaseManager.instance
    val isPremium by purchaseManager.effectiveIsPremium.collectAsStateWithLifecycle()
    val isPayingUser = isPremium
    var filter by remember { mutableStateOf<LocationType?>(null) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showAddLocation by remember { mutableStateOf(false) }
    var addLocationMessage by remember { mutableStateOf<String?>(null) }

    // Nearby places is now driven entirely by the Home location-monitoring
    // toggle. When location monitoring is on, nearby places is on too, and the
    // Near You / Near Me sections appear at the top of this screen automatically.
    val nearbyOn = profile.nearbyPlacesEnabled

    InterstitialAdTrigger(screenKey = "locations") {
        navController.navigate(Routes.PAYWALL)
    }

    // Fetch fresh location immediately when monitoring is enabled, and again
    // whenever the locationRefreshTrigger bumps (app foregrounded, profile
    // saved, pull-to-refresh). This updates the nearby list, the map center,
    // and proximity notifications instantly.
    LaunchedEffect(nearbyOn, locationRefresh) {
        if (nearbyOn && LocationFetcher.hasPermission(context)) {
            LocationRefresher.refresh(context)
        }
    }

    // Determine if user is in the US or international
    val isUsUser = remember(current) {
        InternationalLocations.isInUnitedStates(current.first, current.second)
    }

    // International nearby locations (within 150 miles)
    val intlNearby = remember(current, nearbyOn, filter, searchQuery) {
        if (nearbyOn && !isUsUser) {
            InternationalLocations.nearbyInternational(current.first, current.second)
                .filter { filter == null || it.first.type == filter }
                .filter { searchQuery.isBlank() ||
                    it.first.name.contains(searchQuery, ignoreCase = true) ||
                    it.first.region.contains(searchQuery, ignoreCase = true) ||
                    (it.first.address?.contains(searchQuery, ignoreCase = true) ?: false) }
        } else {
            emptyList()
        }
    }

    // US locations (full list for US users, or when nearby is off)
    val usLocations = remember(current, filter, isUsUser, nearbyOn, searchQuery) {
        SeedData.allLocations
            .filter { filter == null || it.type == filter }
            .filter { searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.region.contains(searchQuery, ignoreCase = true) ||
                (it.address?.contains(searchQuery, ignoreCase = true) ?: false) }
            .map { it to AppRepository.distanceMiles(current.first, current.second, it.latitude, it.longitude) }
            .sortedBy { it.first.name.lowercase() }
    }

    val nearMe = remember(usLocations, nearbyOn, isUsUser) {
        if (nearbyOn && isUsUser) {
            usLocations.filter { it.second <= 100.0 }
        } else {
            emptyList()
        }
    }

    val allSites = remember(usLocations, nearbyOn, isUsUser, nearMe) {
        if (nearbyOn && isUsUser && nearMe.isEmpty()) {
            // Nothing nearby — fall back to the full list automatically so the user
            // never has to toggle nearby off just to see all US locations.
            usLocations
        } else {
            usLocations.filter { loc -> !nearbyOn || !isUsUser || loc.second > 100.0 }
        }
    }

    // When in map view, intercept the system back button to return to list
    // view instead of popping the entire screen off the nav stack.
    BackHandler(enabled = viewMode == ViewMode.MAP) {
        viewMode = ViewMode.LIST
    }

    ScreenScaffold(
        title = "Dig Sites & Rock Shops",
        onBack = { navController.popBackStack() },
        actions = {
            SculptedIconButton(
                icon = Icons.Filled.AddLocation,
                contentDescription = "Upload New Location",
                onClick = { showAddLocation = true },
                accent = Citrine,
                iconTint = Citrine,
                size = 44.dp,
                shadowElevation = 4.dp,
            )
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            CompactSearchPill(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search by name or address…",
                accent = Aqua,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            )
            Text(
                "Mines, quarries, public digs, beaches, river gravels, rock shops, and metaphysical stores — tap any pin or card for details, weather, and recommended gear.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
            )
            ViewModeToggle(
                selected = viewMode,
                onSelect = { viewMode = it },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
            )
            if (viewMode == ViewMode.MAP) {
                DigSitesMapView(
                    locations = SeedData.allLocations + InternationalLocations.internationalLocations,
                    userLat = current.first,
                    userLng = current.second,
                    showUser = nearbyOn,
                    onLocationTap = { navController.navigate(Routes.location(it.id)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp),
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    SponsoredSpecimenPrompt(
                        isPayingUser = isPayingUser,
                        userLat = current.first,
                        userLng = current.second,
                        wishlistIds = wishlistIds,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                    )
                    LocationsList(
                        navController = navController,
                        filter = filter,
                        onFilterChange = { filter = it },
                        isUsUser = isUsUser,
                        profile = profile,
                        current = current,
                        intlNearby = intlNearby,
                        nearMe = nearMe,
                        allSites = allSites,
                        favorites = favorites,
                        modifier = Modifier.fillMaxSize().weight(1f),
                    )
                }
            }
        }
    }

    if (showAddLocation) {
        AddLocationDialog(
            onDismiss = { showAddLocation = false },
            onSubmitted = { approved ->
                addLocationMessage = if (approved) {
                    "Location web-verified and added to the map!"
                } else {
                    "Location submitted for review!"
                }
                showAddLocation = false
            },
        )
    }

    addLocationMessage?.let { msg ->
        androidx.compose.material3.SnackbarHost(
            hostState = remember { androidx.compose.material3.SnackbarHostState() }.also {
                androidx.compose.runtime.LaunchedEffect(msg) {
                    it.showSnackbar(msg)
                    addLocationMessage = null
                }
            },
        )
    }
}

@Composable
private fun ViewModeToggle(
    selected: ViewMode,
    onSelect: (ViewMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1C16))
            .glowingBorder(1.dp, Color(0xFF1E1C16).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(4.dp),
    ) {
        ViewModeToggleButton(
            label = "List",
            icon = Icons.Filled.List,
            selected = selected == ViewMode.LIST,
            modifier = Modifier.weight(1f),
        ) { onSelect(ViewMode.LIST) }
        ViewModeToggleButton(
            label = "Map",
            icon = Icons.Filled.Map,
            selected = selected == ViewMode.MAP,
            modifier = Modifier.weight(1f),
        ) { onSelect(ViewMode.MAP) }
    }
}

@Composable
private fun ViewModeToggleButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Citrine else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (selected) Color(0xFF1C1A14) else TextMid, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color(0xFF1C1A14) else TextMid,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LocationsList(
    navController: NavController,
    filter: LocationType?,
    onFilterChange: (LocationType?) -> Unit,
    isUsUser: Boolean,
    profile: com.rork.rockscout.data.UserProfile,
    current: Pair<Double, Double>,
    intlNearby: List<Pair<DigLocation, Double>>,
    nearMe: List<Pair<DigLocation, Double>>,
    allSites: List<Pair<DigLocation, Double>>,
    favorites: List<String>,
    modifier: Modifier = Modifier,
) {
    val repo = AppRepository.instance
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // Build state-grouped flat list: state headers interspersed with site cards,
    // states sorted alphabetically, sites within each state sorted alphabetically.
    val groupedItems = remember(allSites) {
        val stateMap = allSites.groupBy { extractState(it.first.region) }
        stateMap.entries
            .sortedBy { it.key.lowercase() }
            .flatMap { (state, sites) ->
                listOf<SiteListItem>(SiteListItem.StateHeader(state, sites.size)) +
                    sites.map { SiteListItem.SiteCard(it.first, it.second) }
            }
    }
    val stateNames = remember(groupedItems) {
        groupedItems.filterIsInstance<SiteListItem.StateHeader>().map { it.state }
    }

    // Offset from the top of the LazyColumn to the first grouped item. The
    // list structure is: ShowsTabCard (0), filter chips (1), then either the
    // international section, the US Near Me section, or straight to All Sites.
    val allSitesOffset = when {
        profile.nearbyPlacesEnabled && !isUsUser -> 4 + intlNearby.size
        profile.nearbyPlacesEnabled && isUsUser -> 4 + nearMe.size
        else -> 3
    }

    Row(modifier = modifier) {
        AlphabetIndex(
            names = stateNames,
            onLetterClick = { letter ->
                val indexInGrouped = groupedItems.indexOfFirst { item ->
                    item is SiteListItem.StateHeader &&
                        item.state.startsWith(letter.toString(), ignoreCase = true)
                }
                if (indexInGrouped != -1) {
                    scope.launch { listState.scrollToItem(allSitesOffset + indexInGrouped) }
                }
            },
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterVertically),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 8.dp, end = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        item {
            ShowsTabCard(onClick = { navController.navigate(Routes.GEM_SHOWS) })
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip("All", filter == null) { onFilterChange(null) }
                LocationType.entries.forEach { type ->
                    FilterChip("${type.emoji} ${type.label}", filter == type) {
                        onFilterChange(if (filter == type) null else type)
                    }
                }
            }
        }

        // ── International Near Me section ──
        if (profile.nearbyPlacesEnabled && !isUsUser) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Near You",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "Showing ${intlNearby.size} rockhounding spot${if (intlNearby.size != 1) "s" else ""} within 150 miles of your location.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
            if (intlNearby.isEmpty()) {
                item {
                    Text(
                        "No rockhounding locations found within 150 miles. The full United States list is available below — toggle off Nearby places to browse all locations.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Citrine,
                    )
                }
            }
            items(intlNearby.size) { index ->
                val (loc, miles) = intlNearby[index]
                LocationCard(
                    loc = loc,
                    miles = miles,
                    isFavorited = favorites.contains(loc.id),
                    onToggleFavorite = { repo.toggleFavoriteSpot(loc.id) },
                    onClick = { navController.navigate(Routes.location(loc.id)) },
                )
            }

            // All US Sites section (for international users — shown as reference)
            item {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Public,
                        contentDescription = null,
                        tint = Aqua,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "United States Locations",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "Full list of US dig sites, rock shops, metaphysical shops, and collecting areas — sorted alphabetically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
            items(groupedItems.size) { index ->
                when (val item = groupedItems[index]) {
                    is SiteListItem.StateHeader -> StateHeaderRow(item.state, item.count)
                    is SiteListItem.SiteCard -> LocationCard(
                        loc = item.loc,
                        miles = item.miles,
                        isFavorited = favorites.contains(item.loc.id),
                        onToggleFavorite = { repo.toggleFavoriteSpot(item.loc.id) },
                        onClick = { navController.navigate(Routes.location(item.loc.id)) },
                    )
                }
            }
        }

        // ── US Near Me section (only when nearby places is on and user is in US) ──
        if (profile.nearbyPlacesEnabled && isUsUser) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Near Me",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "Showing ${nearMe.size} site${if (nearMe.size != 1) "s" else ""} within 100 miles of your location.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
            items(nearMe.size) { index ->
                val (loc, miles) = nearMe[index]
                LocationCard(
                    loc = loc,
                    miles = miles,
                    isFavorited = favorites.contains(loc.id),
                    onToggleFavorite = { repo.toggleFavoriteSpot(loc.id) },
                    onClick = { navController.navigate(Routes.location(loc.id)) },
                )
            }
        }

        // All Sites section (US users only)
        if (isUsUser || !profile.nearbyPlacesEnabled) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    if (profile.nearbyPlacesEnabled && isUsUser) "All Sites" else "All mines, quarries, rock shops, metaphysical shops, public digs, beaches, and collecting areas across the United States — sorted alphabetically.",
                    style = if (profile.nearbyPlacesEnabled && isUsUser) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
                    color = if (profile.nearbyPlacesEnabled && isUsUser) MaterialTheme.colorScheme.onSurface else TextMid,
                )
            }
            if (allSites.isEmpty() && profile.nearbyPlacesEnabled && isUsUser) {
                item {
                    Text(
                        "All locations are within 100 miles — you're in a great spot!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Success,
                    )
                }
            }
            items(groupedItems.size) { index ->
                when (val item = groupedItems[index]) {
                    is SiteListItem.StateHeader -> StateHeaderRow(item.state, item.count)
                    is SiteListItem.SiteCard -> LocationCard(
                        loc = item.loc,
                        miles = item.miles,
                        isFavorited = favorites.contains(item.loc.id),
                        onToggleFavorite = { repo.toggleFavoriteSpot(item.loc.id) },
                        onClick = { navController.navigate(Routes.location(item.loc.id)) },
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun StateHeaderRow(state: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Aqua.copy(alpha = 0.18f))
                .glowingBorder(1.dp, Aqua.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                state.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Aqua,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            state,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = TextLow,
        )
    }
}

@Composable
private fun ShowsTabCard(onClick: () -> Unit) {
    DarkCard(
        accent = Amethyst,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Amethyst.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = Amethyst,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Gem & Mineral Shows",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Browse upcoming gem, mineral & fossil shows across the US",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMid,
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = DarkTextMid, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Citrine else Color(0xFF1E1C16))
            .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color(0xFFFAF8F4) else Color(0xFFC9C2B0),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun LocationCard(
    loc: DigLocation,
    miles: Double,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
) {
    // Weather is intentionally NOT fetched per-card here. With 500+ locations
    // in the list, each card entering composition would fire a network request,
    // flooding the IO thread pool and freezing the app during scrolling.
    // Weather is shown on the LocationDetailScreen when the user taps a card.
    DarkCard(
        accent = Aqua,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box {
                LocationImage(
                    name = loc.name,
                    region = loc.region,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    fallback = { SpecimenGlyph(emoji = loc.type.emoji, accent = Aqua, size = 50) },
                )
                if (loc.isNew()) {
                    NewBadge(modifier = Modifier.align(Alignment.TopStart).padding(2.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(loc.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(loc.region, style = MaterialTheme.typography.bodyMedium, color = DarkTextMid)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.NearMe, contentDescription = null, tint = Citrine, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${miles.toInt()} mi away", style = MaterialTheme.typography.labelMedium, color = Citrine)
                    Spacer(Modifier.width(10.dp))
                    if (loc.publicAccess) TagChip("PUBLIC", color = Success)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .sculpted(
                            shape = RoundedCornerShape(18.dp),
                            accent = Citrine,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = onToggleFavorite,
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    YooperliteHeart(
                        active = isFavorited,
                        contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
                        size = 22.dp,
                        unclippedGlow = true,
                    )
                }
                CardPlusDropdown(
                    actions = listOf(
                        CardAction.Item(
                            label = if (isFavorited) "Remove from favorites" else "Add to favorites",
                            icon = if (isFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            iconTint = if (isFavorited) Citrine else Color.White,
                            onClick = onToggleFavorite,
                        ),
                        CardAction.Item(
                            label = "View details",
                            icon = Icons.Filled.ChevronRight,
                            iconTint = Aqua,
                            onClick = onClick,
                        ),
                    ),
                    accent = Aqua,
                    size = 28.dp,
                    allDone = isFavorited,
                )
            }
        }
    }
}
