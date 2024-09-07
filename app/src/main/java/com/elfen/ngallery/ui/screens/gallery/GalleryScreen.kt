package com.elfen.ngallery.ui.screens.gallery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfen.ngallery.models.Gallery
import com.elfen.ngallery.models.GalleryImage
import com.elfen.ngallery.models.GalleryPage
import com.elfen.ngallery.ui.composables.Image
import com.elfen.ngallery.ui.screens.browse.BrowseRoute
import com.elfen.ngallery.ui.screens.gallery.composables.Tags
import com.elfen.ngallery.ui.screens.reader.ReaderRoute
import com.elfen.ngallery.ui.theme.AppTheme
import com.elfen.ngallery.ui.theme.Sizes
import com.elfen.ngallery.utilities.plus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GalleryRoute(val id: Int)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GalleryScreen(
    onNavigate: (Any) -> Unit,
    onSave: () -> Unit,
    onUnsave: () -> Unit,
    onBack: () -> Unit,
    state: GalleryUiState
) {
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            !state.error.isNullOrEmpty() -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(Sizes.medium),
                    verticalArrangement = Arrangement.spacedBy(
                        Sizes.normal,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            state.gallery != null -> {
                Log.d("GalleryScreen", state.gallery.toString())
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = paddingValues + PaddingValues(Sizes.medium),
                    verticalItemSpacing = Sizes.normal,
                    horizontalArrangement = Arrangement.spacedBy(Sizes.normal)
                ) {
                    val gallery by derivedStateOf { state.gallery }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Sizes.normal)
                        ) {
                            Image(
                                model = gallery.cover.url,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Sizes.normal))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .width(128.dp)
                                    .aspectRatio(gallery.cover.aspectRatio),
                                animated = false
                            )
                            Text(
                                text = gallery.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "#${gallery.id}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.clickable {
                                    clipboardManager.setText(AnnotatedString(gallery.id.toString()))
                                }
                            )

                            Tags(
                                tags = gallery.tags,
                                onClick = { category, tag ->
                                    onNavigate(
                                        BrowseRoute(query = "$category:$tag")
                                    )
                                }
                            )

                            if (gallery.saved)
                                TextButton(onClick = { onUnsave() }) {
                                    Text(text = "Unsave")
                                }
                            else
                                Button(onClick = { onSave() }) {
                                    Text(text = "Save")
                                }


                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(Sizes.smaller))
                        }
                    }

                    items(gallery.pages) { page ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Sizes.normal))
                                .clickable {
                                    onNavigate(
                                        ReaderRoute(
                                            id = gallery.id,
                                            page = page.page
                                        )
                                    )
                                }
                        ) {
                            Image(
                                model = page.url,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Sizes.normal))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .fillMaxWidth()
                                    .aspectRatio(page.aspectRatio),
                                animated = false
                            )

                            val isAnimated = page.url.endsWith("gif")

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(Sizes.small, Sizes.small)
                                    .background(
                                        Color.Black.copy(alpha = 0.33f),
                                        RoundedCornerShape(Sizes.smaller)
                                    )
                                    .padding(horizontal = Sizes.smaller)
                            ) {
                                Text(
                                    text = if (isAnimated) "${page.page} • GIF" else page.page.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GalleryScreenPreview() {
    val gallery = Gallery(
        id = 423371,
        mediaId = "2346373",
        saved = false,
        title = "[NEW] Lorem ipsum dolor sit amet, consectetur adipiscing elit",
        cover = GalleryImage(
            url = "https://t5.nhentai.net/galleries/2346373/cover.jpg",
            width = 350,
            height = 496,
            aspectRatio = 0.70564514f
        ),
        thumbnail = GalleryImage(
            url = "https://t3.nhentai.net/galleries/2346373/thumb.jpg",
            width = 250,
            height = 354,
            aspectRatio = 0.70621467f
        ), pages = listOf(
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/1t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/1.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 1
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/2t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/2.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 2
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/3t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/3.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 3
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/4t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/4.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 4
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/5t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/5.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 5
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/6t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/6.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 6
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/7t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/7.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 7
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/8t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/8.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 8
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/9t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/9.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 9
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/10t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/10.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 10
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/11t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/11.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 11
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/12t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/12.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 12
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/13t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/13.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 13
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/14t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/14.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 14
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/15t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/15.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 15
            ),
            GalleryPage(
                url = "https://t3.nhentai.net/galleries/2346373/16t.jpg",
                hdUrl = "https://i.nhentai.net/galleries/2346373/16.jpg",
                width = 1280,
                height = 1813,
                aspectRatio = 0.7060121f,
                page = 16
            )
        ),
        tags = mapOf(
            "language" to listOf("english"),
            "tag" to listOf(
                "nakadashi",
                "full color",
                "stockings",
                "filming",
                "mosaic censorship",
                "sole female",
                "sole male",
                "twintails"
            ),
            "category" to listOf("doujinshi"),
            "parody" to listOf("genshin impact"),
            "character" to listOf("mona megistus", "aether")
        ),
        uploaded = Instant.fromEpochMilliseconds(1665261257000L).toLocalDateTime(TimeZone.UTC)
    )

    AppTheme {
        GalleryScreen(
            onBack = { /*TODO*/ },
            state = GalleryUiState(gallery = gallery, isLoading = false),
            onNavigate = {},
            onSave = {},
            onUnsave = {}
        )
    }
}

fun NavGraphBuilder.galleryScreen(navController: NavController) {
    composable<GalleryRoute> {
        val viewModel: GalleryViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        GalleryScreen(
            state = state,
            onBack = navController::popBackStack,
            onNavigate = navController::navigate,
            onSave = viewModel::saveGallery,
            onUnsave = viewModel::unsaveGallery
        )
    }
}