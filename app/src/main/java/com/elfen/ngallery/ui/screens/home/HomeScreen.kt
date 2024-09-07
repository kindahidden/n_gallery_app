package com.elfen.ngallery.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfen.ngallery.R
import com.elfen.ngallery.data.local.storeToken
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.ui.screens.browse.BrowseRoute
import com.elfen.ngallery.ui.composables.GalleryItemCard
import com.elfen.ngallery.ui.screens.gallery.GalleryRoute
import com.elfen.ngallery.ui.screens.login.Login
import com.elfen.ngallery.ui.theme.Sizes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
object Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    galleries: List<Gallery>,
    onNavigate: (Any) -> Unit,
    isLoggedIn: Boolean
) {
    val dir = LocalLayoutDirection.current

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Home") }) },
        floatingActionButton = {
            if (isLoggedIn)
                ExtendedFloatingActionButton(
                    text = { Text(text = "Search") },
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    onClick = { onNavigate(BrowseRoute()) }
                )
            else {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Login") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_login_24),
                            contentDescription = null
                        )
                    },
                    onClick = { onNavigate(Login) }
                )
            }
        }
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = Sizes.medium,
            contentPadding = PaddingValues(
                top = 16.dp + it.calculateTopPadding(),
                bottom = 16.dp + it.calculateBottomPadding() + 64.dp,
                start = 16.dp + it.calculateStartPadding(dir),
                end = 16.dp + it.calculateEndPadding(dir)
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(galleries) { gallery ->
                GalleryItemCard(
                    onClick = { onNavigate(GalleryRoute(gallery.id)) },
                    gallery = gallery,
                )
            }
        }
    }
}

fun NavGraphBuilder.homeScreen(navController: NavController) {
    composable<Home> {
        val context = LocalContext.current
        val viewModel: HomeViewModel = hiltViewModel()
        val galleries by viewModel.galleries.collectAsState()

        val initialTokenValid = remember {
            runBlocking { context.storeToken.isTokenValid.first() }
        }
        val isTokenValid by context.storeToken.isTokenValid.collectAsState(initialTokenValid)

        HomeScreen(
            galleries = galleries,
            onNavigate = { navController.navigate(route = it) },
            isLoggedIn = isTokenValid
        )
    }
}