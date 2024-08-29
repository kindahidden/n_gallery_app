package com.elfen.ngallery.data.remote.models

import com.google.gson.annotations.SerializedName

data class NetworkResult(
    val result: List<NetworkGallery>,
    @SerializedName("num_pages") val numPages: Int,
    @SerializedName("per_page") val perPage: Int,
)
