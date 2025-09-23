package com.example.drinkly.data.repository

import android.util.Log
import com.example.drinkly.data.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Dobijanje svih menu items za određeni venue
     */
    suspend fun getMenuItemsForVenue(venueId: String): List<MenuItem> {
        return try {
            val snapshot = firestore
                .collection("venues")
                .document(venueId)
                .collection("menu_items")
                .whereEqualTo("available", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject<MenuItem>()
            }
        } catch (e: Exception) {
            Log.e("MenuItemRepository", "Error getting menu items: ${e.message}")
            emptyList()
        }
    }
}