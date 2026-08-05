package com.rork.rockscout.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue

/**
 * Creates a [NestedScrollConnection] that prevents a scrollable column inside
 * a [ModalBottomSheet] from accidentally closing the sheet when the user
 * scrolls to the top/bottom edge.
 *
 * When the sheet is [SheetValue.Expanded] and the child scrollable has no more
 * content to consume (available == 0 before dispatch), we consume the drag
 * so the sheet's swipe-to-dismiss gesture never receives it. The sheet can
 * still be closed by dragging the grabber handle or tapping a close button.
 *
 * Usage:
 * ```
 * val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
 * val scrollFix = rememberSheetScrollFix(sheetState)
 * ModalBottomSheet(..., sheetState = sheetState) {
 *     Column(modifier = Modifier
 *         .nestedScroll(scrollFix)
 *         .verticalScroll(rememberScrollState())
 *     ) { ... }
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSheetScrollFix(sheetState: SheetState): NestedScrollConnection {
    return remember(sheetState) {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Only intercept drags when the sheet is fully expanded.
                // In Hidden or PartiallyExpanded states, let the sheet handle it.
                if (sheetState.currentValue != SheetValue.Expanded) return Offset.Zero

                // When the child scrollable can't scroll further in the drag
                // direction (available.y == 0), consume the delta so the sheet
                // doesn't start its swipe-to-dismiss gesture.
                return if (available.y == 0f && source == NestedScrollSource.Drag) {
                    // Consume a tiny amount to signal "I'm handling this"
                    // without actually moving anything — prevents the sheet
                    // from picking up the drag.
                    Offset(0f, 0.0001f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // After the child scrollable has consumed what it can,
                // any remaining delta would normally go to the sheet's
                // drag gesture. When expanded, consume it so the sheet
                // doesn't start closing.
                if (sheetState.currentValue != SheetValue.Expanded) return Offset.Zero
                if (source != NestedScrollSource.Drag) return Offset.Zero
                // Consume leftover vertical scroll to prevent sheet drag
                return available
            }
        }
    }
}

