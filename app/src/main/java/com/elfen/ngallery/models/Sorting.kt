package com.elfen.ngallery.models

enum class Sorting(val value: String) {
    POPULAR("popular"),
    POPULAR_WEEK("popular-week"),
    POPULAR_MONTH("popular-month"),
    POPULAR_TODAY("popular-today"),
    RECENT("recent")
}

fun Sorting.toDisplay() = when(this){
    Sorting.POPULAR -> "Popular"
    Sorting.POPULAR_WEEK -> "Week"
    Sorting.RECENT -> "Now"
    Sorting.POPULAR_MONTH -> "Month"
    Sorting.POPULAR_TODAY -> "Today"
}