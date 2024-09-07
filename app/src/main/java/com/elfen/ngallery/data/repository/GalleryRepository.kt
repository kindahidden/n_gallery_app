package com.elfen.ngallery.data.repository

import android.util.Log
import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.local.entities.asAppModel
import com.elfen.ngallery.data.local.entities.asEntity
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.remote.models.toAppModel
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.GalleryImage
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.models.Sorting
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
    private val galleryDao: GalleryDao
) {
    suspend fun searchGalleries(query: String, sorting: Sorting, page: Int) = resourceOf {
        try {
            val result = apiService
                .searchGalleries(query, sorting.value, page)
                .result
                .map { it.toAppModel() }

            result
        } catch (error: HttpException) {
            if (error.code() == 404) {
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
                        saved = false
                    )
                }

                result
            } else {
                throw error
            }
        }
    }

    suspend fun getGallery(id: Int) = resourceOf {
        apiService.getGallery(id).toAppModel()
    }

    suspend fun fetchGallery(id: Int) {
        when (val res = getGallery(id)) {
            is Resource.Success -> {
                val gallery = res.data!!
                galleryDao.insertGallery(gallery.asEntity())
                galleryDao.upsertPages(gallery.pages.map { it.asEntity(gallery.id) })
            }

            else -> {}
        }
    }

    fun getGalleryFlow(id: Int) = galleryDao.getGalleryById(id).map { it?.asAppModel() }

    suspend fun saveGallery(gallery: Gallery) {
        galleryDao.updateGallery(gallery.copy(saved = true).asEntity())
    }

    suspend fun removeGallery(gallery: Gallery) {
        galleryDao.updateGallery(gallery.copy(saved = false).asEntity())
    }

    fun getSavedGalleriesFlow() =
        galleryDao.getSavedGalleries().map { it.map { gallery -> gallery.asAppModel() } }
}