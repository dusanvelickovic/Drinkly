package com.example.drinkly.data.repository

import com.example.drinkly.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VenueReviewRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getReviewsForVenue(venueId: String): Result<List<Review>> {
        return try {
            val snapshot = firestore.collection("venues")
                .document(venueId)
                .collection("reviews")
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { document ->
                try {
                    val review = document.toObject(Review::class.java)
                    review?.apply {
                        id = document.id
                    }
                } catch (e: Exception) {
                    println("Failed to convert document ${document.id} to Review. Error: ${e.message}")
                    null
                }
            }

            Result.success(reviews)
        } catch (e: Exception) {
            println("Failed to fetch reviews for venue '$venueId': ${e.message}")
            Result.failure(e)
        }
    }
}