package com.elfen.ngallery.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.local.AppDatabase
import com.elfen.ngallery.data.local.dao.GalleryDao
import com.elfen.ngallery.data.repository.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    galleryRepository: GalleryRepository
) : ViewModel() {
    val galleries = galleryRepository.getSavedGalleriesFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}