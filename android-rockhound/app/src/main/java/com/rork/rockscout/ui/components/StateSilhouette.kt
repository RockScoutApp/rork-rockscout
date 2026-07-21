package com.rork.rockscout.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.rork.rockscout.R

/**
 * Returns the drawable resource ID for a solid colored-in silhouette of the
 * given US state.  Pseudo-codes used in the BLM data set (CA2, CA3) are mapped
 * to their real state shape.
 */
fun stateSilhouetteResource(stateCode: String): Int = when (stateCode) {
        "AL" -> R.drawable.ic_state_al
        "AK" -> R.drawable.ic_state_ak
        "AZ" -> R.drawable.ic_state_az
        "AR" -> R.drawable.ic_state_ar
        "CA" -> R.drawable.ic_state_ca
        "CO" -> R.drawable.ic_state_co
        "CT" -> R.drawable.ic_state_ct
        "DE" -> R.drawable.ic_state_de
        "FL" -> R.drawable.ic_state_fl
        "GA" -> R.drawable.ic_state_ga
        "HI" -> R.drawable.ic_state_hi
        "ID" -> R.drawable.ic_state_id
        "IL" -> R.drawable.ic_state_il
        "IN" -> R.drawable.ic_state_in
        "IA" -> R.drawable.ic_state_ia
        "KS" -> R.drawable.ic_state_ks
        "KY" -> R.drawable.ic_state_ky
        "LA" -> R.drawable.ic_state_la
        "ME" -> R.drawable.ic_state_me
        "MD" -> R.drawable.ic_state_md
        "MA" -> R.drawable.ic_state_ma
        "MI" -> R.drawable.ic_state_mi
        "MN" -> R.drawable.ic_state_mn
        "MS" -> R.drawable.ic_state_ms
        "MO" -> R.drawable.ic_state_mo
        "MT" -> R.drawable.ic_state_mt
        "NE" -> R.drawable.ic_state_ne
        "NV" -> R.drawable.ic_state_nv
        "NH" -> R.drawable.ic_state_nh
        "NJ" -> R.drawable.ic_state_nj
        "NM" -> R.drawable.ic_state_nm
        "NY" -> R.drawable.ic_state_ny
        "NC" -> R.drawable.ic_state_nc
        "ND" -> R.drawable.ic_state_nd
        "OH" -> R.drawable.ic_state_oh
        "OK" -> R.drawable.ic_state_ok
        "OR" -> R.drawable.ic_state_or
        "PA" -> R.drawable.ic_state_pa
        "RI" -> R.drawable.ic_state_ri
        "SC" -> R.drawable.ic_state_sc
        "SD" -> R.drawable.ic_state_sd
        "TN" -> R.drawable.ic_state_tn
        "TX" -> R.drawable.ic_state_tx
        "UT" -> R.drawable.ic_state_ut
        "VT" -> R.drawable.ic_state_vt
        "VA" -> R.drawable.ic_state_va
        "WA" -> R.drawable.ic_state_wa
        "WV" -> R.drawable.ic_state_wv
        "WI" -> R.drawable.ic_state_wi
        "WY" -> R.drawable.ic_state_wy
        "CA2" -> R.drawable.ic_state_ca2
        "CA3" -> R.drawable.ic_state_ca3
        else -> 0
}

/** Colored-in state silhouette icon for BLM state tiles. */
@Composable
fun StateSilhouette(stateCode: String, accent: Color, modifier: Modifier = Modifier) {
    val resId = stateSilhouetteResource(stateCode)
    if (resId != 0) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = resId),
            contentDescription = "$stateCode state silhouette",
            tint = accent,
            modifier = modifier,
        )
    }
}
