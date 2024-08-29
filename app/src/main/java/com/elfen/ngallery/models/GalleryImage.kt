package com.elfen.ngallery.models

import kotlinx.serialization.Serializable

@Serializable
data class GalleryImage(
    val url: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
)
