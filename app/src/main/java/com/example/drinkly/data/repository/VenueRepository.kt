package com.example.drinkly.data.repository

import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.example.drinkly.data.model.Venue
import kotlinx.coroutines.tasks.await

class VenueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Pretrazi venue po kategoriji i/ili imenu
     */
    suspend fun searchVenues(
        category: String = "all",
        searchQuery: String = "",
        radius: Int = 0,
        userLocation: Location? = null
    ): Result<List<Venue>> = try {
        val baseQuery = if (category == "all") {
            firestore.collection("venues")
        } else {
            firestore.collection("venues").whereEqualTo("category", category)
        }

        val snapshot = baseQuery.get().await()
        val venues = snapshot.documents.mapNotNull { document ->
            try {
                val venue = document.toObject(Venue::class.java)

                venue?.apply {
                    id = document.id
                }?.takeIf { venue ->
                    // Filtriraj po search query ako postoji
                    if (searchQuery.isBlank()) {
                        true // Vrati sve ako nema pretrage
                    } else {
                        venue.name?.contains(searchQuery, ignoreCase = true) == true
                    }
                }
            } catch (e: Exception) {
                println("Failed to convert document ${document.id}")
                null
            }
        }

        // Ako je unet radius i lokacija korisnika, dodatno filtriraj po udaljenosti
        val filteredVenues = if (radius > 0 && userLocation != null) {
            filterVenuesByDistance(venues = venues, userLocation = userLocation, radius = radius)
        } else {
            venues
        }

        Result.success(filteredVenues)
    } catch (e: Exception) {
        val action = if (searchQuery.isBlank()) "fetch" else "search"
        println("Failed to $action venues for category '$category' with query '$searchQuery'")
        Result.failure(e)
    }

    /**
     * Dobavi venue po id
     */
    suspend fun getVenueById(
        id: String
    ): Result<Venue?> = try {
        val document = firestore.collection("venues").document(id).get().await()

        if (document.exists()) {
            val venue = document.toObject(Venue::class.java)?.apply {
                this.id = document.id
            }
            Result.success(venue)
        } else {
            Result.success(null)
        }
    } catch (e: Exception) {
        println("Failed to fetch venue with id '$id': ${e.message}")
        Result.failure(e)
    }

    /**
     * Rekalkuliši prosečnu ocenu za dati venueId na osnovu svih recenzija
     */
    suspend fun recalculateVenueRating(venueId: String): Result<Void?> = try {
        val venueRef = firestore.collection("venues").document(venueId)
        val reviewsSnapshot = venueRef.collection("reviews").get().await()
        val totalReviews = reviewsSnapshot.size()
        val totalRating = reviewsSnapshot.documents.sumOf { it.getLong("rating") ?: 0L }

        val updatedReviewCount = totalReviews.toLong()
        val updatedRating = if (updatedReviewCount > 0) {
            totalRating.toDouble() / updatedReviewCount
        } else {
            0.0
        }

        venueRef.update("rating", updatedRating).await()
        println("Successfully recalculated average rating for venue $venueId")
        Result.success(null)
    } catch (e: Exception) {
        println("Failed to recalculate average rating for venue $venueId: ${e.message}")
        Result.failure(e)
    }

    /**
     * Filtriraj venue po udaljenosti od korisnika
     */
    fun filterVenuesByDistance(
        venues: List<Venue>,
        userLocation: Location,
        radius: Int
    ): List<Venue> {
        return venues.filter { venue ->
            val venueLocation = venue.location
            run {
                val results = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    venueLocation.latitude, venueLocation.longitude,
                    results
                )
                results[0] <= radius * 1000 // radius je u km, distanceBetween vraća u metrima
            }
        }
    }
}