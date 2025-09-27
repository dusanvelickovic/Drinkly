package com.example.drinkly.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.drinkly.data.model.Venue
import kotlinx.coroutines.tasks.await
import kotlin.text.get
import kotlin.text.toDouble
import kotlin.text.toLong

class VenueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Pretrazi venue po kategoriji i/ili imenu
     */
    suspend fun searchVenues(
        category: String = "all",
        searchQuery: String = ""
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

        Result.success(venues)
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
}