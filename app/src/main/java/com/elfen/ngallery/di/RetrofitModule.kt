package com.elfen.ngallery.di

import android.content.Context
import android.media.session.MediaSession.Token
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.remote.TokenInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    fun provideBaseUrl(): String = "https://nhentai.net/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context):TokenInterceptor{
        return TokenInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, tokenInterceptor: TokenInterceptor): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient
            .Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(interceptor)
            .build()

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