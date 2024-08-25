package com.elfen.ngallery.ui.screens.browse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elfen.ngallery.ui.theme.Sizes
import kotlinx.serialization.Serializable

@Serializable
data class Browse(val query: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    browse: Browse,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Browse")
                },
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
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(Sizes.medium)
        ) {
            Text(text = "This is browse screen")
            if(!browse.query.isNullOrEmpty()){
                Text(text = "Initial Query: ${browse.query}")
            }
        }
    }
}

fun NavGraphBuilder.browseScreen(navController: NavController){
    composable<Browse> {
        val browse = it.toRoute<Browse>()
        BrowseScreen(
            browse = browse,
            onBack = navController::popBackStack
        )
    }
}