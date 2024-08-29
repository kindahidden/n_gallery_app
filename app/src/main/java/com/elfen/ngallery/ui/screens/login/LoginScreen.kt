package com.elfen.ngallery.ui.screens.login

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elfen.ngallery.R
import com.elfen.ngallery.data.local.prefs.StoreToken
import com.elfen.ngallery.data.local.storeToken
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Login

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Getting Token") },
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
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    webViewClient = WebViewTokenInterceptor(webView = this) { token ->
                        coroutineScope.launch { context.storeToken.setToken(token) }
                        Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                    }

                    loadUrl(context.getString(R.string.login_url))
                }
            },
        )
    }
}

fun NavGraphBuilder.loginScreen(navController: NavController) {
    composable<Login> {
        LoginScreen(
            onBack = navController::popBackStack
        )
    }
}

class WebViewTokenInterceptor(private val webView: WebView,private val onSetToken: (String?) -> Unit) : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val cookies = CookieManager.getInstance().getCookie(url).split("; ")
        val token = cookies
            .find { it.startsWith("csrftoken") }
            ?.split("=")?.get(1)

        Log.d("LoginScreen", "onPageFinished: ${webView.settings.userAgentString}")
        onSetToken(token)
    }
}