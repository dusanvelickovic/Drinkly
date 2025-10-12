package com.example.drinkly.data.enum

enum class VenueCategory {
    RESTAURANT,
    CAFFE,
    BAR,
    PUB,
    KAFANA,
    FAST_FOOD,
}

fun VenueCategory.getDisplayName(): String {
    return when (this) {
        VenueCategory.RESTAURANT -> "Restaurant"
        VenueCategory.CAFFE -> "Caffe"
        VenueCategory.BAR -> "Bar"
        VenueCategory.PUB -> "Pub"
        VenueCategory.KAFANA -> "Kafana"
        VenueCategory.FAST_FOOD -> "Fast Food"
    }
}

fun VenueCategory.getKey(): String {
    return when (this) {
        VenueCategory.RESTAURANT -> "restaurant"
        VenueCategory.CAFFE -> "caffe"
        VenueCategory.BAR -> "bar"
        VenueCategory.PUB -> "pub"
        VenueCategory.KAFANA -> "kafana"
        VenueCategory.FAST_FOOD -> "fast_food"
    }
}