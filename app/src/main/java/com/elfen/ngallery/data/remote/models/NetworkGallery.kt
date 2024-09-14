package com.elfen.ngallery.data.remote.models

import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class NetworkGallery(
    val id: Int,
    @SerializedName("media_id") val mediaId: String,
    val title: NetworkTitle,
    val images: NetworkImages,
    @SerializedName("upload_date") val uploadDate: Long,
    val tags: List<NetworkTag>,
    @SerializedName("num_pages") val numPages: Int,
    @SerializedName("num_favorites") val numFavorites: Int
)

fun NetworkGallery.toAppModel() = Gallery(
    id = id,
    mediaId = mediaId,
    title = title.pretty,
    cover = images.cover.toCover(mediaId),
    thumbnail = images.thumbnail.toThumbnail(mediaId),
    pages = images.pages.mapIndexed { index, networkImage -> networkImage.toPage(mediaId, index) },
    tags = tags.toMap(),
    uploaded = Instant.fromEpochSeconds(uploadDate).toLocalDateTime(TimeZone.UTC),
    state = DownloadState.Unsaved,
    savedAt = null
)