package com.example.drinkly.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class Venue(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("phone") @set:PropertyName("phone")
    var phone: String = "",

    @get:PropertyName("address") @set:PropertyName("address")
    var address: String = "",

    @get:PropertyName("location") @set:PropertyName("location")
    var location: GeoPoint,

    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = "",

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Double = 0.0,

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",
) {
    // Potreban prazan konstruktor za Firestore
    constructor() : this("", "", "", "", GeoPoint(0.0,0.0), "", 0.0, "", "")

    // Mapira kategorije na prikazno ime
    fun getDisplayCategory(): String {
        return when (category.lowercase()) {
            "restaurant" -> "Restaurant"
            "caffe" -> "Caffe"
            "bar" -> "Bar"
            "pub" -> "Pub"
            "kafana" -> "Kafana"
            "fast_food" -> "Fast Food"
            else -> "Other"
        }
    }
}