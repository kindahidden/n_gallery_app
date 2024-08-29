package com.elfen.ngallery.ui.screens.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.Resource
import com.elfen.ngallery.models.Sorting
import com.elfen.ngallery.models.toDisplay
import com.elfen.ngallery.ui.composables.ErrorCard
import com.elfen.ngallery.ui.screens.browse.composables.GalleryItemCard
import com.elfen.ngallery.ui.screens.browse.composables.SearchBar
import com.elfen.ngallery.ui.theme.Sizes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    state: BrowseUiState,
    onOpenGallery: (Gallery) -> Unit,
    onQueryChanged: (String) -> Unit,
    onPageChanged: (Int) -> Unit,
    onSort: (Sorting) -> Unit,
) {
    val dir = LocalLayoutDirection.current
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()
    val sortingSheetState = rememberModalBottomSheetState()
    var sortingModalShown by remember {
        mutableStateOf(false)
    }

    val onScrollToTop = {
        coroutineScope.launch {
            lazyStaggeredGridState.animateScrollToItem(0)
        }
    }

    Scaffold(topBar = {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            SearchBar(
                query = state.query,
                state = state,
                onQueryChanged = {
                    onQueryChanged(it)
                },
                onOpenSortModal = { sortingModalShown = !sortingModalShown },
            )
        }
    }, floatingActionButton = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Sizes.medium),
            modifier = Modifier.imePadding()
        ) {
            FloatingActionButton(onClick = {
                onPageChanged(state.page - 1)
                onScrollToTop()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
            FloatingActionButton(onClick = {
                onPageChanged(state.page + 1)
                onScrollToTop()
            }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }) {
        if (sortingModalShown) {
            ModalBottomSheet(
                onDismissRequest = { sortingModalShown = false },
                sheetState = sortingSheetState,
            ) {
                val onPressSorting: (Sorting) -> Unit = {
                    onSort(it)
                    coroutineScope.launch { sortingSheetState.hide() }.invokeOnCompletion {
                        sortingModalShown = false
                    }
                }

                Column(
                    modifier = Modifier.padding(Sizes.medium),
                    verticalArrangement = Arrangement.spacedBy(Sizes.normal)
                ){
                    Sorting.entries.forEach {
                        TextButton(
                            onClick = { onPressSorting(it) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = it.toDisplay(), modifier = Modifier.weight(1f))
                            if(state.sorting == it){
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }

        if (state.isLoading && state.data != null) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }

        when (state.data) {
            is Resource.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = if (state.isLoading) Modifier.alpha(0.75f) else Modifier,
                    verticalItemSpacing = Sizes.medium,
                    state = lazyStaggeredGridState,
                    contentPadding = PaddingValues(
                        top = 16.dp + it.calculateTopPadding(),
                        bottom = 16.dp + it.calculateBottomPadding() + 64.dp,
                        start = 16.dp + it.calculateStartPadding(dir),
                        end = 16.dp + it.calculateEndPadding(dir)
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data.data!!) { gallery ->
                        GalleryItemCard(
                            onClick = { onOpenGallery(gallery) },
                            gallery = gallery,
                        )
                    }
                }
            }

            is Resource.Error -> ErrorCard(
                message = state.data.message!!,
                modifier = Modifier.padding(it)
            )

            else -> {}
        }
    }
}