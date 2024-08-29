package com.elfen.ngallery.data.remote.models

import com.elfen.ngallery.models.GalleryImage
import com.elfen.ngallery.models.GalleryPage

data class NetworkImage(
    val t: String,
    val w: Int,
    val h: Int,
)

val NetworkImage.extension
    get() = if(t == "p") "png" else "jpg"

fun NetworkImage.toThumbnailUrl(mediaId: String) = "https://t3.nhentai.net/galleries/$mediaId/thumb.$extension"
fun NetworkImage.toCoverUrl(mediaId: String) = "https://t5.nhentai.net/galleries/${mediaId}/cover.$extension"
fun NetworkImage.toPageSdUrl(mediaId: String, index: Int) = "https://t3.nhentai.net/galleries/$mediaId/${index + 1}t.$extension"
fun NetworkImage.toPageHdUrl(mediaId: String, index: Int) = "https://i.nhentai.net/galleries/$mediaId/${index + 1}.$extension"

fun NetworkImage.toThumbnail(mediaId: String) = GalleryImage(
    url = toThumbnailUrl(mediaId),
    width = w,
    height = h,
    aspectRatio = w/h.toFloat(),
)

fun NetworkImage.toCover(mediaId: String) = GalleryImage(
    url = toCoverUrl(mediaId),
    width = w,
    height = h,
    aspectRatio = w/h.toFloat(),
)

fun NetworkImage.toPage(mediaId: String, index: Int) = GalleryPage(
    url = toPageSdUrl(mediaId, index),
    hdUrl = toPageHdUrl(mediaId, index),
    width = w,
    height = h,
    aspectRatio = w/h.toFloat(),
    page = index+1
)