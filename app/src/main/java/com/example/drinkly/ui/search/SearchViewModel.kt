package com.example.drinkly.ui.search

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.Venue
import com.example.drinkly.data.repository.VenueRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class SearchEvent {
    data object Saved : SearchEvent()
    data class Error(val message: String) : SearchEvent()
}

data class Category(
    val id: String,
    val key: String,
    val name: String,
    val isSelected: Boolean = false
)

class SearchViewModel(
    private val venueRepository: VenueRepository = VenueRepository(),
) : ViewModel() {

    private val eventChannel = Channel<SearchEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val _categories = listOf<Category>(
        Category(id = "all", key = "all", name = "All", isSelected = true),
        Category(id = "1", key="restaurant", name = "Restaurant"),
        Category(id = "2", key="caffe", name = "Caffe"),
        Category(id = "3", key="bar", name = "Bar"),
        Category(id = "4", key="pub", name = "Pub"),
        Category(id = "5", key="kafana", name = "Kafana"),
        Category(id = "6", key="fast_food", name = "Fast Food"),
    )
    val categories: List<Category> = _categories

    private val _venues = mutableStateOf<List<Venue>?>(null)
    val venues: State<List<Venue>?> = _venues

    /**
     * Pretrazi venue po kategoriji i/ili imenu
     */
    fun searchVenues(
        category: String = "all",
        searchQuery: String = "",
        radius: Int = 0,
        userLocation: Location? = null
    ) {
        viewModelScope.launch {
            val result = venueRepository.searchVenues(
                category,
                searchQuery,
                radius,
                userLocation
            )

            if (result.isSuccess) {
                _venues.value = result.getOrNull()
                val logMessage = if (searchQuery.isBlank()) {
                    "Fetched venues for category $category: ${_venues.value?.size} results"
                } else {
                    "Search results for '$searchQuery' in category '$category': ${_venues.value?.size} results"
                }
                println(logMessage)
            } else {
                val exception = result.exceptionOrNull()
                val errorMessage = if (searchQuery.isBlank()) {
                    exception?.message ?: "Failed to fetch venues"
                } else {
                    exception?.message ?: "Search failed"
                }
                eventChannel.send(SearchEvent.Error(errorMessage))
            }
        }
    }
}