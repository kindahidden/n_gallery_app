package com.elfen.ngallery.ui.screens.gallery

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.DownloadState
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.services.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val galleryRepository: GalleryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val route = savedStateHandle.toRoute<GalleryRoute>()

    private val _gallery = galleryRepository.getGalleryByIdFlow(route.id.toInt())
    private val _state = MutableStateFlow(GalleryUiState())

    val state = _state.asStateFlow().combine(_gallery) { state, gallery ->
        state.copy(
            gallery = gallery,
            isLoading = false,
            error = null
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GalleryUiState())

    init {
        viewModelScope.launch { fetchGallery() }
    }

    private suspend fun fetchGallery() {
        when (val gallery = galleryRepository.fetchGalleryById(route.id.toInt())) {
            is Resource.Success -> galleryRepository.saveGallery(gallery.data!!)
            is Resource.Error -> _state.update { it.copy(error = gallery.message) }
        }
    }

    fun saveGallery() {
        downloadGallery()
    }

    fun removeGallery() {
        viewModelScope.launch { galleryRepository.removeGallery(state.value.gallery!!) }
    }

    private fun downloadGallery() {
        viewModelScope.launch {
            galleryRepository.startDownload(route.id)
        }

        Log.d("GalleryViewModel", "downloadGallery: Started")
        context.startForegroundService(Intent(context, DownloadService::class.java).apply {
            putExtra("id", route.id)
        })
    }
}