package com.elfen.ngallery.ui.screens.gallery

import com.elfen.ngallery.models.Gallery

data class GalleryUiState(
    val gallery: Gallery? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
