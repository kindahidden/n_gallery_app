package com.elfen.ngallery.models

import kotlinx.serialization.Serializable

@Serializable
data class GalleryPage(
    val url: String,
    val hdUrl: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
    val page: Int,
)
