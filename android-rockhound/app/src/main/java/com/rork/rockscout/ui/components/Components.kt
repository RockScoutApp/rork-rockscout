package com.rork.rockscout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import com.rork.rockscout.data.SeedData
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import com.rork.rockscout.data.RockClass
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.AuthRepository
import com.rork.rockscout.data.ImageModerator
import com.rork.rockscout.data.ImageReviewRepository
import com.rork.rockscout.data.ImageUtils
import com.rork.rockscout.data.ModerationTriState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.rork.rockscout.data.CapturedPhoto
import com.rork.rockscout.data.SavedImage
import com.rork.rockscout.data.Specimen
import com.rork.rockscout.data.SpecimenImages
import com.rork.rockscout.ui.theme.Amethyst
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.AquaDeep
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.CitrineDeep
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Igneous
import com.rork.rockscout.ui.theme.Metamorphic
import com.rork.rockscout.ui.theme.Ink
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Sedimentary
import com.rork.rockscout.ui.theme.Slate800
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.StoneLine
import com.rork.rockscout.R
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.DarkTextLow
import com.rork.rockscout.ui.theme.TextHigh
import com.rork.rockscout.ui.theme.TextLow
import com.rork.rockscout.ui.components.SculptedButton
import com.rork.rockscout.ui.components.sculpted
import com.rork.rockscout.ui.components.LongPressableImage
import com.rork.rockscout.ui.components.glowingBorder

/** URL for the full-page black volcanic rock background image. */
const val BLACK_ROCK_BACKGROUND_URL =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/142d5488-88c9-4b7f-b480-dd7c1e24f6a9.png"

/** URLs for element-category-specific background textures, keyed by category name.
 *  Each image represents the visual character of that element category. */
val ELEMENT_CATEGORY_BG: Map<String, String> = mapOf(
    "Alkali metal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e9e5fd32-828a-4a68-800f-b4333f51acbc.png",
    "Alkaline earth metal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/40dba593-bcaa-4138-9857-bf4e0c260a5b.png",
    "Transition metal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/f9d97072-531f-4e6b-87a8-71051d9163ec.png",
    "Post-transition metal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/2b072f54-2c7e-450a-98b8-7b99f4757636.png",
    "Metalloid" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/16a3e6f4-68af-4a9b-91f2-a8e8caf1569b.png",
    "Polyatomic nonmetal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/bfa93415-e941-4783-b155-bcd88c4dacc9.png",
    "Diatomic nonmetal" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3f7860d8-a1c9-4da5-a013-4db9a059a290.png",
    "Noble gas" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/f9b308ca-6915-433d-8f29-ea88a8286581.png",
    "Lanthanide" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/8b6b4ea4-0eaa-45cc-8f7e-ec3535e1d50e.png",
    "Actinide" to "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3fc1a1fd-476d-426d-885a-54413212ebd1.png",
    "Unknown" to BLACK_ROCK_BACKGROUND_URL,
)

/** Hero image URLs for the exploring informational tabs. */
const val METEORITE_HERO_URL =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/0e4b8440-e027-40a3-a45c-03464a032ebd.png"
const val BLM_HERO_URL =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/52dfa9da-d97b-4cb6-8652-95c591a0da90.png"
const val TECTONIC_HERO_URL =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e1f178b5-881d-4e0e-8417-9bf5838a0420.png"
const val GEM_MINERAL_HERO_URL =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e7703d3b-aee3-43a8-a6a9-378169a022d2.png"

/** Inline content images for the Meteorite Hunting tab. */
const val METEORITE_IMG_CHONDRITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/14e3a107-51e2-477f-ba18-daaf35d39b53.png"
const val METEORITE_IMG_WIDMANSTATTEN =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/499fef6e-35dd-4573-9cec-d6ddc9f0e30a.png"
const val METEORITE_IMG_PALLASITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/9d0795e4-18a8-4647-a761-362b174b1fb5.png"
const val METEORITE_IMG_DRY_LAKE_BED =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d00ed235-17c4-42a2-9aa2-e5c6bc679374.png"
const val METEORITE_IMG_MAGNET_CANE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/7ae2ef7e-ed50-433c-84bc-d1a055dc63ea.png"

/** Inline content images for the BLM Guide tab. */
const val BLM_IMG_ROCKHOUND =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/33ff15aa-1b28-4685-a9d1-0af2a1c9a6dc.png"
const val BLM_IMG_CANYON =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a2ce9843-2aef-4692-8bc4-dfd640d77cfc.png"
const val BLM_IMG_CAMPING =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/1771cc62-2c21-46e4-82b5-bf6df6bfea45.png"
const val BLM_IMG_TRAILHEAD =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/eae26efa-d7b6-4411-9838-64c29ae034be.png"
const val BLM_IMG_PANORAMIC =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a20b298e-4268-42c5-8b0c-f51e775c4678.png"

/** Inline content images for the Tectonics & Volcanoes tab. */
const val TECTONIC_IMG_DIVERGENT =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/2216369f-e4bc-49ac-b32b-ea1d535b895b.png"
const val TECTONIC_IMG_SUBDUCTION =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/029772c3-0dda-4df0-aeaa-0c877cf10159.png"
const val TECTONIC_IMG_TRANSFORM =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/e24d4352-d023-49fe-9753-02e5eebd15b6.png"
const val TECTONIC_IMG_HOTSPOT =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/15c8e2af-59a8-4496-9629-d47ded7aab8f.png"
const val TECTONIC_IMG_BASALT =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/bb45a3a0-c9c1-49ed-bd3b-92de4d0bebe2.png"

/** Inline content images for the Rock & Gem Resources tab. */
const val GEM_IMG_CUT_GEMS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d845c867-382a-43cc-9e35-e3e424d6285f.png"
const val GEM_IMG_MUSEUM =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/b220d2b5-b1cc-4302-832f-dabdd0359fb4.png"
const val GEM_IMG_LAPIDARY =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/ce4fde84-ac4a-47ef-b6aa-6f5d164405ba.png"
const val GEM_IMG_AMMONITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/36993054-0feb-4de6-b263-9d0f870606a3.png"
const val GEM_IMG_PEGMATITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/9ed6c263-a2a9-45e1-ad59-09116c69f17b.png"

// ── Book-style inline images for explorer/learn tabs ──────────────────────
// Small corner-positioned images placed within DarkCard text sections,
// sized to ~90dp so they don't dominate but are clearly visible.
// Used in RockInfo, Paleontology, PrehistoricOrganisms, and BLM screens.

const val GEO_IMG_STREAK_TEST =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/dbf1c798-8474-4c74-9b4c-37158d370898.png"
const val GEO_IMG_CRYSTAL_HABIT =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/dda688f4-8638-45d6-af14-592af5802b13.png"
const val GEO_IMG_MOHS_SCRATCH =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/acbfee49-7de8-4518-8071-d605ac419c1a.png"
const val GEO_IMG_ACID_TEST =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/bed6ba4b-f531-4cd6-ab6b-ae2b379aab4c.png"

const val PALEO_IMG_TRILOBITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4c86902a-c538-45df-81d0-a2fc16ef0a47.png"
const val PALEO_IMG_AMMONITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/513b7306-ae75-4097-9d4d-bcd594832700.png"
const val PALEO_IMG_DINO_TRACK =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/54cfa859-cab8-486a-aea0-8d5cb12d6dcc.png"
const val PALEO_IMG_PETRIFIED_WOOD =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/f9e96187-a4ef-4dbc-8746-0735d822bc21.png"
const val PALEO_IMG_STROMATOLITE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/f66533f4-8d40-47ee-9460-3889a0c2132a.png"

const val PREHISTORIC_IMG_ARCHAEOPTERYX =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/98fe48e5-e81e-4c93-afdc-aaedfb83ff31.png"
const val PREHISTORIC_IMG_TIKTAALIK =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/d9ef3c90-a34a-4103-9936-12d3b14360f5.png"
const val PREHISTORIC_IMG_SKELETON =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/24f080db-5f04-4c43-a124-3c6e5b9df11a.png"

const val BLM_IMG_ROCK_HAMMER =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/4bf5b02a-8b93-4cf6-8843-938cdfff99d5.png"
const val BLM_IMG_DESERT_VARNISH =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/48a62fbc-61ca-40ef-b783-de7516f46691.png"

/** Generated tile background images for the BLM guide landing page tiles. */
const val BLM_TILE_LAND =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/17521a06-df08-482c-982b-e4696066ea70.png"
const val BLM_TILE_PARKS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/9359a64f-062b-42c9-8054-118946d0ae6f.png"
const val BLM_TILE_TRAILHEADS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/10420553-ec06-4656-9e45-e48084261a8f.png"
const val BLM_TILE_CAMPGROUNDS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/b37f4a62-2469-43e3-9b50-5cc3d83b9ca4.png"

/** Generated tile background images for the Exploring Geology landing page tiles. */
const val GEO_TILE_ROCK_TYPES =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/5f8f6841-1281-4ee0-959e-d0d29ab713a8.png"
const val GEO_TILE_MINERAL_ID =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/214f8ec5-38bc-4ee8-82c3-a17f4fdfb003.png"
const val GEO_TILE_CRYSTALS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/91f4b09f-d566-4ae7-b599-0b6ec5a155dc.png"
const val GEO_TILE_ROCK_CYCLE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/a8439572-8abe-4f34-b578-cda46c9d240b.png"

/** Generated tile background images for the Exploring Paleontology landing page tiles. */
const val PALEO_TILE_TIME_SCALE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/87639397-231b-4581-a795-338f6cacb2d4.png"
const val PALEO_TILE_EXTINCTIONS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/c5fa8ea2-5b74-42f6-805b-a274b2affe66.png"
const val PALEO_TILE_FOSSIL_TYPES =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/0e31db28-1dc2-4a0c-85c7-b220112ed2ce.png"
const val PALEO_TILE_PERIODS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/09db815d-4bc0-4100-aa63-fe437ca66304.png"

/** Generated nature background for the BLM homepage tile. */
const val BLM_HOME_TILE_NATURE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/7f95720c-5737-487c-975f-fa5b1dbedf0a.png"

/** Generated tile background for the National / State Parks field kit tile. */
const val FIELD_KIT_TILE_PARKS =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/6d05636d-7ae8-4886-9789-ae62aecd18a2.png"

/** Generated tile background for the Campgrounds & Trailheads field kit tile. */
const val FIELD_KIT_TILE_TRAIL_CAMP =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/3224f2e2-4cb7-463e-8ba6-f0563b917f8f.png"

/** The Wave sandstone formation — used as the Natural Wonders tile background. */
const val NATURAL_WONDERS_TILE_WAVE =
    "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets/5eafcf10-07b7-47ec-acd2-bd068858b712.png"

/** Data for a navigation tile on a landing page (BLM guide, Geology, Paleontology).
 *  Mirrors the home screen's HomeTile but is public so any screen can use it. */
data class LandingTileData(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
    val imageUrl: String? = null,
)

/** A square navigation tile with a photo header, icon badge, label, and subtitle.
 *  Matches the home screen's DashboardTile design language. Used on the BLM guide,
 *  Geology, and Paleontology landing pages.
 *
 *  @param tile the tile data (label, subtitle, icon, accent, image)
 *  @param onClick called when the tile is tapped */
@Composable
fun LandingTile(
    tile: LandingTileData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "landingGlow")
    val glow by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            tween(2400 + (tile.label.hashCode() % 1000), delayMillis = 200),
            RepeatMode.Reverse,
        ),
        label = "landingGlowAlpha",
    )
    Box(
        modifier = modifier
            .height(175.dp)
            .sculpted(
                shape = RoundedCornerShape(20.dp),
                accent = tile.accent,
                shadowElevation = 8.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
                )
            )
            .glowingBorder(3.dp, tile.accent.copy(alpha = 0.55f), RoundedCornerShape(20.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(tile.accent.copy(alpha = 0.18f + glow), tile.accent.copy(alpha = 0.04f), Color.Transparent)
                    )
                ),
        )
        if (tile.imageUrl != null) {
            AsyncImage(
                model = tile.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.15f), Color(0xFF16140F).copy(alpha = 0.72f))
                        )
                    ),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF16140F).copy(alpha = 0.78f), Color(0xFF16140F).copy(alpha = 0.96f))
                    )
                )
                .padding(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(tile.accent.copy(alpha = 0.35f), tile.accent.copy(alpha = 0.10f))
                        )
                    )
                    .glowingBorder(2.dp, tile.accent.copy(alpha = 0.60f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(tile.icon, contentDescription = null, tint = tile.accent, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = tile.label,
                style = MaterialTheme.typography.titleSmall,
                color = Aqua,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = tile.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE8E0D0),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Book-style inline image — a small rounded image placed in a corner
 *  within a DarkCard's text content, like illustrations in a book.
 *  Sized to ~90dp so it's visible but doesn't dominate the text.
 *  Text wraps around it naturally via Row layout. Tapping opens the
 *  full-screen image viewer so readers can enlarge the illustration.
 *
 *  @param imageUrl the image URL to display
 *  @param contentDescription accessibility description
 *  @param alignment corner placement: TopStart, TopEnd, BottomStart, or BottomEnd
 *  @param modifier optional layout modifier
 */
@Composable
fun BookStyleImage(
    imageUrl: String,
    contentDescription: String,
    alignment: Alignment = Alignment.TopEnd,
    modifier: Modifier = Modifier,
    size: Dp = 92.dp,
) {
    var viewerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val request = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .size(184, 184)
            .build()
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1812))
            .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { viewerOpen = true },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
    if (viewerOpen) {
        FullScreenImageViewer(
            imageUrls = listOf(imageUrl),
            initialPage = 0,
            onDismiss = { viewerOpen = false },
        )
    }
}

/** Inline content image — a medium-sized rounded image placed sporadically
 *  between text sections in informational screens. Sized to match specimen
 *  card thumbnails (about 120-160dp tall, full width). Dark gradient overlay
 *  at the bottom keeps any optional caption legible. Tapping always opens the
 *  full-screen image viewer; pass [onClick] only when you need to override the
 *  tap behavior (e.g., navigation instead of enlargement). */
@Composable
fun InlineContentImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    caption: String? = null,
) {
    var viewerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val request = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build()
    }
    val baseModifier = if (onClick != null) {
        modifier.fillMaxWidth().clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth().clickable { viewerOpen = true }
    }
    Box(
        modifier = baseModifier
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1812))
            .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomStart,
    ) {
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        if (caption != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    ),
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

/** Full-screen dark volcanic rock background — very dark, almost black basalt
 *  texture with subtle mineral flecks. Used on element state pages and any screen
 *  that needs a darker, less colorful background than the agate slice. */
@Composable
fun BlackRockBackground(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val request = remember(BLACK_ROCK_BACKGROUND_URL) {
        ImageRequest.Builder(context)
            .data(BLACK_ROCK_BACKGROUND_URL)
            .crossfade(false)
            .size(Size.ORIGINAL)
            .build()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        coil3.compose.AsyncImage(
            model = request,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim — keeps text/icons legible over the volcanic rock texture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.45f),
                        )
                    )
                )
        )
        // Subtle ambient starburst sparkles over the volcanic texture —
        // low density and low alpha so they read as mineral flecks twinkling,
        // not a dominant star field. Sits behind all content.
        TwinklingStars(
            starCount = 30,
            maxAlpha = 0.45f,
        )
        content()
    }
}

/** Full-screen ambient background using the polished agate slice image.
 *  The image is cropped to fill the screen at high resolution and covered
 *  with a subtle dark scrim so the darker dashboard tiles and light text
 *  stay legible while the stone colors and tones remain visible. A low-density
 *  TwinklingStars layer adds ambient starburst sparkles over the agate. */
@Composable
fun RockBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.agate_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Dark scrim — keeps the agate colors visible while making tiles/text pop
        // Lightened slightly so the background reads a touch brighter and dark tiles pop.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.32f),
                            Color.Black.copy(alpha = 0.38f),
                            Color.Black.copy(alpha = 0.46f),
                        )
                    )
                )
        )
        // Subtle ambient starburst sparkles over the agate — low density and
        // low alpha so they read as mineral flecks catching the light, not a
        // dominant star field. Sits behind all content.
        TwinklingStars(
            starCount = 30,
            maxAlpha = 0.5f,
        )
        content()
    }
}

/** Draws a mineral-rich rock surface: dense grain speckles, crystalline sparkles
 *  (mica / quartz flecks), and thin stone veins. Seeded so the pattern is stable
 *  across recompositions and never hurts legibility. */
@Composable
private fun MineralTextureOverlay() {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val w = size.width
        val h = size.height
        // Deterministic pseudo-random generator so the texture stays still
        var seed = 1234567L
        fun nextFloat(): Float {
            seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
            return (seed.toFloat() / 0x7FFFFFFF.toFloat())
        }

        // ── Stone grain speckles ─────────────────────────────────────────────
        val speckleCount = 180
        val speckleColors = listOf(
            Color(0xFFB8A88A),
            Color(0xFFC9B89A),
            Color(0xFFD8C8A8),
            Color(0xFFA89B8C),
            Color(0xFFC0B095),
            Color(0xFF9E8B6D),
        )
        repeat(speckleCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val r = nextFloat() * 2.2f + 0.5f
            val alpha = nextFloat() * 0.16f + 0.06f
            val color = speckleColors[(seed % speckleColors.size).toInt()]
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = r,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }

        // ── Crystalline sparkles (mica / quartz flecks) ────────────────────────
        val sparkleCount = 28
        val sparkleColors = listOf(
            Color(0xFFFFF8E7).copy(alpha = 0.80f),
            Color(0xFFFDE9B8).copy(alpha = 0.70f),
            Color(0xFFE8F4F3).copy(alpha = 0.60f),
            Color(0xFFFFFFFF).copy(alpha = 0.75f),
        )
        repeat(sparkleCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val r = nextFloat() * 1.8f + 0.8f
            val color = sparkleColors[(seed % sparkleColors.size).toInt()]
            // Soft halo around each sparkle
            drawCircle(
                color = color.copy(alpha = color.alpha * 0.35f),
                radius = r * 2.4f,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
            // Bright core
            drawCircle(
                color = color,
                radius = r,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }

        // ── Veins — thin quartz-like lines in the stone ───────────────────────
        val veinCount = 12
        val veinColors = listOf(
            Color(0xFFD8C8A8).copy(alpha = 0.22f),
            Color(0xFFC9B89A).copy(alpha = 0.18f),
            Color(0xFFE8A33D).copy(alpha = 0.16f),
            Aqua.copy(alpha = 0.14f),
        )
        repeat(veinCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val len = nextFloat() * 120f + 40f
            val angle = nextFloat() * 6.28f
            val curve = nextFloat() * 40f - 20f
            val endX = x + kotlin.math.cos(angle) * len
            val endY = y + kotlin.math.sin(angle) * len
            val c1X = x + kotlin.math.cos(angle + 0.5f) * (len * 0.5f) + curve
            val c1Y = y + kotlin.math.sin(angle + 0.5f) * (len * 0.5f)
            val c2X = endX - kotlin.math.cos(angle - 0.5f) * (len * 0.5f) - curve
            val c2Y = endY - kotlin.math.sin(angle - 0.5f) * (len * 0.5f)
            val path = Path().apply {
                moveTo(x, y)
                cubicTo(c1X, c1Y, c2X, c2Y, endX, endY)
            }
            val stroke = nextFloat() * 1.2f + 0.4f
            val color = veinColors[(seed % veinColors.size).toInt()]
            drawPath(
                color = color,
                path = path,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    }
}

/** Gamer-style rock texture overlay — a dark, mineral-rich surface with dense
 *  grain speckles, glowing crystalline flecks (mica / quartz / amethyst), and
 *  thin neon-ish veins. Designed to sit on top of a dark gradient so text and
 *  UI elements stay legible while the surface reads as polished igneous stone.
 *  Seeded so the pattern is stable across recompositions. */
@Composable
fun GamerRockTexture(
    modifier: Modifier = Modifier,
    speckleTint: Color = Color(0xFFB8A88A),
    veinColors: List<Color> = listOf(
        Citrine.copy(alpha = 0.32f),
        Aqua.copy(alpha = 0.28f),
        Amethyst.copy(alpha = 0.24f),
        Color(0xFFD8C8A8).copy(alpha = 0.20f),
    ),
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        var seed = 7654321L
        fun nextFloat(): Float {
            seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
            return seed.toFloat() / 0x7FFFFFFF.toFloat()
        }

        // ── Dense stone grain speckles ────────────────────────────────────────
        val speckleCount = (w * h / 2800f).toInt().coerceIn(60, 220)
        val speckleColors = listOf(
            speckleTint,
            Color(0xFFC9B89A),
            Color(0xFFA89B8C),
            Color(0xFF9E8B6D),
            Color(0xFF7A6F5C),
        )
        repeat(speckleCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val r = nextFloat() * 2.0f + 0.4f
            val alpha = nextFloat() * 0.22f + 0.05f
            val color = speckleColors[(seed % speckleColors.size).toInt()]
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = r,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }

        // ── Glowing crystalline flecks (mica / quartz / amethyst) ───────────────
        val fleckCount = (w * h / 18000f).toInt().coerceIn(10, 45)
        val fleckColors = listOf(
            Color(0xFFFFF8E7).copy(alpha = 0.85f),
            Color(0xFFFDE9B8).copy(alpha = 0.75f),
            Color(0xFFE8F4F3).copy(alpha = 0.70f),
            Color(0xFFC9B8FF).copy(alpha = 0.65f),
            Color(0xFFFFD98A).copy(alpha = 0.80f),
        )
        repeat(fleckCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val r = nextFloat() * 1.6f + 0.6f
            val color = fleckColors[(seed % fleckColors.size).toInt()]
            // Soft glow halo
            drawCircle(
                color = color.copy(alpha = color.alpha * 0.30f),
                radius = r * 3.0f,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
            // Bright core
            drawCircle(
                color = color,
                radius = r,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }

        // ── Neon-ish mineral veins ─────────────────────────────────────────────
        val veinCount = (w * h / 24000f).toInt().coerceIn(6, 20)
        repeat(veinCount) {
            val x = nextFloat() * w
            val y = nextFloat() * h
            val len = nextFloat() * (maxOf(w, h) * 0.25f) + 30f
            val angle = nextFloat() * 6.28f
            val curve = nextFloat() * 50f - 25f
            val endX = x + kotlin.math.cos(angle) * len
            val endY = y + kotlin.math.sin(angle) * len
            val c1X = x + kotlin.math.cos(angle + 0.5f) * (len * 0.5f) + curve
            val c1Y = y + kotlin.math.sin(angle + 0.5f) * (len * 0.5f)
            val c2X = endX - kotlin.math.cos(angle - 0.5f) * (len * 0.5f) - curve
            val c2Y = endY - kotlin.math.sin(angle - 0.5f) * (len * 0.5f)
            val path = Path().apply {
                moveTo(x, y)
                cubicTo(c1X, c1Y, c2X, c2Y, endX, endY)
            }
            val stroke = nextFloat() * 1.4f + 0.5f
            val color = veinColors[(seed % veinColors.size).toInt()]
            drawPath(
                color = color,
                path = path,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    }
}

/** Standard screen wrapper with a back row title bar over the rock background.
 *  Includes built-in pull-to-refresh on all screens. */
@Composable
fun ScreenScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    background: @Composable (@Composable () -> Unit) -> Unit = { RockBackground(it) },
    titleStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    content: @Composable () -> Unit,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    background {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .sculpted(
                            shape = CircleShape,
                            accent = Aqua,
                            shadowElevation = 4.dp,
                            circular = true,
                            onClick = onBack,
                        )
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextHigh,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = title,
                    style = titleStyle,
                    color = Aqua,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                actions()
            }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    scope.launch {
                        delay(800)
                        com.rork.rockscout.data.AppRepository.instance.saveProfileChanges()
                        isRefreshing = false
                    }
                },
                // Use weight(1f) so the content area takes the remaining space below
                // the header, then apply imePadding and navigationBarsPadding so
                // scrollable content resizes when the keyboard opens and stays above
                // the system gesture nav bar.
                modifier = modifier
                    .weight(1f)
                    .imePadding()
                    .navigationBarsPadding(),
            ) {
                content()
            }
        }
    }
}

/** Compact Legal pill button used in screen headers to open the disclaimer. */
@Composable
fun LegalPillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .sculpted(
                shape = RoundedCornerShape(16.dp),
                accent = Citrine,
                shadowElevation = 5.dp,
                onClick = onClick,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.75f))
            .glowingBorder(2.dp, Citrine, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Gavel,
            contentDescription = null,
            tint = Citrine,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Legal",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Citrine,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

fun rockClassColor(rockClass: RockClass): Color = when (rockClass) {
    RockClass.IGNEOUS -> Igneous
    RockClass.SEDIMENTARY -> Sedimentary
    RockClass.METAMORPHIC -> Metamorphic
    RockClass.MINERAL -> Citrine
    RockClass.CRYSTAL -> Color(0xFF9B7BD8)
    RockClass.FOSSIL -> Fossil
}

/**
 * Subtle animated sparkles overlaid on SpecimenListItem cards, tinted by
 * rock class color. 14 sparkles with randomized positions seeded by specimen
 * id hash (stable across recompositions). Each twinkles independently with
 * low peak alpha (0.30–0.55) and small radii so readability is never impeded.
 * Draw-only Canvas — no pointerInput, so taps pass through to the card.
 */
@Composable
fun SpecimenSparkleOverlay(
    rockClass: RockClass,
    seedId: String,
    modifier: Modifier = Modifier,
) {
    val tint = rockClassColor(rockClass)
    val transition = rememberInfiniteTransition(label = "sparkle")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparkleProgress",
    )
    val sparkles = remember(seedId) {
        val hash = seedId.hashCode()
        val random = java.util.Random(hash.toLong())
        List(14) {
            SparkleData(
                x = random.nextFloat(),
                y = random.nextFloat(),
                phaseOffset = random.nextFloat(),
                driftAmp = 2f + random.nextFloat() * 3f,
                driftPhase = random.nextFloat() * 6.28f,
                peakAlpha = 0.30f + random.nextFloat() * 0.25f,
                coreRadius = 0.6f + random.nextFloat() * 1.0f,
            )
        }
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val t = progress.value
        for (s in sparkles) {
            val phase = (t + s.phaseOffset) % 1f
            val alpha = (kotlin.math.sin(phase * kotlin.math.PI * 2.0).toFloat() * 0.5f + 0.5f) * s.peakAlpha
            if (alpha < 0.02f) continue
            val drift = kotlin.math.sin((t * 6.28f + s.driftPhase).toDouble()).toFloat() * s.driftAmp
            val cx = s.x * w + drift
            val cy = s.y * h + drift * 0.5f
            val scale = 0.7f + alpha / s.peakAlpha * 0.3f
            val coreR = s.coreRadius * scale * density
            val haloR = coreR * 3f
            drawCircle(
                color = tint.copy(alpha = alpha * 0.15f),
                radius = haloR,
                center = Offset(cx, cy),
            )
            drawCircle(
                color = tint.copy(alpha = alpha),
                radius = coreR,
                center = Offset(cx, cy),
            )
        }
    }
}

private data class SparkleData(
    val x: Float,
    val y: Float,
    val phaseOffset: Float,
    val driftAmp: Float,
    val driftPhase: Float,
    val peakAlpha: Float,
    val coreRadius: Float,
)

/** Light, class-tinted card background gradient. Each rock class gets its own
 *  subtle color identity (warm, sandy, blue-gray, amber, purple, sepia) while
 *  staying dark enough at the bottom for the white title and dark text rows to
 *  remain legible. The top of the gradient carries the class tint; the bottom
 *  fades to a dark base so text contrast is preserved. */
fun rockClassCardGradient(rockClass: RockClass): List<Color> = when (rockClass) {
    RockClass.IGNEOUS -> listOf(Color(0xFF4A2A1F), Color(0xFF2A1810), Color(0xFF1E1208))
    RockClass.SEDIMENTARY -> listOf(Color(0xFF473820), Color(0xFF2A2010), Color(0xFF1E1608))
    RockClass.METAMORPHIC -> listOf(Color(0xFF2D3540), Color(0xFF1A2028), Color(0xFF12161E))
    RockClass.MINERAL -> listOf(Color(0xFF473320), Color(0xFF2A1E10), Color(0xFF1E1408))
    RockClass.CRYSTAL -> listOf(Color(0xFF362A40), Color(0xFF1E1828), Color(0xFF14101E))
    RockClass.FOSSIL -> listOf(Color(0xFF473A2A), Color(0xFF2A2014), Color(0xFF1E160A))
}

/** Maps a specimen's specific category string (e.g. "Silicate — Sorosilicate",
 *  "Carbonate mineral", "Extrusive volcanic rock") to a distinct color so each
 *  category group gets its own visually unique pill color on cards and detail screens. */
fun categoryColor(category: String): Color {
    val c = category.lowercase()
    return when {
        // — Mineral chemistry groups —
        c.startsWith("silicate") || c.contains("phyllosilicate serpentine") ||
            c.startsWith("silicate (") -> Color(0xFF26A69A)          // Teal
        c.startsWith("oxide") || c.startsWith("oxide ") -> Color(0xFFE65100) // Burnt orange
        c.startsWith("sulfide") || c.startsWith("sulfide ") -> Color(0xFFD4AF37) // Gold
        c.startsWith("carbonate") || c.contains("copper carbonate") ->
            Color(0xFF42A5F5)                                      // Sky blue
        c.startsWith("sulfate") -> Color(0xFFAB47BC)               // Lavender
        c.startsWith("halide") -> Color(0xFF66BB6A)                 // Mint green
        c.startsWith("phosphate") -> Color(0xFFEF5350)              // Coral pink
        c.startsWith("native element") -> Color(0xFFB0BEC5)         // Silver/platinum
        c.startsWith("borate") -> Color(0xFFEC407A)                 // Rose
        c.startsWith("arsenate") || c.startsWith("arsenide") ->
            Color(0xFFB71C1C)                                      // Dark red
        c.startsWith("tungstate") || c.startsWith("molybdate") ||
            c.startsWith("vanadate") || c.startsWith("chromate") ->
            Color(0xFF827717)                                      // Olive
        c.startsWith("biogenic mineral") || c.startsWith("biogenic gem") ->
            Color(0xFF80CBC4)                                      // Light teal

        // — Igneous / volcanic —
        c.startsWith("igneous") || c.startsWith("intrusive") ||
            c.startsWith("extrusive") || c.startsWith("volcanic") ||
            c.startsWith("pyroclastic") || c.startsWith("ultramafic") ->
            Color(0xFFFF5722)                                      // Fiery red-orange
        c.startsWith("alkaline igneous") || c.contains("alkaline") ->
            Color(0xFFFF8A65)                                      // Light coral

        // — Sedimentary —
        c.startsWith("sedimentary") || c.startsWith("clastic") ||
            c.startsWith("chemical") || c.startsWith("biogenic sed") ||
            c.contains("banded iron") -> Color(0xFF8D6E63)          // Earthy amber

        // — Metamorphic —
        c.startsWith("metamorphic") || c.startsWith("foliated") ||
            c.startsWith("non-foliated") || c.startsWith("contact meta") ||
            c.startsWith("fault-rock") || c.startsWith("high-grade") ||
            c.startsWith("low-grade") || c.startsWith("medium-grade") ||
            c.startsWith("metamorphosed") || c.startsWith("talc-rich") ->
            Color(0xFF7E57C2)                                      // Purple
        c.startsWith("rock (metamorphic") -> Color(0xFF7E57C2)     // Purple

        // — Fossils (by organism group) —
        c.startsWith("fossil") || c.startsWith("arthropod") ||
            c.startsWith("brachiopod") || c.startsWith("cephalopod") ||
            c.startsWith("cnidarian") || c.startsWith("echinoderm") ||
            c.startsWith("fish") || c.startsWith("mammal") ||
            c.startsWith("reptile") || c.startsWith("plant") ||
            c.startsWith("mollusk") || c.startsWith("hemichordate") ||
            c.startsWith("microfossil") || c.startsWith("microbial") ||
            c.startsWith("porifera") || c.startsWith("trace fossil") ||
            c.startsWith("coprolites") || c.startsWith("dinosaur") ->
            Color(0xFFA1887F)                                      // Sepia brown

        // — Meteorites —
        c.startsWith("meteorite") || c.startsWith("impact") ||
            c.contains("tektite") -> Color(0xFF607D8B)              // Steel blue-gray

        // — Organic —
        c.startsWith("organic") -> Color(0xFF558B2F)                // Forest green

        // — Opal / hydrated silica —
        c.startsWith("hydrated silica") -> Color(0xFF7986CB)        // Periwinkle

        // — Glass —
        c.startsWith("volcanic glass") || c.startsWith("natural glass") ||
            c.startsWith("man-made glass") -> Color(0xFFFFB300)     // Amber

        // — Assemblage / special groupings —
        c.startsWith("geode") || c.startsWith("pegmatite") ||
            c.startsWith("hydrothermal") || c.endsWith("assemblage") ||
            c.contains("assemblage") -> Color(0xFF3949AB)           // Indigo

        // — Copper / mixed ore —
        c.startsWith("copper") || c.startsWith("mixed copper") ||
            c.startsWith("sulfide ore") -> Color(0xFFD84315)        // Deep copper

        // — Anthropogenic / industrial / user —
        c.startsWith("anthropogenic") || c.startsWith("industrial") ||
            c.startsWith("user submitted") || c.startsWith("field guides") ->
            Color(0xFF757575)                                      // Gray

        // — Special phenomena —
        c.startsWith("enhydros") || c.contains("petroleum") ||
            c.contains("hydrocarbon") || c.contains("fluid inclusion") ->
            Color(0xFF6A1B9A)                                      // Deep purple
        c.startsWith("mineral inclusions") || c.startsWith("pseudomorph") ||
            c.startsWith("optical phenomena") || c.contains("chatoyancy") ->
            Color(0xFFC2185B)                                      // Magenta
        c.startsWith("copper-inclusion") -> Color(0xFFD84315)      // Deep copper
        c.startsWith("fluorescent") -> Color(0xFF00E5FF)           // Bright cyan
        c.startsWith("other amazing") -> Color(0xFF5C6BC0)         // Indigo-light

        // Fallback — use a warm amber
        else -> Color(0xFFB8860B)                                  // Dark goldenrod
    }
}

/** Extracts a short, pill-friendly label from a full category string.
 *  e.g. "Silicate — Sorosilicate" → "Silicate", "Carbonate mineral" → "Carbonate",
 *  "Extrusive volcanic rock" → "Volcanic", "Sulfide mineral" → "Sulfide". */
fun shortCategoryLabel(category: String): String {
    val dashIdx = category.indexOfAny(charArrayOf('\u2014', '\u2013', '-'))
    if (dashIdx > 0) return category.substring(0, dashIdx).trim()
    // No dash — grab the first word if it's a known prefix, otherwise first two words
    val words = category.trim().split(Regex("\\s+"))
    val first = words.firstOrNull()?.lowercase() ?: return category
    return when (first) {
        "silicate", "oxide", "sulfide", "carbonate", "sulfate", "halide",
        "phosphate", "borate", "arsenate", "arsenide", "tungstate", "molybdate",
        "vanadate", "chromate", "native", "biogenic", "hydrated",
        "fluorescent", "anthropogenic", "industrial", "user",
        "alkaline", "clastic", "chemical", "biogenic", "metamorphic",
        "foliated", "non-foliated", "contact", "fault-rock", "high-grade",
        "low-grade", "medium-grade", "metamorphosed", "talc-rich",
        "volcanic", "pyroclastic", "ultramafic", "igneous", "intrusive",
        "extrusive", "sedimentary", "rock", "meteorite", "impact",
        "organic", "trace", "coprolites", "dinosaur", "geode",
        "pegmatite", "hydrothermal", "copper", "mixed", "sulfide",
        "enhydros", "mineral", "pseudomorph", "optical", "copper-inclusion",
        "field", "other", "natural", "man-made" -> {
            // Take first two words for better context
            if (words.size >= 2 && first != "native" && first != "rock" &&
                first != "biogenic" && first != "volcanic" && first != "natural" &&
                first != "man-made" && first != "field" && first != "other" &&
                first != "igneous" && first != "intrusive" && first != "extrusive") {
                words.take(2).joinToString(" ")
            } else {
                words.first()
            }
        }
        else -> words.first()
    }
}

/** Brightens a color toward white if it's too dark for legible text on dark card
 *  backgrounds. Preserves the accent's hue identity while ensuring readability. */
fun brightenForText(color: Color, threshold: Float = 0.55f, amount: Float = 0.4f): Color {
    val lum = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (lum < threshold) {
        Color(
            color.red + (1f - color.red) * amount,
            color.green + (1f - color.green) * amount,
            color.blue + (1f - color.blue) * amount,
        )
    } else color
}

/** Small rounded label used for categories, rarity, hardness, etc. */
@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Citrine,
    filled: Boolean = false,
    textColor: Color? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (filled) color else color.copy(alpha = 0.14f))
            .glowingBorder(1.dp, color.copy(alpha = if (filled) 0.9f else 0.55f), shape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor ?: if (filled) Ink else brightenForText(color),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Pill-shaped "Search by State" button that opens a scrollable dropdown.
 *  Tapping a state name calls [onStateSelected] with that state's code.
 *  @param states List of state codes and display names to show in the dropdown.
 *  @param onStateSelected Called with the selected state code when a state is tapped.
 *  @param accentColor Border/accent color for the pill.
 *  @param modifier Layout modifier. */
@Composable
fun StatePickerPill(
    states: List<Pair<String, String>>,
    onStateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Aqua,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(accentColor.copy(alpha = 0.12f))
                .glowingBorder(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(50))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Search by State",
                    style = MaterialTheme.typography.labelMedium,
                    color = brightenForText(accentColor),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = brightenForText(accentColor),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF1A1812))
                .width(200.dp),
        ) {
            states.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHigh,
                        )
                    },
                    onClick = {
                        onStateSelected(code)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Compact rounded label for tight horizontal rows on specimen cards. */
@Composable
fun CompactTagChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Citrine,
    filled: Boolean = false,
    textColor: Color? = null,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (filled) color else color.copy(alpha = 0.14f))
            .glowingBorder(1.dp, color.copy(alpha = if (filled) 0.9f else 0.50f), shape)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor ?: if (filled) Ink else brightenForText(color),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Clean horizontal rarity indicator — small colored dot + label text.
 *  Replaces the bulky TagChip for rarity display on specimen cards.
 *  Pass [textFontSize] to enlarge the label (e.g. on redesigned specimen cards). */
@Composable
fun RarityIndicator(
    rarity: String,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    textColor: Color = DarkTextMid,
    dotSize: Dp = 7.dp,
    textFontSize: TextUnit? = null,
) {
    val rarityColor = when {
        rarity.contains("Rare", ignoreCase = true) && !rarity.contains("Uncommon", ignoreCase = true) ->
            Color(0xFFE2574C)
        rarity.contains("Uncommon", ignoreCase = true) ->
            Color(0xFFE8A33D)
        else -> Color(0xFF5CC98C)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(rarityColor)
                .glowingBorder(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
        )
        Text(
            text = rarity,
            style = if (textFontSize != null) {
                MaterialTheme.typography.labelMedium.copy(fontSize = textFontSize)
            } else {
                MaterialTheme.typography.labelMedium
            },
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

/** Dark-themed clickable card matching the homepage dashboard tiles.
 *  Deep dark gradient background with accent-colored border and glow overlay. */
@Composable
fun DarkCard(
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    solidBackground: Boolean = false,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    val backgroundModifier = if (solidBackground) {
        Modifier.background(Color(0xFF16140F))
    } else {
        Modifier.background(
            Brush.verticalGradient(
                listOf(Color(0xFF2A2820), Color(0xFF1E1C16), Color(0xFF16140F))
            )
        )
    }
    Box(
        modifier = modifier
            .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
            .clip(shape)
            .then(backgroundModifier)
            .glowingBorder(BorderStroke(3.dp, accent.copy(alpha = 0.50f)), shape)
    ) {
        // Accent glow overlay at top (only when using the gradient background)
        if (!solidBackground) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
            )
        }
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/** A glassy surface card with a subtle stone border. */
@Composable
fun StoneCard(
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(3.dp, StoneLine.copy(alpha = 0.85f)),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .sculpted(shape = shape, accent = StoneLine, shadowElevation = 8.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Slate800.copy(alpha = 0.7f), Slate900.copy(alpha = 0.9f))
                )
            )
            .glowingBorder(border, shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

/** Circular emoji "specimen" badge with a colored ring matching its class. */
@Composable
fun SpecimenGlyph(
    emoji: String,
    accent: Color,
    size: Int = 52,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.30f), accent.copy(alpha = 0.06f))
                )
            )
            .glowingBorder(2.dp, accent.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
    }
}

/** Gold "NEW" badge pill — shown for catalog entries added within the last 7 days.
 *  Shared by all card types: specimens, artifacts, dig sites, state parks, trailheads, campgrounds.
 *  Matches the original inline badge style: gold gradient, glowingBorder, ExtraBold 9sp "NEW" text. */
@Composable
fun NewBadge(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                )
            )
            .glowingBorder(1.dp, Color(0xFFFFD54F).copy(alpha = 0.9f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "NEW",
            color = Color(0xFF1A1306),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp,
        )
    }
}

/** Square thumbnail for a specimen card — shows the main image, or the emoji fallback. */
@Composable
fun SpecimenThumbnail(
    specimen: Specimen,
    accent: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    thumbnailUrl: String? = null,
    onClick: () -> Unit = {},
    showBorder: Boolean = false,
) {
    val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls
    val displayUrls = (thumbnailUrl?.let { listOf(it) } ?: imageUrls).filter { it.isNotBlank() }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812))
                )
            )
            .then(
                if (showBorder) Modifier.glowingBorder(2.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (displayUrls.isEmpty()) {
            Text(
                text = specimen.emoji,
                style = MaterialTheme.typography.titleLarge,
            )
        } else {
            LongPressableImage(
                model = displayUrls.first(),
                contentDescription = specimen.name,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop,
                onClick = onClick,
            )
        }
    }
}

/** Maps a [RockClass] to its Formation Environment badge metadata:
 *  an emoji + short label + class-tinted color. Used by [FormationEnvironmentBadge]
 *  on specimen cards to visually distinguish volcanic, sedimentary, and metamorphic
 *  (plus mineral, crystal, and fossil) formation environments. */
private fun formationEnvironmentMeta(rockClass: RockClass): Triple<String, String, Color> =
    when (rockClass) {
        RockClass.IGNEOUS     -> Triple("\uD83C\uDF0B", "Volcanic",    Igneous)
        RockClass.SEDIMENTARY -> Triple("\uD83C\uDFD7\uFE0F", "Sedimentary", Sedimentary)
        RockClass.METAMORPHIC -> Triple("\u26F0\uFE0F", "Metamorphic", Metamorphic)
        RockClass.MINERAL     -> Triple("\uD83E\uDEA8", "Mineral",     Citrine)
        RockClass.CRYSTAL     -> Triple("\uD83D\uDC8E", "Crystal",     Amethyst)
        RockClass.FOSSIL      -> Triple("\uD83E\uDEA5", "Fossil",      Fossil)
    }

/** Prominent "Formation Environment" badge for a specimen card — an icon+label
 *  pill tinted with the specimen's class color (volcanic orange, sedimentary gold,
 *  metamorphic slate-blue, mineral amber, crystal purple, fossil sepia).
 *  Sits below the title on the same row as the category text so the card's
 *  formation environment is immediately visible at a glance. */
@Composable
fun FormationEnvironmentBadge(
    rockClass: RockClass,
    modifier: Modifier = Modifier,
) {
    val (emoji, label, classColor) = formationEnvironmentMeta(rockClass)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(classColor.copy(alpha = 0.18f))
            .glowingBorder(1.dp, classColor.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = brightenForText(classColor, amount = 0.30f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Fixed-size tagline text rendered at the larger body size.
 *  The text color matches the card's glowing border accent so the tagline
 *  reads as part of the card's color identity, sitting directly on the card
 *  background with no shadow box behind it. */
@Composable
fun AutoSizeTaglineText(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 17.sp,
        color = brightenForText(accent, amount = 0.25f),
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Medium,
        maxLines = 4,
        overflow = TextOverflow.Visible,
    )
}

/** Horizontal specimen card with thumbnail, stacked text rows, a dark tagline box
 *  at the bottom-left, and a compact heart + add/share button stack at the
 *  bottom-right corner. Cards fill the full available width.
 *
 *  The specimen thumbnail sits on the left with a subtle accent border. Text content
 *  (title, type pill, location rows, rarity) is aligned in a single vertical stack.
 *  The tagline sits in a dark semi-transparent rounded box at the bottom-left. The
 *  heart icon and compact circular Add/Share button are stacked vertically at the
 *  far bottom-right corner, ~1dp from the card borders, with 1dp between them. */
/** A single location chip used inside the specimen card's two-row locations
 *  block. A pin icon + enlarged location text, capped to one line. */
@Composable
private fun LocationChipRow(location: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = DarkTextMid,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = location,
            style = MaterialTheme.typography.labelMedium,
            color = DarkTextMid,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpecimenListItem(
    specimen: Specimen,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onImageClick: () -> Unit = {},
    title: String = specimen.name,
    subtitle: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
    addShare: @Composable (() -> Unit)? = null,
    heart: @Composable (() -> Unit)? = null,
    showCategory: Boolean = false,
    categoryLabel: String = shortCategoryLabel(specimen.category),
    imageSize: Dp = 48.dp,
    thumbnailUrl: String? = null,
    showTagline: Boolean = true,
    heartSize: Dp = 44.dp,
    addShareHeight: Dp = 44.dp,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: (() -> Unit)? = null,
) {
    val hasHeart = heart != null
    val hasAddShare = addShare != null
    val hasTagline = showTagline && specimen.tagline.isNotBlank()
    val hasButtons = hasHeart || hasAddShare
    val hasBottomSection = hasTagline || hasButtons

    val imageUrls = SpecimenImages.urls[specimen.id] ?: specimen.imageUrls
    val displayUrls = (thumbnailUrl?.let { listOf(it) } ?: imageUrls).filter { it.isNotBlank() }
    val imageHeight = (imageSize.value * 1.31f).dp

    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .sculpted(shape = shape, accent = accent, shadowElevation = 6.dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(rockClassCardGradient(specimen.rockClass))
                )
                .glowingBorder(3.dp, accent.copy(alpha = 0.50f), shape)
                .clickable {
                    if (selectionMode && onToggleSelection != null) onToggleSelection() else onClick()
                },
        ) {
            // Accent glow overlay at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.15f), Color.Transparent)
                        )
                    ),
            )

            // Animated class-colored sparkles
            SpecimenSparkleOverlay(
                rockClass = specimen.rockClass,
                seedId = specimen.id,
                modifier = Modifier.fillMaxSize(),
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // ── Image (left) + 5 evenly-spaced rows (right) ──
                // The image sets a fixed height; the right column fills that height
                // and is split into 5 equal-weight rows so top/bottom align to the
                // image edges and rows stay evenly spaced regardless of name length.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight + 12.dp)
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Selection checkbox on the left when in selection mode.
                    if (selectionMode && onToggleSelection != null) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelection() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = accent,
                                uncheckedColor = DarkTextLow,
                                checkmarkColor = Color(0xFF1C1A14),
                            ),
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    // Tall image — fixed size, bottom edge stays anchored
                    Box(
                        modifier = Modifier
                            .width(imageSize)
                            .height(imageHeight)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(accent.copy(alpha = 0.28f), Color(0xFF1A1812))
                                )
                            )
                            .glowingBorder(2.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (displayUrls.isNotEmpty()) {
                            LongPressableImage(
                                model = displayUrls.first(),
                                contentDescription = specimen.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onClick = onImageClick,
                            )
                        } else {
                            Text(
                                text = specimen.emoji,
                                style = MaterialTheme.typography.displaySmall,
                            )
                        }
                        // NEW badge — shown for specimens added to the catalog within the last 7 days.
                        // Matches the ArtifactListItem badge style exactly (gold gradient, glowingBorder,
                        // ExtraBold 9sp "NEW" text, TopStart alignment, 4dp padding).
                        if (specimen.isNew()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                                        )
                                    )
                                    .glowingBorder(1.dp, Color(0xFFFFD54F).copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "NEW",
                                    color = Color(0xFF1A1306),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp,
                                )
                            }
                        }
                    }

                    // Right column: 5 evenly-spaced rows, each weight(1f),
                    // content vertically centered within its row.
                    // Row 1: name line 1
                    // Row 2: name line 2 (blank if name fits on one line)
                    // Row 3: type pill
                    // Row 4: two location pills
                    // Row 5: rarity pill (aligned with image bottom)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(imageHeight),
                    ) {
                        // Row 1 — name line 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = title.specimenLineOne(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                lineHeight = 22.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        // Row 2 — name line 2 (blank if name is one line)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            val second = title.specimenLineTwo()
                            if (second.isNotEmpty()) {
                                Text(
                                    text = second,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    lineHeight = 22.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Row 3 — type pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (showCategory) {
                                val (emoji, _, classColor) = formationEnvironmentMeta(specimen.rockClass)
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(classColor.copy(alpha = 0.16f))
                                        .glowingBorder(1.dp, classColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 9.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = emoji,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        text = categoryLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = brightenForText(classColor, amount = 0.30f),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            } else {
                                FormationEnvironmentBadge(
                                    rockClass = specimen.rockClass,
                                )
                            }
                        }

                        // Row 4 — two famous-location pills in one horizontal row
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            val locations = specimen.whereFound.filter { it.isNotBlank() }.take(2)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                locations.forEach { loc ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Aqua.copy(alpha = 0.16f))
                                            .glowingBorder(1.dp, Aqua.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 9.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Aqua,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Text(
                                            text = loc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = brightenForText(Aqua),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }

                        // Row 5 — rarity pill (aligned with image bottom)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            val rColor = specimenRarityColor(specimen.rarity)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(rColor)
                                    .glowingBorder(1.dp, rColor.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = specimen.rarity,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Ink,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                // ── Subtitle + trailing row (thin strip between image and tagline) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 16.dp, top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    subtitle()
                    Spacer(Modifier.weight(1f))
                    if (!hasHeart) {
                        trailing()
                    }
                }

                // ── Tagline (left, class-colored) + heart/add (right) ──
                if (hasBottomSection) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (hasTagline) {
                                AutoSizeTaglineText(
                                    text = specimen.tagline,
                                    accent = accent,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            } else {
                                Spacer(Modifier.fillMaxSize())
                            }
                        }

                        if (hasButtons) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                if (hasHeart) {
                                    heart()
                                }
                                if (hasAddShare) {
                                    addShare()
                                }
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

/** Split a name into its first line for the 2-row name layout. */
private fun String.specimenLineOne(): String {
    val trimmed = trim()
    return if (trimmed.length <= 14) trimmed else trimmed.substringBefore(' ', trimmed)
}

/** Second line of the name for the 2-row name layout (empty if it fits on one line). */
private fun String.specimenLineTwo(): String {
    val trimmed = trim()
    if (trimmed.length <= 14) return ""
    val first = trimmed.substringBefore(' ', trimmed)
    val rest = trimmed.substring(first.length).trim()
    return if (rest.isEmpty()) "" else rest
}

/** Map rarity string to color for the filled rarity pill. */
private fun specimenRarityColor(rarity: String): Color {
    val r = rarity.lowercase()
    return when {
        r.contains("very rare") -> Color(0xFFB71C1C)
        r.contains("rare") && !r.contains("uncommon") -> Color(0xFFE2574C)
        r.contains("uncommon") -> Color(0xFFE8A33D)
        else -> Color(0xFF5CC98C)
    }
}

/** A single zoomable photo used by [FullScreenImageViewer] and
 *  [StandaloneZoomableImageViewer]. Handles pinch-to-zoom, pan, tap-to-dismiss,
 *  tap-to-reset, and long-press-to-save.
 *
 *  State (scale, offset) is kept internally so the gesture lambdas always read
 *  live values — passing scale as a parameter would capture a stale 1f inside
 *  the pointerInput coroutine, breaking pinch-zoom and pan.
 *
 *  Pinch is the only zoom gesture — double-tap-to-zoom is intentionally omitted
 *  so users inspect textures deliberately with two fingers. */
@Composable
private fun ZoomablePhoto(
    url: String,
    contentDescription: String,
    onDismiss: () -> Unit,
    onLongPress: () -> Unit,
) {
    val context = LocalContext.current
    val request = remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build()
    }
    val painter = rememberAsyncImagePainter(request)

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else onDismiss()
                    },
                    onLongPress = { onLongPress() },
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 6f)
                    scale = newScale
                    if (newScale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY,
                ),
            contentScale = ContentScale.Fit,
        )
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
            }
            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Image failed to load",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
            else -> Unit
        }
    }
}

/** Full-screen image viewer — opens a single specimen photo at high resolution
 *  with pinch-to-zoom, pan, and long-press-to-save. Users tap each image
 *  individually to enlarge it; there is no swipe-to-page, no left/right arrows,
 *  and no page counter so zoom and pan gestures are never confused with paging. */
@Composable
fun FullScreenImageViewer(
    imageUrls: List<String>,
    initialPage: Int = 0,
    onDismiss: () -> Unit,
) {
    if (imageUrls.isEmpty()) return

    val selectedUrl = imageUrls.getOrNull(initialPage.coerceIn(0, imageUrls.lastIndex)) ?: imageUrls.first()

    var longPressSaveUrl by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
        ) {
            ZoomablePhoto(
                url = selectedUrl,
                contentDescription = "Specimen photo",
                onDismiss = onDismiss,
                onLongPress = { longPressSaveUrl = selectedUrl },
            )

            // Close button — top left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Color.White,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onDismiss,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }



        }

        // Long-press-to-save dialog — same "Save photo?" flow used by LongPressableImage.
        longPressSaveUrl?.let { url ->
            LongPressSaveDialog(
                imageUrl = url,
                onDismiss = { longPressSaveUrl = null },
            )
        }
    }
}

/** Standalone single-image zoomable viewer — opens ONE photo full-screen with
 *  no swipe-to-next behavior. Each enlarged image has its own independent pinch-
 *  to-zoom and pan state, so zooming one photo never affects another. There are no
 *  pager dots, left/right arrows, or "1 / 2" counter because there is only one
 *  image at a time (master build task O).
 *
 *  The viewer is a full-screen black overlay with a centered image, a close button
 *  top-left, and a long-press "Save photo?" action mirroring [FullScreenImageViewer]. */
@Composable
fun StandaloneZoomableImageViewer(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    if (imageUrl.isBlank()) return

    var longPressSaveUrl by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
        ) {
            ZoomablePhoto(
                url = imageUrl,
                contentDescription = "Specimen photo",
                onDismiss = onDismiss,
                onLongPress = { longPressSaveUrl = imageUrl },
            )

            // Close button — top left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
                    .size(44.dp)
                    .sculpted(
                        shape = CircleShape,
                        accent = Color.White,
                        shadowElevation = 5.dp,
                        circular = true,
                        onClick = onDismiss,
                    )
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        longPressSaveUrl?.let { url ->
            LongPressSaveDialog(
                imageUrl = url,
                onDismiss = { longPressSaveUrl = null },
            )
        }
    }
}

/** "Save photo?" dialog used by [FullScreenImageViewer] and [StandaloneZoomableImageViewer]
 *  when a user long-presses an enlarged image. Mirrors the save flow in
 *  [LongPressableImage]: saves to the device gallery and adds the URL to the
 *  in-app My Saved Images collection. */
@Composable
private fun LongPressSaveDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var showToast by remember { mutableStateOf(false) }

    if (!showToast) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Save photo?",
                    color = Aqua,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "This will save the image to your gallery and add it to My Saved Images.",
                    color = TextHigh,
                )
            },
            confirmButton = {
                SculptedButton(
                    text = "Save photo",
                    onClick = {
                        onDismiss()
                        scope.launch {
                            val result = saveImageToGalleryAndCollection(context, imageUrl)
                            saveMessage = result
                            showToast = true
                            // Show snackbar toast for successful save
                            if (result?.contains("saved", ignoreCase = true) == true || result == null) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Image saved to gallery",
                                    android.widget.Toast.LENGTH_SHORT,
                                ).show()
                            }
                            kotlinx.coroutines.delay(2500)
                            showToast = false
                        }
                    },
                    accent = Citrine,
                    containerColor = Citrine,
                    textColor = Ink,
                    shape = RoundedCornerShape(12.dp),
                    icon = Icons.Filled.Download,
                )
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = DarkTextMid)
                }
            },
            containerColor = Slate900,
        )
    }
}

/** A labelled stat shown in detail screens (e.g. Hardness · 7).
 *  Clean two-column layout: subtle dot, muted label, and a right-aligned
 *  value that wraps neatly within its own weighted column. */
@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = Citrine,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Small accent dot
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.85f))
                    .glowingBorder(1.dp, accent.copy(alpha = 0.35f), CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                modifier = Modifier.width(110.dp),
            )
            // Value — aligned right, wraps within its own column
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextHigh,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.weight(1f),
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
    }
}

/** Reusable lockout banner shown on gated screens (field captures, wishlist,
 *  my rocks, favorite spots) when a free user's 1-week trial has expired and
 *  no donated full-feature unlock is active. Routes the user to the paywall.
 *
 *  Phase 8 strict lockout — educational browsing and field-capture share-only
 *  mode stay open; everything else locks. */
@Composable
fun LockedFeatureBanner(
    onSubscribe: () -> Unit,
    modifier: Modifier = Modifier,
    message: String = "Your 1-week trial has ended. Subscribe or donate to unlock this feature.",
) {
    DarkCard(
        accent = Citrine,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape)
                    .background(Citrine.copy(alpha = 0.18f))
                    .glowingBorder(1.dp, Citrine.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Text("\uD83D\uDD12", style = MaterialTheme.typography.headlineMedium) }
            Spacer(Modifier.height(12.dp))
            Text(
                "Locked",
                style = MaterialTheme.typography.titleLarge,
                color = Citrine,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            SculptedButton(
                text = "Subscribe or Donate",
                onClick = onSubscribe,
                accent = Citrine,
                containerColor = Citrine,
                textColor = androidx.compose.ui.graphics.Color.Black,
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

/** Modal dialog that lets the user pick a photo from their in-app Saved Images collection.
 *  Tapping an image immediately calls [onImageSelected] with the chosen [SavedImage]. */
@Composable
fun SavedImagesPickerDialog(
    onDismiss: () -> Unit,
    onImageSelected: (SavedImage) -> Unit,
) {
    val savedImages by AppRepository.instance.savedImages.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = Slate900,
        title = { Text("Pick from Saved Images", style = MaterialTheme.typography.headlineSmall, color = Aqua) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                if (savedImages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No saved images yet. Long-press any photo in the app to save one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMid,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(savedImages, key = { it.id }) { image ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1A1812))
                                    .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .clickable { onImageSelected(image) },
                                contentAlignment = Alignment.Center,
                            ) {
                                AsyncImage(
                                    model = image.url,
                                    contentDescription = "Saved image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextMid)
            }
        },
    )
}

/** Modal dialog that lets the user pick a photo from one of three in-app sources:
 *  phone gallery, Saved Images, or Field Captures. The selected image is returned as a URI. */
@Composable
fun ImageSourcePickerDialog(
    onImageSelected: (android.net.Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val savedImages by AppRepository.instance.savedImages.collectAsStateWithLifecycle()
    val captures by AppRepository.instance.captures.collectAsStateWithLifecycle()
    var mode by remember { mutableStateOf<ImageSourcePickerMode?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        containerColor = Slate900,
        title = {
            Text(
                when (mode) {
                    ImageSourcePickerMode.SAVED_IMAGES -> "Pick from Saved Images"
                    ImageSourcePickerMode.FIELD_CAPTURES -> "Pick from Field Captures"
                    null -> "Choose photo source"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = Aqua,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                when (mode) {
                    null -> ImageSourceOptions(
                        onGallery = { galleryLauncher.launch("image/*") },
                        onSavedImages = { mode = ImageSourcePickerMode.SAVED_IMAGES },
                        onFieldCaptures = { mode = ImageSourcePickerMode.FIELD_CAPTURES },
                    )
                    ImageSourcePickerMode.SAVED_IMAGES -> SavedImagesGrid(
                        savedImages = savedImages,
                        onImageSelected = { image ->
                            val uri = image.localUri?.let { android.net.Uri.parse(it) }
                                ?: android.net.Uri.parse(image.url)
                            onImageSelected(uri)
                        },
                        emptyMessage = "No saved images yet. Long-press any photo in the app to save one.",
                    )
                    ImageSourcePickerMode.FIELD_CAPTURES -> FieldCapturesGrid(
                        captures = captures,
                        onImageSelected = { capture ->
                            val uriString = capture.imageUris.firstOrNull()
                            if (uriString != null) {
                                onImageSelected(android.net.Uri.parse(uriString))
                            }
                        },
                        emptyMessage = "No field captures yet. Use the Field Camera tile to snap photos.",
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { if (mode == null) onDismiss() else { mode = null } }) {
                Text(if (mode == null) "Cancel" else "Back", color = DarkTextMid)
            }
        },
    )
}

private enum class ImageSourcePickerMode { SAVED_IMAGES, FIELD_CAPTURES }

@Composable
private fun ImageSourceOptions(
    onGallery: () -> Unit,
    onSavedImages: () -> Unit,
    onFieldCaptures: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ImageSourceOptionCard(
            icon = Icons.Filled.PhotoLibrary,
            label = "Phone Gallery",
            subtitle = "Choose from your device",
            accent = Citrine,
            onClick = onGallery,
        )
        ImageSourceOptionCard(
            icon = Icons.Filled.Download,
            label = "Saved Images",
            subtitle = "Photos you've saved in the app",
            accent = Aqua,
            onClick = onSavedImages,
        )
        ImageSourceOptionCard(
            icon = Icons.Filled.PhotoCamera,
            label = "Field Captures",
            subtitle = "Photos from your field camera",
            accent = Color(0xFF00E5C9),
            onClick = onFieldCaptures,
        )
    }
}

@Composable
private fun ImageSourceOptionCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1812))
            .glowingBorder(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.15f))
                .glowingBorder(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = accent, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = DarkTextMid)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 180f))
    }
}

@Composable
private fun SavedImagesGrid(
    savedImages: List<SavedImage>,
    onImageSelected: (SavedImage) -> Unit,
    emptyMessage: String,
) {
    if (savedImages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(savedImages, key = { it.id }) { image ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { onImageSelected(image) },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = image.url,
                        contentDescription = "Saved image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldCapturesGrid(
    captures: List<CapturedPhoto>,
    onImageSelected: (CapturedPhoto) -> Unit,
    emptyMessage: String,
) {
    if (captures.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextMid,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(captures, key = { it.id }) { capture ->
                val firstUri = capture.imageUris.firstOrNull()
                val specimenName = remember(capture.specimenId) {
                    SeedData.specimenById(capture.specimenId)?.name ?: "Field Capture"
                }
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1812))
                        .glowingBorder(1.dp, Color(0xFF1A1812).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { onImageSelected(capture) }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0C08)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (firstUri != null) {
                            AsyncImage(
                                model = firstUri,
                                contentDescription = specimenName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text(capture.specimenEmoji, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        capture.displayName(specimenName),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextHigh,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/** Copies a saved image into the requested destination folder, runs it through the same
 *  moderation pipeline as a gallery pick, and returns the persistent path (or null if
 *  it was rejected). */
suspend fun processSavedImage(
    context: android.content.Context,
    savedImage: SavedImage,
    subdir: String,
    type: String,
): String? = processImageUri(
    context,
    if (savedImage.localUri != null) android.net.Uri.parse(savedImage.localUri) else android.net.Uri.parse(savedImage.url),
    subdir,
    type,
)

/** Copies any image URI into the requested destination folder, runs it through the same
 *  moderation pipeline, and returns the persistent path (or null if rejected). */
suspend fun processImageUri(
    context: android.content.Context,
    uri: android.net.Uri,
    subdir: String,
    type: String,
): String? {
    val persistentPath = ImageUtils.copyUriToInternalStorage(context, uri, subdir) ?: return null

    val base64 = ImageUtils.uriToModerationBase64(context, android.net.Uri.parse(persistentPath))
    if (base64 == null) {
        android.widget.Toast.makeText(context, "Could not load image.", android.widget.Toast.LENGTH_SHORT).show()
        return null
    }
    val verdict = ImageModerator.scan(base64, "image/jpeg")
    return when (verdict.triState) {
        ModerationTriState.CLEAN -> persistentPath
        ModerationTriState.EXPLICIT -> {
            android.widget.Toast.makeText(
                context,
                verdict.reason.ifBlank { "This image can't be used because it violates our family-friendly policies." },
                android.widget.Toast.LENGTH_LONG,
            ).show()
            null
        }
        ModerationTriState.QUESTIONABLE -> {
            val userId = AuthRepository.instance.currentUserId
            val userName = AppRepository.instance.profile.value.name
            val avatar = AppRepository.instance.profile.value.avatarEmoji
            ImageReviewRepository.instance.submitReview(
                userId = userId ?: "unknown",
                userName = userName,
                userAvatar = avatar,
                imageUri = persistentPath,
                type = type,
                reason = verdict.reason,
            )
            android.widget.Toast.makeText(
                context,
                "Image submitted for review. It'll be visible once approved.",
                android.widget.Toast.LENGTH_LONG,
            ).show()
            persistentPath
        }
    }
}
