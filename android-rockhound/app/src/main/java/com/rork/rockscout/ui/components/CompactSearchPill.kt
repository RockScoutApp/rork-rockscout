package com.rork.rockscout.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.DarkTextHigh
import com.rork.rockscout.ui.theme.DarkTextMid
import com.rork.rockscout.ui.theme.Obsidian
import com.rork.rockscout.ui.theme.Slate900
import com.rork.rockscout.ui.theme.TextMid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Compact collapsible search bar matching the home page MiniSearchBar style.
 *
 * Collapsed: a 48dp pill showing a search icon and placeholder text.
 * Expanded: a full-width text input with clear button, same visual styling.
 *
 * Tapping the collapsed pill expands it and focuses the text field.
 * Tapping the clear (X) button clears the query but stays expanded.
 * Pressing back or losing focus with empty query collapses it.
 *
 * @param query Current search text
 * @param onQueryChange Called when the text changes
 * @param placeholder Placeholder text when empty
 * @param accent Accent color for the glowing border (defaults to Aqua)
 * @param modifier Modifier for the composable
 * @param onSearch Called when the user submits the search (IME action)
 */
@Composable
fun CompactSearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search…",
    accent: Color = Aqua,
    modifier: Modifier = Modifier,
    onSearch: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = expanded,
        transitionSpec = {
            fadeIn(tween(150)) togetherWith fadeOut(tween(100))
        },
        label = "searchExpand",
    ) { isExpanded ->
        if (!isExpanded) {
            // Collapsed state — single row pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Obsidian.copy(alpha = 0.85f),
                                Slate900.copy(alpha = 0.9f),
                            )
                        )
                    )
                    .glowingBorder(2.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(23.dp))
                    .clickable {
                        expanded = true
                        scope.launch {
                            delay(100)
                            focusRequester.requestFocus()
                        }
                    }
                    .padding(horizontal = 14.dp),
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = accent,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = if (query.isNotEmpty()) query else placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (query.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (query.isNotEmpty()) DarkTextHigh else TextMid,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (query.isNotEmpty()) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Clear search",
                        tint = DarkTextMid,
                        modifier = Modifier.size(16.dp).clickable { onQueryChange("") },
                    )
                }
            }
        } else {
            // Expanded state — full text input
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = modifier
                    .height(48.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        placeholder,
                        color = DarkTextMid,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Clear",
                            tint = DarkTextMid,
                            modifier = Modifier.size(18.dp).clickable {
                                onQueryChange("")
                            },
                        )
                    } else {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Collapse",
                            tint = DarkTextMid,
                            modifier = Modifier.size(18.dp).clickable {
                                expanded = false
                                keyboard?.hide()
                            },
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(23.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent.copy(alpha = 0.7f),
                    unfocusedBorderColor = accent.copy(alpha = 0.35f),
                    focusedContainerColor = Slate900.copy(alpha = 0.9f),
                    unfocusedContainerColor = Obsidian.copy(alpha = 0.85f),
                    cursorColor = accent,
                    focusedTextColor = DarkTextHigh,
                    unfocusedTextColor = DarkTextHigh,
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch?.invoke()
                        keyboard?.hide()
                    },
                ),
            )
        }
    }
}
