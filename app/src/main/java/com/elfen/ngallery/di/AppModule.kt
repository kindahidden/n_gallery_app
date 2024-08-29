package com.elfen.ngallery.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Database
import androidx.room.Room
import com.elfen.ngallery.data.local.AppDatabase
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.repository.GalleryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideGalleryRepository(apiService: APIService, database: AppDatabase) =
        GalleryRepository(apiService, database.galleryDao())

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
}