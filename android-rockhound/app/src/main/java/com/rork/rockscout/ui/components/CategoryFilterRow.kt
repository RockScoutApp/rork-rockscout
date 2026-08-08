package com.rork.rockscout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.rork.rockscout.ui.components.glowingBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.rockscout.data.AssemblageSpecimens
import com.rork.rockscout.data.RockClass
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Ink

/**
 * Category filter options for specimen lists.
 * Used by SpecimenListScreen, CollectionScreen, WishlistScreen, and UserCollectionScreen.
 */
enum class ListCategoryFilter(
    val label: String,
    val rockClass: RockClass? = null,
    val isAssemblage: Boolean = false,
) {
    ALL("All"),
    ASSEMBLAGE("Assemblages", isAssemblage = true),
    CRYSTAL("Crystal / Gem", RockClass.CRYSTAL),
    FOSSIL("Fossil", RockClass.FOSSIL),
    IGNEOUS("Igneous", RockClass.IGNEOUS),
    METAMORPHIC("Metamorphic", RockClass.METAMORPHIC),
    MINERAL("Mineral", RockClass.MINERAL),
    SEDIMENTARY("Sedimentary", RockClass.SEDIMENTARY),
    ARTIFACTS("Artifacts"),
    WAR_RELICS("War Relics"),
}

/** Set of specimen IDs that are assemblage specimens. */
private val assemblageIds: Set<String> by lazy {
    AssemblageSpecimens.specimens.map { it.id }.toSet()
}

/** Filter a list of specimens by the given category filter. null = no filter (show all). */
fun filterSpecimensByCategory(
    specimens: List<Specimen>,
    filter: ListCategoryFilter?,
): List<Specimen> {
    if (filter == null) return specimens
    return when {
        filter.isAssemblage -> specimens.filter { it.id in assemblageIds }
        filter.rockClass != null -> specimens.filter { it.rockClass == filter.rockClass }
        else -> specimens
    }
}

/**
 * Reusable horizontal scrolling category filter row.
 *
 * @param selectedFilter currently selected filter, or null for "All"
 * @param onFilterSelected callback when a filter is tapped (null = "All" selected)
 * @param showNavigationEntry whether to show the "Unique formations" navigation entry at the end
 * @param onNavigate callback for the navigation entry
 */
@Composable
fun CategoryFilterRow(
    selectedFilter: ListCategoryFilter?,
    onFilterSelected: (ListCategoryFilter?) -> Unit,
    modifier: Modifier = Modifier,
    showNavigationEntry: Boolean = false,
    navigationLabel: String = "Unique formations",
    onNavigate: () -> Unit = {},
) {
    LazyRow(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ListCategoryFilter.entries.toTypedArray()) { filter ->
            val isSelected = filter == selectedFilter ||
                (filter == ListCategoryFilter.ALL && selectedFilter == null)
            val accent = if (filter.rockClass != null) {
                rockClassColor(filter.rockClass)
            } else if (filter.isAssemblage) {
                Citrine
            } else {
                Citrine
            }
            val filterShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .clip(filterShape)
                    .background(if (isSelected) accent else accent.copy(alpha = 0.12f))
                    .glowingBorder(1.dp, accent.copy(alpha = if (isSelected) 0.9f else 0.45f), filterShape)
                    .clickable {
                        onFilterSelected(if (isSelected) null else filter)
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = filter.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Ink else accent,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
        if (showNavigationEntry) {
            item {
                val navShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .clip(navShape)
                        .background(Citrine.copy(alpha = 0.12f))
                        .glowingBorder(1.dp, Citrine.copy(alpha = 0.45f), navShape)
                        .clickable { onNavigate() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = navigationLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = Citrine,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
