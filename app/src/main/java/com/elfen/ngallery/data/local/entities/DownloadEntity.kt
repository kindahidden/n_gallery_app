package com.elfen.ngallery.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery

@Entity("download")
data class DownloadEntity(
    @PrimaryKey val galleryId: Int,
    val state: LocalDownloadState,
    val progress: Int?,
    val total: Int?,
)

fun DownloadEntity.asAppModel() = when (state) {
    LocalDownloadState.DOWNLOADING -> DownloadState.Downloading(progress!!, total!!)
    LocalDownloadState.PENDING -> DownloadState.Pending
    LocalDownloadState.DONE -> DownloadState.Done
    LocalDownloadState.FAILED -> DownloadState.Failure
}

fun DownloadState.asDownloadEntity(galleryId: Int) = when (this) {
    is DownloadState.Downloading -> DownloadEntity(
        galleryId = galleryId,
        state = LocalDownloadState.DOWNLOADING,
        progress = progress,
        total = total
    )

    is DownloadState.Pending -> DownloadEntity(
        galleryId = galleryId,
        state = LocalDownloadState.PENDING,
        progress = null,
        total = null
    )

    is DownloadState.Done -> DownloadEntity(
        galleryId = galleryId,
        state = LocalDownloadState.DONE,
        progress = null,
        total = null
    )

    is DownloadState.Failure -> DownloadEntity(
        galleryId = galleryId,
        state = LocalDownloadState.FAILED,
        progress = null,
        total = null
    )

    else -> throw Exception("Unsaved cannot be saved in database")
}
