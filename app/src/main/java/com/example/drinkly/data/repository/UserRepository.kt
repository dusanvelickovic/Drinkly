package com.example.drinkly.data.repository

import android.location.Location
import com.example.drinkly.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val authRepository: AuthRepository = AuthRepository()
) {
    /**
     * Fetch all users within a certain radius from a given location.
     */
    suspend fun fetchNearbyUsers(
        userLocation: Location,
        radiusInMeters: Double
    ): Result<List<User>> = try {
        val snapshot = firestore.collection("users").get().await()
        val users = snapshot.documents.mapNotNull { document ->
            try {
                val user = document.toObject(User::class.java)
                user?.apply { uid = document.id }
            } catch (e: Exception) {
                println("Failed to convert document ${document.id}")
                null
            }
        }

        // Filter users based on distance
        val nearbyUsers = users.filter { user ->
            val isNearby = user.location?.let { geoPoint ->
                val results = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    geoPoint.latitude, geoPoint.longitude,
                    results
                )
                results[0] <= radiusInMeters
            } ?: false

            val wasRecentlyActive = user.lastActiveAt?.let { lastActive ->
                val fifteenSecondsAgo = System.currentTimeMillis() - 15000
                lastActive.toDate().time >= fifteenSecondsAgo
            } ?: false

            isNearby && wasRecentlyActive && user.uid != authRepository.getAuthUser().getOrNull()?.uid
        }

        Result.success(nearbyUsers)
    } catch (e: Exception) {
        println("Failed to fetch nearby users")
        Result.failure(e)
    }
}