package com.elfen.ngallery.ui.screens.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val galleryRepository: GalleryRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<GalleryRoute>()

    private val _gallery = galleryRepository.getGalleryFlow(route.id)
    private val _state = MutableStateFlow(GalleryUiState())

    val state = _state.asStateFlow().combine(_gallery) { state, gallery ->
        state.copy(gallery = gallery, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GalleryUiState())

    init {
        viewModelScope.launch { galleryRepository.fetchGallery(route.id) }
    }
}