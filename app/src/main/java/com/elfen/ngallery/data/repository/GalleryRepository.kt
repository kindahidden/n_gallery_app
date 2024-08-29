package com.elfen.ngallery.data.repository

import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.local.entities.asAppModel
import com.elfen.ngallery.data.local.entities.asEntity
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.remote.models.toAppModel
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.models.Sorting
import com.elfen.ngallery.utilities.resourceOf
import kotlinx.coroutines.flow.map

class GalleryRepository(
    private val apiService: APIService,
    private val galleryDao: GalleryDao
) {
    suspend fun searchGalleries(query: String, sorting: Sorting, page: Int) = resourceOf {
        apiService
            .searchGalleries(query, sorting.value, page)
            .result
            .map { it.toAppModel() }
    }

    suspend fun getGallery(id: Int) = resourceOf {
        apiService.getGallery(id).toAppModel()
    }

    suspend fun fetchGallery(id: Int){
        when(val res = getGallery(id)){
            is Resource.Success -> {
                val gallery = res.data!!
                galleryDao.upsertGallery(gallery.asEntity())
                galleryDao.upsertPages(gallery.pages.map { it.asEntity(gallery.id) })
            }
            else -> {}
        }
    }

    fun getGalleryFlow(id: Int) = galleryDao.getGalleryById(id).map { it?.asAppModel() }
}