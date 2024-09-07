package com.elfen.ngallery.ui.screens.browse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elfen.ngallery.data.remote.APIService
import com.elfen.ngallery.data.repository.GalleryRepository
import com.elfen.ngallery.models.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(FlowPreview::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val routeParams = savedStateHandle.toRoute<BrowseRoute>()
    private val _query = MutableStateFlow(routeParams.query ?: "")
    private val _page = MutableStateFlow(1)
    private val _sorting = MutableStateFlow(Sorting.POPULAR)
    private val _state = MutableStateFlow(BrowseUiState())
    val state: StateFlow<BrowseUiState> = combine(_state, _query, _page,_sorting) { state, query, page, sorting ->
        state.copy(query = query, page = page, sorting = sorting)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowseUiState())

    init {
        viewModelScope.launch {
            combine(
                _query.debounce(500.toDuration(DurationUnit.MILLISECONDS)),
                _page,
                _sorting
            ) { query, page, sorting -> Triple(query, page, sorting) }
                .collect { (query, page) ->
                    if (query.isNotEmpty() && query.length > 1)
                        fetchQuery(query, page)
                    else
                        fetchQuery("*", page)
                }
        }
    }

    private suspend fun fetchQuery(query: String, page: Int) {
        _state.update { it.copy(isLoading = true) }
        val data = galleryRepository.searchGalleries(query = query, _sorting.value, page = page)
        _state.update { it.copy(data = data, isLoading = false) }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun setPage(page: Int) {
        if (page >= 1)
            _page.update { page }
    }

    fun setSorting(sorting: Sorting){
        _sorting.update { sorting }
    }
}