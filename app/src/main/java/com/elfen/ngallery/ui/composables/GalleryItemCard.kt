package com.elfen.ngallery.ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.ui.theme.Sizes

@Composable
fun GalleryItemCard(
    onClick: () -> Unit,
    gallery: Gallery,
) {
    val isDownloading =
        gallery.state is DownloadState.Downloading || gallery.state is DownloadState.Pending
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.small))
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                model = gallery.thumbnail.url,
                modifier = Modifier
                    .alpha(if (isDownloading) 0.5f else 1f)
                    .clip(RoundedCornerShape(Sizes.small))
                    .fillMaxWidth()
                    .aspectRatio(gallery.thumbnail.aspectRatio),
                animated = false
            )
            when (gallery.state) {
                is DownloadState.Downloading -> {
                    val progressAnimate by animateFloatAsState(
                        targetValue = gallery.state.progress.toFloat() / gallery.state.total,
                        animationSpec = tween<Float>(
                            durationMillis = 1000,
                            easing = LinearEasing
                        ),
                        label = ""
                    )

                    CircularProgressIndicator(
                        progress = { progressAnimate },
                    )
                }

                DownloadState.Pending -> CircularProgressIndicator()
                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(Sizes.smaller))
        Text(
            text = gallery.title,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(Sizes.smaller)
                .alpha(if (isDownloading) 0.5f else 1f)
        )
    }
}