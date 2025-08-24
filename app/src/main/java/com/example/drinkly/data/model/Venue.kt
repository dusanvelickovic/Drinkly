package com.example.drinkly.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class Venue(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("address") @set:PropertyName("address")
    var address: String = "",

    @get:PropertyName("location") @set:PropertyName("location")
    var location: GeoPoint? = null,

    @get:PropertyName("type") @set:PropertyName("type")
    var type: String = "",

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Double = 0.0,
) {
    // Potreban prazan konstruktor za Firestore
    constructor() : this("", "", "", null, "", 0.0)
}