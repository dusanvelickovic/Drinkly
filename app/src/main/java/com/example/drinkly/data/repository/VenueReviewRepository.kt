package com.example.drinkly.data.repository

import android.net.Uri
import com.example.drinkly.data.helper.CloudinaryHelper
import com.example.drinkly.data.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class VenueReviewRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Dobavi recenzije za dati venueId iz firestore
     */
    fun observeReviewsForVenue(venueId: String): Flow<Result<List<Review>>> = callbackFlow {
        val registration = firestore.collection("venues")
            .document(venueId)
            .collection("reviews")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // pokreni coroutine za obradu user podataka
                    launch {
                        val reviews = snapshot.documents.mapNotNull { document ->
                            try {
                                val review = document.toObject(Review::class.java)
                                review?.apply {
                                    id = document.id

                                    // asinhrono fetch user data
                                    val userUid = this.userUid
                                    val userSnapshot = firestore.collection("users")
                                        .document(userUid)
                                        .get()
                                        .await()

                                    user = if (userSnapshot.exists()) {
                                        userSnapshot.data ?: emptyMap()
                                    } else {
                                        emptyMap()
                                    }
                                }
                            } catch (e: Exception) {
                                println("Failed to convert ${document.id}: ${e.message}")
                                null
                            }
                        }

                        trySend(Result.success(reviews))
                    }
                }
            }

        awaitClose { registration.remove() }
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
                "date" to review.date,
                "image_url" to review.imageUrl,
                "verified" to review.verified,
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

    /**
     * Inkrementiraj broj recenzija za autentifikovanog korisnika
     */
    suspend fun incrementUserReviewsPosted(value: Int): Result<Void?> {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val userRef = firestore.collection("users").document(userUid)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentCount = snapshot.getLong("reviews_posted") ?: 0
                transaction.update(userRef, "reviews_posted", currentCount + value)
            }.await()

            println("Successfully incremented reviewsPosted for user: $userUid")
            Result.success(null)
        } catch (e: Exception) {
            println("Failed to increment reviewsPosted for user '$userUid': ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Inkrementiraj broj recenzija za dati venueId
     */
    suspend fun incrementVenueReviewsCount(venueId: String): Result<Void?> {
        return try {
            val venueRef = firestore.collection("venues").document(venueId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(venueRef)
                val currentCount = snapshot.getLong("reviews_count") ?: 0
                transaction.update(venueRef, "reviews_count", currentCount + 1)
            }.await()

            println("Successfully incremented reviews_count for venue: $venueId")
            Result.success(null)
        } catch (e: Exception) {
            println("Failed to increment reviews_count for venue '$venueId': ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Samo sačuvaj sliku recenzije u Cloudinary i vrati URL
     */
    suspend fun storeVenueReviewImage(imageUri: Uri): Result<String> {
        return try {
            val imageUrl = CloudinaryHelper.uploadImageToCloudinary(imageUri)

            println("Successfully uploaded review image to Cloudinary: $imageUrl")
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}