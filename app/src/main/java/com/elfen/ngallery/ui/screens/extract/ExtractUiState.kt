package com.elfen.ngallery.ui.screens.extract

import com.elfen.ngallery.models.Gallery

data class ExtractUiState(
    val idList: List<Int> = listOf(),
    val galleries: Map<Int, GalleryLoadingState> = mapOf(),
    val text: String = "",
)
