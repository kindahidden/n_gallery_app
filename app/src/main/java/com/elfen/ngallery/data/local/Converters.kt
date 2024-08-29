package com.elfen.ngallery.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromTagsMapToString(tags: Map<String, List<String>>): String{
        return Gson().toJson(tags)
    }

    @TypeConverter
    fun fromStringToTagsMap(value: String): Map<String, List<String>>{
        return Gson().fromJson(value, Map::class.java) as Map<String, List<String>>
    }
}