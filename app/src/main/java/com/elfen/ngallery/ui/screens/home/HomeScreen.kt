package com.elfen.ngallery.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfen.ngallery.ui.screens.browse.Browse
import com.elfen.ngallery.ui.theme.Sizes
import kotlinx.serialization.Serializable

@Serializable
object Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowse: (String?) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Home") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Search") },
                icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                onClick = { onNavigateToBrowse(null) })
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(Sizes.medium)) {
            Text(text = "Hello World")
            Button(onClick = { onNavigateToBrowse(null) }) {
                Text(text = "Navigate to browse Screen")
            }
            Button(onClick = { onNavigateToBrowse("hello world") }) {
                Text(text = "Navigate to browse Screen with query")
            }
        }
    }
}

fun NavGraphBuilder.homeScreen(navController: NavController) {
    composable<Home> {
        HomeScreen(
            onNavigateToBrowse = { navController.navigate(Browse(it)) }
        )
    }
}