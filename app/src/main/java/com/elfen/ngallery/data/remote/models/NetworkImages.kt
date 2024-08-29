package com.elfen.ngallery.data.remote.models

data class NetworkImages(
    val pages: List<NetworkImage>,
    val cover: NetworkImage,
    val thumbnail: NetworkImage,
)
