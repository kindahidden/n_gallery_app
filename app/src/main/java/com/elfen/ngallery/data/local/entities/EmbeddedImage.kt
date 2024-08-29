package com.elfen.ngallery.data.local.entities

import com.elfen.ngallery.models.GalleryImage

data class EmbeddedImage(
    val url: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
)

fun EmbeddedImage.asAppModel() = GalleryImage(
    url = url,
    width = width,
    height = height,
    aspectRatio = aspectRatio
)

fun GalleryImage.asEmbeddedImage() = EmbeddedImage(
    url = url,
    width = width,
    height = height,
    aspectRatio = aspectRatio
)