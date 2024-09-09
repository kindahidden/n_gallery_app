package com.elfen.ngallery.ui.screens.home

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.local.AppDatabase
import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.services.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val galleryRepository: GalleryRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    val galleries = galleryRepository.getSavedGalleriesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch { downloadNotCompleted() }
    }

    @Suppress("DEPRECATION")
    private fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }

    private suspend fun downloadNotCompleted() {
        val incomplete = galleryRepository
            .getSavedGalleriesFlow()
            .first()
            .filter { it.state !is DownloadState.Done && it.state !is DownloadState.Unsaved }

        if (incomplete.isNotEmpty() && !context.isServiceRunning(DownloadService::class.java)) {
            incomplete.forEach {
                viewModelScope.launch {
                    galleryRepository.updateDownloadState(it.id, DownloadState.Pending)
                }

                Log.d("HomeViewModel", "downloadNotCompleted: Started ${it.id}")
                context.startForegroundService(Intent(context, DownloadService::class.java).apply {
                    putExtra("id", it.id)
                })
            }
        }
    }
}