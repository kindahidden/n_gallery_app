package com.elfen.ngallery.ui.screens.browse.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.elfen.ngallery.models.toDisplay
import com.elfen.ngallery.ui.screens.browse.BrowseUiState
import com.elfen.ngallery.ui.theme.Sizes

@Composable
fun SearchBar(
    onQueryChanged: (String) -> Unit,
    onOpenSortModal: () -> Unit,
    query: String,
    state: BrowseUiState,
) {
    var visited by rememberSaveable { mutableStateOf(false) }
    val focus = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = visited) {
        if(!visited){
            focus.requestFocus()
            visited = true
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .focusRequester(focus),
            textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = {}),
            decorationBox = {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = Sizes.medium,
                            horizontal = Sizes.medium
                        )
                        .padding(top = Sizes.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Sizes.normal)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Genshin Impact...",
                                modifier = Modifier
                                    .matchParentSize()
                                    .alpha(0.16f),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        it()
                    }
                }
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = Sizes.small)
                .padding(horizontal = Sizes.medium)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Sizes.normal)
        ) {
            PillButton(text = "Page: ${state.page}")
            PillButton(text = state.sorting.toDisplay(), onClick = {
                onOpenSortModal()
            })
        }
    }
}

@Composable
private fun RowScope.PillButton(
    text: String,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .alpha(if (disabled) 0.5f else 1f)
            .conditionalClickable(onClick)
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.surfaceDim, CircleShape)
            .padding(horizontal = Sizes.normal, vertical = Sizes.smaller)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Sizes.small)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Modifier.conditionalClickable(onClick: (() -> Unit)?): Modifier {
    if (onClick == null) {
        return this
    }

    return this.clickable {
        onClick()
    }
}