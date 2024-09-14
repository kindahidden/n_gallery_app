package com.elfen.ngallery.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.local.entities.DownloadEntity
import com.elfen.ngallery.data.local.entities.GalleryEntity
import com.elfen.ngallery.data.local.entities.PageEntity

@Database(
    entities = [
        GalleryEntity::class,
        PageEntity::class,
        DownloadEntity::class
    ], version = 4,
    autoMigrations = [
        AutoMigration(2, 3),
        AutoMigration(3, 4)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galleryDao(): GalleryDao
}