package com.example.drinkly.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Review (
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("user_uid") @set:PropertyName("user_uid")
    var userUid: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("comment") @set:PropertyName("comment")
    var comment: String = "",

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Int = 0,

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Timestamp = Timestamp.now(),

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String? = null,

    @get:PropertyName("user") @set:PropertyName("user")
    var user: Map<String, Any> = emptyMap()
) {
    constructor() : this("", "", "", "", 0, Timestamp.now(), null,)

    fun getDateFormatted(): String {
        val date = date.toDate()
        val day = date.date
        val month = date.month + 1 // Months are 0-based
        val year = date.year + 1900 // Years since 1900

        return String.format("%02d/%02d/%04d", day, month, year)
    }
}