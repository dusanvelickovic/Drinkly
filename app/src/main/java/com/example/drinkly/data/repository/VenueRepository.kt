package com.example.drinkly.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.drinkly.data.model.Venue
import kotlinx.coroutines.tasks.await

class VenueRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Fetch venues
    suspend fun fetchVenues(): Result<List<Venue>> = try {
        val snapshot = db.collection("venues").get().await()
        val venues = snapshot.documents.mapNotNull { document ->
            try {
                val venue = document.toObject(Venue::class.java)
                venue?.apply { id = document.id }
            } catch (e: Exception) {
                println("Failed to convert document ${document.id}")
                null
            }
        }
        Result.success(venues)
    } catch (e: Exception) {
        println("Failed to fetch venues")
        Result.failure(e)
    }

    // Realtime updates (optional)
//    fun listenVenues(): Flow<Result<List<Venue>>> = callbackFlow {
//        val reg = db.collection("venues")
//            .addSnapshotListener { snap, e ->
//                when {
//                    e != null -> trySend(Result.failure(e))
//                    snap != null -> {
//                        val list = snap.documents.mapNotNull { it.toObject(Venue::class.java) }
//                        trySend(Result.success(list))
//                    }
//                }
//            }
//        awaitClose { reg.remove() }
//    }
}