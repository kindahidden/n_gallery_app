package com.elfen.ngallery.models

import android.content.Context
import kotlinx.datetime.LocalDateTime
import java.io.File

data class Gallery(
    val id: Int,
    val mediaId: String,
    val title: String,
    val cover: GalleryImage,
    val thumbnail: GalleryImage,
    val pages: List<GalleryPage>,
    val tags: Map<String, List<String>>,
    val uploaded: LocalDateTime,
    val state: DownloadState,
    val savedAt: LocalDateTime?
)

fun Gallery.toDownloaded(context: Context): Gallery {
    val newCover = cover.let {
        val coverExtension = it.url.split(".").last()
        it.copy(url = coverFile(context).absolutePath)
    }
    return this.copy(
        pages = pages.map {
            val extension = it.hdUrl.split(".").last()
            val path = pageFile(context, it).absolutePath

            it.copy(hdUrl = path, url = path)
        },
        thumbnail = newCover,
        cover = newCover,
    )
}

fun Gallery.pagesDir(context: Context) = File(context.dataDir, "galleries/$id")

fun Gallery.coverFile(context:Context): File {
    val coverExtension = cover.url.split(".").last()
    return File(context.dataDir, "cover/$id.$coverExtension")
}

fun Gallery.pageFile(context: Context, page: GalleryPage): File {
    val extension = page.hdUrl.split(".").last()
    return File(context.dataDir, "galleries/$id/${page.page}.$extension")
}