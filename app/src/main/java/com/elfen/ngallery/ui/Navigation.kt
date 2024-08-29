package com.elfen.ngallery.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.elfen.ngallery.ui.screens.browse.browseScreen
import com.elfen.ngallery.ui.screens.gallery.galleryScreen
import com.elfen.ngallery.ui.screens.home.Home
import com.elfen.ngallery.ui.screens.home.homeScreen
import com.elfen.ngallery.ui.screens.login.loginScreen
import com.elfen.ngallery.ui.screens.reader.readerRoute
import kotlin.math.roundToInt

const val ANIM_DURATION_MILLIS = 150

@Composable
fun Navigation(navHostController: NavHostController) {
    val spec = remember{ tween<Float>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }
    val specInt = remember { tween<IntOffset>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }

    NavHost(
        navController = navHostController,
        startDestination = Home,
        enterTransition = {
            fadeIn() + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIM_DURATION_MILLIS),
            ) + scaleIn(initialScale = 0.9f)
        },
        exitTransition = {
            fadeOut(animationSpec = tween(ANIM_DURATION_MILLIS)) + scaleOut(
                targetScale = 0.75f
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = spec) +
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = spec
                    ) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        initialOffset = { (it * 0.15f).roundToInt() },
                        animationSpec = specInt
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = spec) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        targetOffset = { (it * 0.15f).roundToInt() },
                        animationSpec = specInt
                    ) +
                    scaleOut(
                        targetScale = 0.85f,
                        animationSpec = spec
                    )
        },
    ) {
        homeScreen(navHostController)
        browseScreen(navHostController)
        loginScreen(navHostController)
        galleryScreen(navHostController)
        readerRoute(navHostController)
    }
}