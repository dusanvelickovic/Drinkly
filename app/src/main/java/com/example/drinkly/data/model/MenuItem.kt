package com.example.drinkly.data.model

import com.google.firebase.firestore.PropertyName

data class MenuItem(
    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = "",

    @get:PropertyName("price") @set:PropertyName("price")
    var price: String = "",

    @get:PropertyName("currency") @set:PropertyName("currency")
    var currency: String = "",

    @get:PropertyName("available") @set:PropertyName("available")
    var available: Boolean,
) {
    constructor() : this("", "", "", "", "", false)
}