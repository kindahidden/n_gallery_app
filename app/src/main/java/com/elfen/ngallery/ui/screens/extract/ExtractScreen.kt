package com.elfen.ngallery.ui.screens.extract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfen.ngallery.ui.composables.Image
import com.elfen.ngallery.ui.screens.gallery.GalleryRoute
import com.elfen.ngallery.ui.theme.AppTheme
import com.elfen.ngallery.ui.theme.Sizes
import kotlinx.serialization.Serializable

@Serializable
object ExtractRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractScreen(
    state: ExtractUiState = ExtractUiState(),
    onBack: () -> Unit = {},
    onChangeText: (String) -> Unit = {},
    onNavigate: (Any) -> Unit = {},
    onRequestGallery: (Int) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Extract IDs") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onChangeText("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Paste") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null
                    )
                },
                onClick = { clipboardManager.getText()?.text?.let { onChangeText(it) } },
                modifier = Modifier.imePadding()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                TextField(
                    value = state.text,
                    onValueChange = onChangeText,
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = { Text(text = "117013") },
                )
            }
            items(state.idList) {
                val galleryState = state.galleries[it]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (galleryState is GalleryLoadingState.Loaded)
                                onNavigate(GalleryRoute(id = it))
                            else if (!state.galleries.containsKey(it) || state.galleries[it] is GalleryLoadingState.Error)
                                onRequestGallery(it)
                        }
                        .padding(Sizes.medium),
                    horizontalArrangement = Arrangement.spacedBy(Sizes.medium)
                ) {
                    Image(
                        model = if (galleryState is GalleryLoadingState.Loaded) galleryState.gallery.thumbnail.url else "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(Sizes.medium))
                            .width(128.dp)
                            .aspectRatio(0.69f)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                    Column(
                        modifier = Modifier.padding(vertical = Sizes.medium)
                    ) {
                        when(galleryState){
                            is GalleryLoadingState.Loaded -> Text(text = galleryState.gallery.title)
                            is GalleryLoadingState.Loading -> Text(text = "Loading...")
                            is GalleryLoadingState.Error -> Text(text = "Error: ${galleryState.error}")
                            else -> Text(text = it.toString())
                        }
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
private fun ExtractScreenPreview() {
    AppTheme {
        ExtractScreen(
            state = ExtractUiState(idList = listOf(12345, 678912))
        )
    }
}

fun NavGraphBuilder.extractScreen(navController: NavController) {
    composable<ExtractRoute> {
        val viewModel: ExtractViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        ExtractScreen(
            state = state,
            onChangeText = viewModel::onChange,
            onBack = { navController.popBackStack() },
            onNavigate = navController::navigate,
            onRequestGallery = viewModel::requestGallery
        )
    }
}