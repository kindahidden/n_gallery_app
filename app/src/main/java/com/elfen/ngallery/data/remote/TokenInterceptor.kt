package com.elfen.ngallery.data.remote

import android.content.Context
import android.util.Log
import com.elfen.ngallery.data.local.storeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor constructor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { context.storeToken.getToken.first() }
        val agent = runBlocking { context.storeToken.getAgent.first() }

        Log.d("TokenInterceptor", "intercept: $token")
        val newRequest = originalRequest.newBuilder()
            .apply {
                removeHeader("Cookie");
                removeHeader("User-Agent")
                addHeader("Cookie", token.toString())
                addHeader("User-Agent", agent.toString())
            }
            .build()

        return chain.proceed(newRequest)
    }
}