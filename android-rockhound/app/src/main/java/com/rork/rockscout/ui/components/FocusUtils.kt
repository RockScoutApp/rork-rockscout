package com.rork.rockscout.ui.components

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

/**
 * Prevents this focusable composable from automatically receiving focus when it
 * first enters composition (e.g. when a dialog or screen opens). The cursor and
 * soft keyboard only appear after the user explicitly taps the field.
 */
fun Modifier.noAutoFocus(): Modifier = composed {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.freeFocus() }
    this.then(Modifier.focusRequester(focusRequester))
}
