package com.elfen.ngallery.ui.screens.gallery.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.elfen.ngallery.ui.theme.Sizes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(
    tags:Map<String, List<String>>,
    onClick: (category:String, tag: String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Sizes.small)
    ) {
        tags.forEach { (category, tags) ->
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(Sizes.smaller),
                horizontalArrangement = Arrangement.spacedBy(Sizes.small)
            ) {
                Box(
                    modifier = Modifier.fillMaxRowHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$category:",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                tags.forEach {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(Sizes.smaller))
                            .clickable { onClick(category, it) }
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(
                                vertical = Sizes.smaller,
                                horizontal = Sizes.small
                            )
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}