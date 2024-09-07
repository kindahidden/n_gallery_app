package com.elfen.ngallery.ui.screens.extract

import com.elfen.ngallery.models.Gallery

sealed class GalleryLoadingState {
    data object Loading: GalleryLoadingState()
    data class Error(val error: String): GalleryLoadingState()
    data class Loaded(val gallery: Gallery): GalleryLoadingState()
}