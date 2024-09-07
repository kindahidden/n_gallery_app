package com.elfen.ngallery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.elfen.ngallery.data.local.entities.GalleryEntity
import com.elfen.ngallery.data.local.entities.GalleryWithPages
import com.elfen.ngallery.data.local.entities.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGallery(galleryEntity: GalleryEntity)

    @Upsert
    suspend fun upsertPages(pages: List<PageEntity>)

    @Update
    suspend fun updateGallery(galleryEntity: GalleryEntity)

    @Transaction
    @Query("SELECT * FROM gallery WHERE id=:id")
    fun getGalleryById(id: Int): Flow<GalleryWithPages?>

    @Transaction
    @Query("SELECT * FROM gallery WHERE saved=1")
    fun getSavedGalleries(): Flow<List<GalleryWithPages>>
}