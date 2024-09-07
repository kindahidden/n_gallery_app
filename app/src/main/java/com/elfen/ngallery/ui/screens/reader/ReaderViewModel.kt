package com.elfen.ngallery.ui.screens.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.ui.screens.gallery.GalleryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ReaderRoute>()
    val state = galleryRepository.getGalleryFlow(route.id)
        .map { GalleryUiState(isLoading = false, gallery = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = GalleryUiState())
}