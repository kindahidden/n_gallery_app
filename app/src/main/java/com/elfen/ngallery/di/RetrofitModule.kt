package com.elfen.ngallery.di

import android.content.Context
import android.content.SharedPreferences
import android.media.session.MediaSession.Token
import androidx.annotation.NonNull
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.remote.TokenInterceptor
import java.util.Collections;
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

class CustomCookieJar(private val cache: CookieCache, private val persistor: CookiePersistor) :
    ClearableCookieJar {

    init {
        cache.addAll(persistor.loadAll());
    }

    companion object {
        fun filterPersistentCookies(cookies: List<Cookie>): List<Cookie> {
            var persistentCookies: List<Cookie> = java.util.ArrayList();

            for (cookie in cookies) {
                if (cookie.persistent) {
                    persistentCookies += cookie;
                }
            }
            return persistentCookies;
        }

        fun isCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt < System.currentTimeMillis();
        }
    }

    @Override
    override fun saveFromResponse(@NonNull url:HttpUrl, @NonNull cookies:List<Cookie>){
        cache.addAll(cookies);
        persistor.saveAll(filterPersistentCookies(cookies));
    }

    @NonNull
    @Override
    override fun  loadForRequest(@NonNull url:HttpUrl):List<Cookie>    {
        var cookiesToRemove:List<Cookie>  = ArrayList();
        var validCookies:List<Cookie>  = ArrayList();

        val iterator = cache.iterator()
        while (iterator.hasNext()) {
            val currentCookie:Cookie = iterator.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove += (currentCookie);
                iterator.remove();
            } else {
                validCookies += (currentCookie);
            }
        }

        persistor.removeAll(cookiesToRemove);
        return validCookies;
    }

    override fun clearSession() {
        cache.clear()
        cache.addAll(persistor.loadAll())
    }

    override fun clear() {
        cache.clear()
        persistor.clear()
    }

    fun removeCookie(name: String){
        val cookies:List<Cookie> = persistor.loadAll();
        for (cookie in cookies) {
            if (cookie.name == name) {
                cache.clear();
                persistor.removeAll(Collections.singletonList(cookie));
            }
        }
    }
}


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    fun provideBaseUrl(): String = "https://nhentai.net/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context): TokenInterceptor {
        return TokenInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context, baseUrl: String, tokenInterceptor: TokenInterceptor): Retrofit {
        val preferences: SharedPreferences = context.getSharedPreferences("Login", 0);
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient
            .Builder()
            .cookieJar(
                CustomCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(preferences)
                )
            )
            .addNetworkInterceptor(tokenInterceptor)
            .addInterceptor(interceptor)
            .addNetworkInterceptor(interceptor)
            .build()

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("nhentai.net")
            .build()
        val cookie = Cookie.parse(url, "cf_clearance=XzNS9cNPfM7BHt0jXlsPQRvmD5cJq9ssId_SyvWH_BI-1738533674-1.2.1.1-dEhB_TNwtGdnoIZtVbCZhMCIjEaVHXPVBuhd0FXRTtFACYqKwpmWBjbfzvK.hQHrhx6uikoX0hypJCCEkcfPsuAhNgdpqq2DAliCb5GOAfSWD5Jw0KdU_.XUfncXD_9zRJMLEMDrrsI4eLRvIaaukXd86vzLGj1cV5RXwwp54lQ88TasE4ZCHxl9438dWqw6jGfb.IqZdJzmhi.c4etCOhrNVO9bG.mOx4eSpz43UzMrnGQQScbLu6XNc.e1OLE370d5W.zFTlHsiPrSSiMs3V8dst7pRbKEj0GAI1FXqUpvwrsjIZCnc6YFcSM_PrpLHbGYuJrt3Zqe9icl1b37YA; Max-Age=31449600; Path=/;")

        client.cookieJar.saveFromResponse(url, Collections.singletonList(cookie));

        client.dispatcher.maxRequests = 25
        client.dispatcher.maxRequestsPerHost = 25


        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)
}