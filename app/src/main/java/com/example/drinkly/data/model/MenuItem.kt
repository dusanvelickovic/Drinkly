package com.example.drinkly.data.model

import android.annotation.SuppressLint
import com.google.firebase.firestore.PropertyName

data class MenuItem(
    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = "",

    @get:PropertyName("price") @set:PropertyName("price")
    var price: Double,

    @get:PropertyName("currency") @set:PropertyName("currency")
    var currency: String = "",

    @get:PropertyName("available") @set:PropertyName("available")
    var available: Boolean,

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",
) {
    constructor() : this("", "", "", 0.0, "", false, "")

    @SuppressLint("DefaultLocale")
    fun getPriceFormatted(): String {
        return String.format("%d %s", price.toInt(), currency)
    }
}