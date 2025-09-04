package com.example.drinkly.data.repository

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
    companion object {
        private const val VENUES_COLLECTION = "venues"
        private const val MENU_ITEMS_COLLECTION = "menu_items"
        private const val FIELD_AVAILABLE = "available"
        private const val FIELD_CATEGORY = "category"
        private const val VALUE_TRUE = "true"
        private const val CATEGORY_DRINK = "drink"
        private const val CATEGORY_FOOD = "food"
    }

    // Dobijanje svih menu items za određeni venue
    suspend fun getMenuItemsForVenue(venueId: String): List<MenuItem> {
        return try {
            val snapshot = firestore
                .collection(VENUES_COLLECTION)
                .document(venueId)
                .collection(MENU_ITEMS_COLLECTION)
                .whereEqualTo(FIELD_AVAILABLE, VALUE_TRUE)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject<MenuItem>()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}