package com.elfen.ngallery.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.ui.theme.Sizes

@Composable
fun GalleryItemCard(
    onClick: () -> Unit,
    gallery: Gallery,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.small))
            .clickable { onClick() }
    ) {
        Image(
            model = gallery.thumbnail.url,
            modifier = Modifier
                .clip(RoundedCornerShape(Sizes.small))
                .fillMaxWidth()
                .aspectRatio(gallery.thumbnail.aspectRatio),
            animated = false
        )
        Spacer(modifier = Modifier.height(Sizes.smaller))
        Text(
            text = gallery.title,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(Sizes.smaller)
        )
    }
}