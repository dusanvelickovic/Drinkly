package com.example.drinkly.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.drinkly.data.model.Venue
import kotlinx.coroutines.tasks.await
import kotlin.text.get

class VenueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Search venues
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

    // Get venue by ID
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
}