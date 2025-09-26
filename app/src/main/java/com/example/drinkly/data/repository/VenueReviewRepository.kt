package com.example.drinkly.data.repository

import com.example.drinkly.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VenueReviewRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Dobavi recenzije za dati venueId iz firestore
     */
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

                        // Fetch user data based on userUid and add as dynamic attribute
                        val userUid = this.userUid

                        val userSnapshot = firestore.collection("users").document(userUid).get().await()
                        val user = userSnapshot.data

                        if (user != null) {
                            this.user = user
                        }

                        println("Successfully converted document ${document.id} to Review.")
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

    /**
     * Sačuvaj recenziju za dati venueId iz firestore
     */
    suspend fun storeVenueReview(venueId: String, review: Review): Result<Void?> {
        return try {
            val reviewData = hashMapOf(
                "user_uid" to review.userUid,
                "title" to review.title,
                "comment" to review.comment,
                "rating" to review.rating,
                "date" to review.date
            )

            val documentRef = firestore.collection("venues")
                .document(venueId)
                .collection("reviews")
                .add(reviewData)
                .await()

            println("Successfully stored review with ID: ${documentRef.id} for venue: $venueId")
            Result.success(null)
        } catch (e: Exception) {
            println("Failed to store review for venue '$venueId': ${e.message}")
            Result.failure(e)
        }
    }
}