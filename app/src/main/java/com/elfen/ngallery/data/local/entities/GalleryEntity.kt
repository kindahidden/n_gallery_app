package com.elfen.ngallery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.GalleryPage
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Entity(tableName = "gallery")
data class GalleryEntity(
    @PrimaryKey val id: Int,
    val mediaId: String,
    val title: String,
    @Embedded(prefix = "cover_") val cover: EmbeddedImage,
    @Embedded(prefix = "thumbnail_") val thumbnail: EmbeddedImage,
    val uploaded: Long,
    val tags: Map<String, List<String>>,
    val savedAt: Long?
)

fun GalleryEntity.asAppModel(pages: List<GalleryPage>, downloadState: DownloadState) = Gallery(
    id = id,
    mediaId = mediaId,
    title = title,
    cover = cover.asAppModel(),
    thumbnail = thumbnail.asAppModel(),
    pages = pages,
    tags = tags,
    uploaded = Instant.fromEpochMilliseconds(uploaded).toLocalDateTime(TimeZone.UTC),
    state = downloadState,
    savedAt = savedAt?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC) }
)

fun Gallery.asEntity() = GalleryEntity(
    id = id,
    mediaId = mediaId,
    title = title,
    cover = cover.asEmbeddedImage(),
    thumbnail = thumbnail.asEmbeddedImage(),
    uploaded = uploaded.toInstant(TimeZone.UTC).toEpochMilliseconds(),
    tags = tags,
    savedAt = savedAt?.toInstant(TimeZone.UTC)?.toEpochMilliseconds()
)