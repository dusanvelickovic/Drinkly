package com.example.drinkly.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Review (
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("comment") @set:PropertyName("comment")
    var comment: String = "",

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Int = 0,

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Timestamp = Timestamp.now(),
) {
    constructor() : this("", "", "", 0, Timestamp.now())
}