package com.example.drinkly.ui.venue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.Venue
import com.example.drinkly.data.repository.VenueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VenueViewModel(
    private val venueRepository: VenueRepository = VenueRepository()
) : ViewModel() {
    private val _venue = MutableStateFlow<Venue?>(null)
    val venue: StateFlow<Venue?> = _venue

    fun getVenueById(id: String) {
        viewModelScope.launch {
            val result = venueRepository.getVenueById(id)
            _venue.value = if (result.isSuccess) {
                println("Loaded venue with id $id")
                result.getOrNull()
            } else {
                result.exceptionOrNull()?.message ?: "Failed to load venue"
            } as Venue
        }
    }
}