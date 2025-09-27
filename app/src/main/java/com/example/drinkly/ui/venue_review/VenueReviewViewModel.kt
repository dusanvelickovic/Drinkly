package com.example.drinkly.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.Review
import com.example.drinkly.data.repository.AuthRepository
import com.example.drinkly.data.repository.VenueRepository
import com.example.drinkly.data.repository.VenueReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VenueReviewViewModel(
    private val venueReviewRepository: VenueReviewRepository = VenueReviewRepository(),
    private val venueRepository: VenueRepository = VenueRepository()
) : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    /**
     * Dobavi recenzije za dati venueId
     */
    fun getReviewsForVenue(venueId: String) {
        viewModelScope.launch {
            val result = venueReviewRepository.getReviewsForVenue(venueId)
            if (result.isSuccess) {
                _reviews.value = result.getOrNull() ?: emptyList()
                println("Loaded ${_reviews.value.size} reviews for venue $venueId")

                // print each review for debugging
                _reviews.value.forEach { review ->
                    println("Review: ${review.id}, Title: ${review.title}, Rating: ${review.rating}, User: ${review.user}")
                }
            } else {
                println("Failed to load reviews for venue $venueId: ${result.exceptionOrNull()?.message}")
                _reviews.value = emptyList()
            }
        }
    }

    /**
     * Sačuvaj recenziju za dati venueId
     */
    suspend fun storeVenueReview(venueId: String, title: String, comment: String, rating: Int) {
        val review = Review(
            userUid = AuthRepository().getAuthUser().getOrThrow()?.uid ?: "",
            title = title,
            comment = comment,
            rating = rating
        )

        val result = venueReviewRepository.storeVenueReview(venueId, review)
        if (result.isSuccess) {
            getReviewsForVenue(venueId)
        } else {
            println("Failed to store review for venue $venueId: ${result.exceptionOrNull()?.message}")
        }
    }

    /**
     * Rekalkuliši ocenu za dati venueId
     */
    fun recalculateVenueRating(venueId: String) {
         viewModelScope.launch {
             val result = venueRepository.recalculateVenueRating(venueId)
             if (result.isSuccess) {
                 println("Successfully recalculated rating for venue $venueId")
             } else {
                 println("Failed to recalculate rating for venue $venueId: ${result.exceptionOrNull()?.message}")
             }
         }
     }
}