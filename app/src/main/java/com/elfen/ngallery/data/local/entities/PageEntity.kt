package com.elfen.ngallery.data.local.entities

import androidx.room.Entity
import com.elfen.ngallery.models.GalleryPage

@Entity(tableName = "page", primaryKeys = ["galleryId", "page"])
data class PageEntity(
    val galleryId: Int,
    val url: String,
    val hdUrl: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Float,
    val page: Int,
)

fun GalleryPage.asEntity(galleryId: Int) = PageEntity(
    galleryId = galleryId,
    url = url,
    hdUrl = hdUrl,
    width = width,
    height = height,
    aspectRatio = aspectRatio,
    page = page
)

fun PageEntity.asAppModel() = GalleryPage(
    url = url,
    hdUrl = hdUrl,
    width = width,
    height = height,
    aspectRatio = aspectRatio,
    page = page
)