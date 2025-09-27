package com.example.drinkly.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LeaderboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Dobavi top korisnike sortirane po broju postavljenih ocena
     */
    suspend fun getTopUsers(limit: Int = 50): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection("users")
                .orderBy("reviews_posted", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data
                    if (data != null) {
                        data["id"] = document.id // Dodaj ID dokumenta u mapu
                        println("Successfully converted document ${document.id} to User data.")
                    }
                    data
                } catch (e: Exception) {
                    println("Failed to convert document ${document.id} to User data. Error: ${e.message}")
                    null
                }
            }

            Result.success(users)
        } catch (e: Exception) {
            println("Failed to fetch top users: ${e.message}")
            Result.failure(e)
        }
    }
}