package com.example.drinkly.ui.venue

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
    private val venueRepository: VenueRepository = VenueRepository(),

) : ViewModel() {
    private val _reviewsFlow = MutableStateFlow<Result<List<Review>>>(Result.success(emptyList()))
    val reviewsFlow: StateFlow<Result<List<Review>>> = _reviewsFlow

    /**
     * Dobavi recenzije za dati venueId
     */
    fun observeReviewsForVenue(venueId: String) {
        viewModelScope.launch {
            venueReviewRepository.observeReviewsForVenue(venueId).collect { result ->
                _reviewsFlow.value = result
            }
        }
    }

    /**
     * Postavi recenziju za dati venueId
     */
    fun submitReview(venueId: String, title: String, comment: String, rating: Int) {
        viewModelScope.launch {
            storeVenueReview(venueId, title, comment, rating)
            recalculateVenueRating(venueId)
            incrementUserReviewsPosted()
            incrementVenueReviewsCount(venueId)
        }
    }

    /**
     * Sačuvaj recenziju za dati venueId
     */
    private suspend fun storeVenueReview(venueId: String, title: String, comment: String, rating: Int) {
        val review = Review(
            userUid = AuthRepository().getAuthUser().getOrThrow()?.uid ?: "",
            title = title,
            comment = comment,
            rating = rating
        )

        val result = venueReviewRepository.storeVenueReview(venueId, review)
        if (result.isSuccess) {
            println("Successfully stored review for venue $venueId")
        } else {
            println("Failed to store review for venue $venueId: ${result.exceptionOrNull()?.message}")
        }
    }

    /**
     * Rekalkuliši ocenu za dati venueId
     */
    private suspend fun recalculateVenueRating(venueId: String) = venueRepository.recalculateVenueRating(venueId)

    /**
     * Inkrementiraj broj postavljenih recenzija za korisnika
     */
    private suspend fun incrementUserReviewsPosted() = venueReviewRepository.incrementUserReviewsPosted()

    /**
     * Inkrementiraj broj recenzija za dati venueId
     */
    private suspend fun incrementVenueReviewsCount(venueId: String)
        = venueReviewRepository.incrementVenueReviewsCount(venueId)
}