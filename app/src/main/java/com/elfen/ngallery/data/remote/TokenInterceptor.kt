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

        Log.d("TokenInterceptor", "intercept: $token")
        val newRequest = originalRequest.newBuilder()
            .apply {
                addHeader("Cookie", "csrftoken=$token; cf_clearance=vRyMjIqfcj29_2AKS6oUWXYc_5hKyso1GI.VtXYgsAA-1738329260-1.2.1.1-qR9DzfV7VMziv9Zq3WLyrBQyv_fbf1l9NPo1D_TIvVSg664xU2834XDvv_SEfTEJfnRq0oTe8tnK.73dzqiFsxio23mJEEW3GOrTVjUeN4kuow9HyY5P4ZTLoSdW9RlHeqAYyhOveHzcDW1XGHliCrArpJhHKIlRarSeMbNMgMsv6Yv5douVRzaZRfSQ5QqVyNMwknsrjVUdXJtCNAkCXjBp6g.z_V2f1vWDh9B.kNrPbYKBxwr9Ef9ClsrNFPMf5eORRAU6qc8qinKyQmnYZJajHOllI7jJVKGc.zWOecO6Sm97LmxsPr7uehPoEK7A4JuG9gdhxT4NNBnzIJGh7w")
//                addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14; SM-A546B Build/UP1A.231005.007; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/127.0.6533.105 Mobile Safari/537.36")
//                addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 15; en-us; sdk_gphone64_x86_64 Build/AE3A.240806.005; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/124.0.6367.219 Mobile Safari/537.36 GoogleApp/15.22.33.28.x86_64")
                addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64; rv:134.0) Gecko/20100101 Firefox/134.0")
            }
            .build()

        return chain.proceed(newRequest)
    }
}