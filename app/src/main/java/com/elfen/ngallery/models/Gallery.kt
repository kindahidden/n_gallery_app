package com.elfen.ngallery.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Gallery(
    val id: Int,
    val mediaId: String,
    val title: String,
    val cover: GalleryImage,
    val thumbnail: GalleryImage,
    val pages: List<GalleryPage>,
    val tags: Map<String, List<String>>,
    val uploaded: LocalDateTime
)