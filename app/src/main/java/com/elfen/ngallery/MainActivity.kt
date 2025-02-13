package com.elfen.ngallery

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.imageLoader
import com.elfen.ngallery.ui.Navigation
import com.elfen.ngallery.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initiateNotificationChannels()

        val requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasUserAccepted ->
            Log.d("MainActivity", "hasUserAccepted: $hasUserAccepted")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    if (SDK_INT >= 28) {
                        add(AnimatedImageDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }
        setContent {


            AppTheme {
                Surface {
                    val navController = rememberNavController()
                    Navigation(navHostController = navController)
                }
            }
        }
    }

    private fun initiateNotificationChannels() {
        val channel = NotificationChannel(
            "DOWNLOAD_CHANNEL",
            "Download Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}