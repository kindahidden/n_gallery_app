package com.elfen.ngallery.data.remote

import android.content.Context
import com.elfen.ngallery.data.local.storeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor constructor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { context.storeToken.getToken.first() }

        val newRequest = originalRequest.newBuilder()
            .apply {
                addHeader("Cookie", "csrftoken=$token")
                addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14; SM-A546B Build/UP1A.231005.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/127.0.6533.105 Mobile Safari/537.36")
            }
            .build()

        return chain.proceed(newRequest)
    }
}