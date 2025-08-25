package com.example.drinkly.ui.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.Venue
import com.example.drinkly.data.repository.VenueRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
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
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val venueRepository: VenueRepository = VenueRepository(),
) : ViewModel() {

    private val eventChannel = Channel<SearchEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

//    fun addSampleVenueOnce() {
//        println("Adding sample venue to Firestore...")
//
//        val venue = Venue(
//            name = "My Venue",
//            address = "123 Main St",
//            location = GeoPoint(37.7749, -122.4194),
//            type = "Bar"
//        )
//
//        db.collection("venues")
//            .add(venue)
//            .addOnSuccessListener {
////                viewModelScope.launch { eventChannel.send(SearchEvent.Saved) }
//            }
//            .addOnFailureListener { e ->
////                viewModelScope.launch { eventChannel.send(SearchEvent.Error(e.message ?: "Unknown error")) }
//            }
//    }

    private val _categories = listOf<Category>(
        Category(id = "all", key = "all", name = "Sve", isSelected = true),
        Category(id = "1", key="restaurant", name = "Restoran"),
        Category(id = "2", key="caffe", name = "Kafić"),
        Category(id = "3", key="bar", name = "Bar"),
        Category(id = "4", key="pub", name = "Pivnica"),
        Category(id = "5", key="kafana", name = "Kafana"),
        Category(id = "6", key="fast_food", name = "Fast Food"),
    )
    val categories: List<Category> = _categories

    private val _venues = mutableStateOf<List<Venue>?>(null)
    val venues: State<List<Venue>?> = _venues

    fun fetchVenues(category: String = "all") {
        viewModelScope.launch {
            val result = venueRepository.fetchVenues(category)
            if (result.isSuccess) {
                _venues.value = result.getOrNull()
                println("Fetched venues for category $category: ${_venues.value}")
            } else {
                val exception = result.exceptionOrNull()
                eventChannel.send(SearchEvent.Error(exception?.message ?: "Unknown error"))
            }
        }
    }
}