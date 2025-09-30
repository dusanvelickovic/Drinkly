package com.example.drinkly.ui.venue

import android.location.Location
import android.net.Uri
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
    fun submitReview(
        venueId: String,
        title: String,
        comment: String,
        rating: Int,
        imageUri: Uri?,
        userLocation: Location?,
    ) {
        viewModelScope.launch {
            // Sačuvaj sliku recenzije ako postoji
            val imageUrl: String? = storeVenueReviewImage(imageUri)

            println("Review image URL: $imageUrl")

            // Proveri da li je korisnik u blizini venue-a (npr. unutar 50 metara)
            val verified = if (calculateDistance(userLocation, venueId) <= 50) {
                true
            } else {
                false
            }

            // Sačuvaj recenziju
            storeVenueReview(venueId, title, comment, rating, imageUrl, verified)

            recalculateVenueRating(venueId)

            // Inkrementiraj broj postavljenih recenzija za korisnika
            val incrementValue = if (verified) 2 else 1
            incrementUserReviewsPosted(incrementValue)

            incrementVenueReviewsCount(venueId)
        }
    }

    /**
     * Sačuvaj recenziju za dati venueId
     */
    private suspend fun storeVenueReview(
        venueId: String,
        title: String,
        comment: String,
        rating: Int,
        imageUrl: String?,
        verified: Boolean,
    ) {
        val review = Review(
            userUid = AuthRepository().getAuthUser().getOrThrow()?.uid ?: "",
            title = title,
            comment = comment,
            rating = rating,
            imageUrl = imageUrl,
            verified = verified,
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
    private suspend fun incrementUserReviewsPosted(value: Int) = venueReviewRepository.incrementUserReviewsPosted(value)

    /**
     * Inkrementiraj broj recenzija za dati venueId
     */
    private suspend fun incrementVenueReviewsCount(venueId: String)
        = venueReviewRepository.incrementVenueReviewsCount(venueId)

    /**
     * Sačuvaj sliku recenzije i vrati njen URL
     */
    private suspend fun storeVenueReviewImage(imageUri: Uri?): String? {
        if (imageUri == null) return null

        val result = venueReviewRepository.storeVenueReviewImage(imageUri)
        if (result.isSuccess) {
            println("Successfully stored review image with url: ${result.getOrNull()}")
        } else {
            println("Failed to store review image with message: ${result.exceptionOrNull()?.message}")
        }

        return result.getOrThrow()
    }

    /**
     * Izračunaj udaljenost korisnika od venue
     */
    private suspend fun calculateDistance(userLocation: Location?, venueId: String): Float {
        if (userLocation == null) return Float.MAX_VALUE

        val venueResult = venueRepository.getVenueById(venueId)
        if (venueResult.isSuccess) {
            val venue = venueResult.getOrNull()
            if (venue != null) {
                val venueLocation = Location("").apply {
                    latitude = venue.location.latitude
                    longitude = venue.location.longitude
                }
                return userLocation.distanceTo(venueLocation)
            }
        } else {
            println("Failed to fetch venue for distance calculation: ${venueResult.exceptionOrNull()?.message}")
        }

        return Float.MAX_VALUE
    }
}