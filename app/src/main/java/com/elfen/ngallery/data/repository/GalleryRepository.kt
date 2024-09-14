package com.elfen.ngallery.data.repository

import android.content.Context
import android.util.Log
import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.local.entities.asAppModel
import com.elfen.ngallery.data.local.entities.asDownloadEntity
import com.elfen.ngallery.data.local.entities.asEntity
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.remote.models.toAppModel
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.GalleryImage
import com.elfen.ngallery.models.Sorting
import com.elfen.ngallery.models.coverFile
import com.elfen.ngallery.models.pagesDir
import com.elfen.ngallery.utilities.resourceOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jsoup.Jsoup
import retrofit2.HttpException

class GalleryRepository(
    private val apiService: APIService,
    private val galleryDao: GalleryDao,
    private val context: Context
) {
    private suspend fun searchGalleriesHTML(
        query: String,
        sorting: Sorting,
        page: Int
    ): List<Gallery> {
        val html = apiService
            .searchGalleriesHTML(query, sorting.value, page)
            .string()
        val document = Jsoup.parse(html)
        val galleries = document.getElementsByClass("gallery")

        val result = galleries.map { gallery ->
            val hyperlink = gallery.getElementsByTag("a").first()!!
            val id = hyperlink.attribute("href").value.split("/")[2]
            val title = gallery.getElementsByClass("caption").first()!!.text()
            val image = gallery.getElementsByClass("lazyload").first()!!
            val thumbnail = image.let {
                val width = it.attribute("width").value.toInt()
                val height = it.attribute("height").value.toInt()
                val url = it.attribute("data-src").value

                GalleryImage(
                    url = url,
                    width = width,
                    height = height,
                    aspectRatio = width / height.toFloat(),
                )
            }

            Gallery(
                id = id.toInt(),
                mediaId = "",
                title = title,
                cover = thumbnail,
                thumbnail = thumbnail,
                pages = listOf(),
                tags = mapOf(),
                uploaded = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                state = DownloadState.Unsaved,
                savedAt = null
            )
        }

        return result
    }

    suspend fun searchGalleries(query: String, sorting: Sorting, page: Int) = resourceOf {
        try {
            val result = apiService
                .searchGalleries(query, sorting.value, page)
                .result
                .map { it.toAppModel() }

            result
        } catch (error: HttpException) {
            if (error.code() == 404) {
                val result = searchGalleriesHTML(query, sorting, page)
                result
            } else {
                throw error
            }
        }
    }

    suspend fun fetchGalleryById(id: Int) = resourceOf {
        apiService.getGallery(id).toAppModel()
    }

    suspend fun saveGallery(gallery: Gallery) {
        galleryDao.insertGallery(gallery.asEntity())
        galleryDao.upsertPages(gallery.pages.map { it.asEntity(gallery.id) })
    }

    suspend fun removeGallery(gallery: Gallery) {
        galleryDao.deleteDownloadById(gallery.id)

        gallery.coverFile(context).delete()
        gallery.pagesDir(context).delete()

        galleryDao.updateSavedAt(gallery.id, null)
    }

    fun getSavedGalleriesFlow() =
        galleryDao.getSavedGalleries().map { it.map { gallery -> gallery.asAppModel(context) } }

    fun getGalleryByIdFlow(id: Int) = galleryDao.getGalleryByIdFlow(id).map {
        it?.asAppModel(context)
    }

    suspend fun updateDownloadState(galleryId: Int, downloadState: DownloadState) {
        galleryDao.upsertDownloadState(downloadState.asDownloadEntity(galleryId))
    }

    suspend fun startDownload(id: Int) {
        Log.d("GalleryRepository", "startDownload: ${Clock.System.now().toEpochMilliseconds()}")
        galleryDao.updateSavedAt(id, Clock.System.now().toEpochMilliseconds())
        updateDownloadState(id, DownloadState.Pending)
    }
}