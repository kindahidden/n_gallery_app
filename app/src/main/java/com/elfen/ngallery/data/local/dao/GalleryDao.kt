package com.elfen.ngallery.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.elfen.ngallery.data.local.entities.DownloadEntity
import com.elfen.ngallery.data.local.entities.GalleryEntity
import com.elfen.ngallery.data.local.entities.FullGallery
import com.elfen.ngallery.data.local.entities.LocalDownloadState
import com.elfen.ngallery.data.local.entities.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryDao {
    @Upsert
    suspend fun insertGallery(galleryEntity: GalleryEntity)

    @Upsert
    suspend fun upsertPages(pages: List<PageEntity>)

    @Query("DELETE FROM gallery WHERE id=:id")
    suspend fun deleteGalleryById(id: Int)

    @Query("DELETE FROM download WHERE galleryId=:id")
    suspend fun deleteDownloadById(id: Int)

    @Transaction
    @Query("SELECT * FROM gallery WHERE id=:id")
    suspend fun getGalleryById(id: Int): FullGallery?

    @Transaction
    @Query("SELECT * FROM gallery WHERE id=:id")
    fun getGalleryByIdFlow(id: Int): Flow<FullGallery?>

    @Query("UPDATE gallery SET cover_url=:cover AND thumbnail_url=:cover")
    suspend fun updateCover(cover: String)

    @Transaction
    @Query("SELECT * FROM gallery WHERE id IN (SELECT galleryId FROM download)")
    fun getSavedGalleries(): Flow<List<FullGallery>>

    @Upsert
    suspend fun upsertDownloadState(downloadState: DownloadEntity)
}