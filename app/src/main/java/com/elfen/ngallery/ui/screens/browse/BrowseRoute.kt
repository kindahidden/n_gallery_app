package com.elfen.ngallery.ui.screens.browse

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elfen.ngallery.ui.screens.gallery.GalleryRoute
import kotlinx.serialization.Serializable

@Serializable
data class BrowseRoute(val query: String? = null)

fun NavGraphBuilder.browseScreen(navController: NavController) {
    composable<BrowseRoute> {
        val viewModel: BrowseViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        BrowseScreen(
            state = state,
            onQueryChanged = viewModel::setQuery,
            onOpenGallery = { gallery -> navController.navigate(GalleryRoute(gallery.id)) },
            onPageChanged = viewModel::setPage,
            onSort = viewModel::setSorting
        )
    }
}