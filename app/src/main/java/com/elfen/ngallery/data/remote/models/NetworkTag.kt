package com.elfen.ngallery.data.remote.models

data class NetworkTag(
    val id: Int,
    val type: String,
    val name: String,
    val url: String,
    val count: Int
)


fun List<NetworkTag>.toMap(): Map<String, List<String>> {
    val categories = this.map { it.type }.distinct()
    val tagsMap = emptyMap<String, List<String>>().toMutableMap()

    categories.forEach {category ->
        val tagsFiltered = this
            .filter { it.type == category }
            .map { it.name }
        tagsMap += Pair(category, tagsFiltered)
    }

    return tagsMap.toMap()
}