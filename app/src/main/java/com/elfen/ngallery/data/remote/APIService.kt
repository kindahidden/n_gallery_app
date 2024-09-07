package com.elfen.ngallery.data.remote

import com.elfen.ngallery.data.remote.models.NetworkGallery
import com.elfen.ngallery.data.remote.models.NetworkResult
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("/api/galleries/search")
    suspend fun searchGalleries(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): NetworkResult

    @GET("/api/gallery/{id}")
    suspend fun getGallery(@Path("id") id: Int): NetworkGallery

    @GET("https://nhentai.net/search/")
    suspend fun searchGalleriesHTML(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): ResponseBody
}