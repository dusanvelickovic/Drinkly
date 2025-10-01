package com.example.drinkly.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class User(
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String? = null,

    @get:PropertyName("phone") @set:PropertyName("phone")
    var phone: String? = null,

    @get:PropertyName("bio") @set:PropertyName("bio")
    var bio: String? = null,

    @get:PropertyName("reviews_posted") @set:PropertyName("reviews_posted")
    var reviewsPosted: Int = 0,

    @get:PropertyName("profile_image_url") @set:PropertyName("profile_image_url")
    var profileImageUrl: String? = null,

    @get:PropertyName("location") @set:PropertyName("location")
    var location: GeoPoint? = null,

    @get:PropertyName("last_active_at") @set:PropertyName("last_active_at")
    var lastActiveAt: Timestamp? = null,

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now(),
) {
    // Potreban prazan konstruktor za Firestore
    constructor() : this("", "", "", null, "", 0, null, null, null, Timestamp.now())

    val firstName: String?
        get() = name?.trim()?.split("\\s+".toRegex())?.firstOrNull()?.takeIf { it.isNotBlank() }

    /**
     * Na osnovu imena korisnika vraća inicijale.
     */
    fun getInitials(): String? {
        val parts = name?.trim()?.split("\\s+".toRegex()) ?: return null
        return when (parts.size) {
            0 -> null
            1 -> parts[0].take(2).uppercase()
            else -> (parts[0].first().toString() + parts[1].first().toString()).uppercase()
        }
    }
}
