package com.example.drinkly.data.model

import com.google.firebase.Timestamp
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

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var created_at: Timestamp = Timestamp.now(),
) {
    val first_name: String?
        get() = name?.trim()?.split("\\s+".toRegex())?.firstOrNull()?.takeIf { it.isNotBlank() }
}
