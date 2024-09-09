package com.elfen.ngallery.ui.screens.extract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtractViewModel @Inject constructor(
    private val repository: GalleryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExtractUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            state.map { it.text }.collect {
                val regex = Regex("(?<!\\d)\\d{5,6}(?!\\d)")
                val matches = regex.findAll(it).toList().map { match -> match.value.toInt() }
                _state.update { state -> state.copy(idList = matches) }
            }
        }
    }

    fun onChange(text: String) {
        _state.update { it.copy(text = text) }
    }

    private fun updateGalleryState(id: Int, state: GalleryLoadingState) {
        _state.update {
            val galleries = it.galleries.toMutableMap()
            galleries[id] = state

            it.copy(galleries = galleries)
        }
    }

    fun requestGallery(id: Int) {
        viewModelScope.launch {
            updateGalleryState(id, GalleryLoadingState.Loading)
            when (val result = repository.fetchGalleryById(id)) {
                is Resource.Success -> updateGalleryState(
                    id,
                    GalleryLoadingState.Loaded(result.data!!)
                )

                is Resource.Error -> updateGalleryState(
                    id,
                    GalleryLoadingState.Error(result.message!!)
                )
            }
        }
    }
}