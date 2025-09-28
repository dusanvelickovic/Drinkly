package com.example.drinkly.data.repository

import com.example.drinkly.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LeaderboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Dobavi top korisnike sortirane po broju postavljenih ocena
     */
    fun observeTopUsers(limit: Int = 50): Flow<Result<List<User>>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereGreaterThan("reviews_posted", 0)
            .orderBy("reviews_posted", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Failed to fetch top users: ${error.message}")
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                try {
                    val users = snapshot?.documents?.mapNotNull { document ->
                        try {
                            val user = document.toObject(User::class.java)
                            user?.copy(uid = document.id)
                        } catch (e: Exception) {
                            println("Failed to convert document ${document.id} to User. Error: ${e.message}")
                            null
                        }
                    } ?: emptyList()

                    trySend(Result.success(users))
                } catch (e: Exception) {
                    println("Error processing snapshot: ${e.message}")
                    trySend(Result.failure(e))
                }
            }

        // Clean up
        awaitClose {
            listener.remove()
            println("Top users listener removed")
        }
    }
}