package com.elfen.ngallery.ui.screens.reader

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class ReaderRoute(val id: Int, val page: Int = 1)

fun NavGraphBuilder.readerRoute(navController: NavController) {
    composable<ReaderRoute> {
        val viewModel: ReaderViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        val route = it.toRoute<ReaderRoute>()

        ReaderScreen(
            route = route,
            state = state
        )
    }
}