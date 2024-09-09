package com.elfen.ngallery.data.local.entities

import android.content.Context
import androidx.room.Embedded
import androidx.room.Relation
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.toDownloaded

data class FullGallery(
    @Embedded val gallery: GalleryEntity,

    @Relation(parentColumn = "id", entityColumn = "galleryId")
    val pages: List<PageEntity>,

    @Relation(parentColumn = "id", entityColumn = "galleryId")
    val download: DownloadEntity?
)

fun FullGallery.asAppModel(context: Context): Gallery {
    val downloadState = download?.asAppModel() ?: DownloadState.Unsaved
    val appGallery = gallery.asAppModel(
        pages = pages.map { it.asAppModel() },
        downloadState = downloadState
    )

    if(downloadState is DownloadState.Done){
        return appGallery.toDownloaded(context)
    }

    return appGallery
}