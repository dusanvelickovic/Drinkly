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
        Category(id = "all", name = "Sve", isSelected = true),
        Category(id = "1", name = "Restoran"),
        Category(id = "2", name = "Kafić"),
        Category(id = "3", name = "Bar"),
        Category(id = "4", name = "Pivnica"),
        Category(id = "5", name = "Kafana"),
        Category(id = "6", name = "Fast Food"),
    )
    val categories: List<Category> = _categories

    private val _venues = mutableStateOf<List<Venue>?>(null)
    val venues: State<List<Venue>?> = _venues

    fun fetchVenues() {
        viewModelScope.launch {
            val result = venueRepository.fetchVenues()
            if (result.isSuccess) {
                _venues.value = result.getOrNull()
                println("Fetched venues: ${_venues.value}")
            } else {
                val exception = result.exceptionOrNull()
                eventChannel.send(SearchEvent.Error(exception?.message ?: "Unknown error"))
            }
        }
    }
}