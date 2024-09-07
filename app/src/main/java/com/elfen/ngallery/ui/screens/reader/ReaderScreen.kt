package com.elfen.ngallery.ui.screens.reader

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.elfen.ngallery.ui.composables.Image
import com.elfen.ngallery.ui.screens.gallery.GalleryUiState

@Composable
fun ReaderScreen(
    state: GalleryUiState,
    route: ReaderRoute,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = route) {
        scrollState.scrollToItem(route.page-1)
    }

    Scaffold {
        if (!state.isLoading)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                contentPadding = it
            ) {
                items(state.gallery!!.pages, key = {page -> page.page}) { page ->
                    Image(
                        model = page.hdUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(page.aspectRatio)
                    )
                }
            }
    }
}