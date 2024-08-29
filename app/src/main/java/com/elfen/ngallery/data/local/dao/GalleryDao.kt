package com.elfen.ngallery.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.elfen.ngallery.data.local.entities.GalleryEntity
import com.elfen.ngallery.data.local.entities.GalleryWithPages
import com.elfen.ngallery.data.local.entities.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryDao {
    @Upsert
    suspend fun upsertGallery(galleryEntity: GalleryEntity)

    @Upsert
    suspend fun upsertPages(pages: List<PageEntity>)

    @Transaction
    @Query("SELECT * FROM gallery WHERE id=:id")
    fun getGalleryById(id: Int): Flow<GalleryWithPages?>

    @Transaction
    @Query("SELECT * FROM gallery")
    fun getGalleries(): Flow<List<GalleryWithPages>>
}