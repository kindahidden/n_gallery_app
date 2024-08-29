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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ReaderRoute>()
    private val _state = MutableStateFlow(GalleryUiState())
    val state = _state.asStateFlow()

    init {
        fetchGallery()
    }

    private fun fetchGallery() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val res = galleryRepository.getGallery(route.id)) {
                is Resource.Error -> _state.update {
                    it.copy(
                        error = res.message,
                        isLoading = false
                    )
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        gallery = res.data,
                        isLoading = false
                    )
                }
            }
        }
    }
}