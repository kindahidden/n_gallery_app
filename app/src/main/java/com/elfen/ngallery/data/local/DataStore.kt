package com.elfen.ngallery.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.elfen.ngallery.data.local.prefs.StoreToken

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val Context.storeToken: StoreToken
    get() = StoreToken(this)