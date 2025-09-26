package com.example.drinkly.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.Review
import com.example.drinkly.data.repository.AuthRepository
import com.example.drinkly.data.repository.VenueReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VenueReviewViewModel(
    private val venueReviewRepository: VenueReviewRepository = VenueReviewRepository()
) : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun getReviewsForVenue(venueId: String) {
        viewModelScope.launch {
            val result = venueReviewRepository.getReviewsForVenue(venueId)
            if (result.isSuccess) {
                _reviews.value = result.getOrNull() ?: emptyList()
                println("Loaded ${_reviews.value.size} reviews for venue $venueId")
            } else {
                println("Failed to load reviews for venue $venueId: ${result.exceptionOrNull()?.message}")
                _reviews.value = emptyList()
            }
        }
    }
}