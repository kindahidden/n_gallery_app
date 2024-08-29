package com.elfen.ngallery.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.elfen.ngallery.models.Gallery

data class GalleryWithPages(
    @Embedded val gallery: GalleryEntity,

    @Relation(parentColumn = "id", entityColumn = "galleryId")
    val pages: List<PageEntity>
)

fun GalleryWithPages.asAppModel() = gallery.asAppModel(pages.map { it.asAppModel() })