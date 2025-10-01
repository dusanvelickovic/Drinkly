package com.example.drinkly.data.repository

import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.example.drinkly.data.model.Venue
import kotlinx.coroutines.tasks.await

class VenueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Search venues by category, name, and optional radius from user's location.
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
     * Fetch venue by its ID
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
     * Recalculate average rating for a venue based on its reviews.
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
     * Filter venues based on distance from user's location.
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

    /**
     * Fetch venues that are nearby the user's current location.
     * This is a placeholder function and should be implemented as needed.
     */
    suspend fun fetchNearbyVenues(
        userLocation: Location,
        radiusInMeters: Int
    ): Result<List<Venue>> = try {
        // Fetch all venue documents
        val snapshot = firestore.collection("venues").get().await()

        // Map documents to Venue objects
        val venues = snapshot.documents.mapNotNull { document ->
            try {
                // Correctly convert the document to the Venue data class
                val venue = document.toObject(Venue::class.java)

                // Set the document ID (the venue's unique ID)
                venue?.apply { id = document.id }
                venue
            } catch (e: Exception) {
                println("Failed to convert document ${document.id}: ${e.message}")
                null
            }
        }

        // Filter venues based on distance
        val nearbyVenues = venues.filter { venue ->
            // Ensure the venue has a valid GeoPoint location
            venue.location?.let { geoPoint ->
                val results = FloatArray(1)

                // Calculate distance between the user's location and the venue's location
                Location.distanceBetween(
                    userLocation.latitude,
                    userLocation.longitude,
                    geoPoint.latitude, // Venue latitude from GeoPoint
                    geoPoint.longitude, // Venue longitude from GeoPoint
                    results
                )
                // Check if the distance is within the specified radius
                results[0] <= radiusInMeters
            } ?: false // If location is null, treat it as not nearby
        }

        Result.success(nearbyVenues)
    } catch (e: Exception) {
        println("Failed to fetch nearby venues: ${e.message}")
        Result.failure(e)
    }
}