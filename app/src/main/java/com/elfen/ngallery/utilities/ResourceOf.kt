package com.elfen.ngallery.utilities

import android.database.sqlite.SQLiteException
import com.elfen.ngallery.R
import com.elfen.ngallery.models.Resource
import com.google.gson.JsonParseException
import okio.IOException


suspend fun <T> resourceOf(call: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: IOException) {
        e.printStackTrace()
        Resource.Error("Not connected to the internet")
    } catch (e: retrofit2.HttpException) {
        e.printStackTrace()
        Resource.Error(if (e.code() == 404) "Result not found" else "something went wrong")
    } catch (e: JsonParseException) {
        e.printStackTrace()
        Resource.Error("Failed to parse json")
    } catch (e: SQLiteException) {
        e.printStackTrace()
        Resource.Error("Failed to save to database")
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error("Something went wrong")
    }
}
