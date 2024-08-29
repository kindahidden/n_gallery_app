package com.elfen.ngallery.ui.screens.browse

import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.models.Sorting

data class BrowseUiState(
    val data: Resource<List<Gallery>>? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val page: Int = 1,
    val sorting: Sorting = Sorting.POPULAR
)
