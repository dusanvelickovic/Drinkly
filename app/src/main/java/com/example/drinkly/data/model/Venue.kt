//package com.example.drinkly.data.model
//
//import com.google.firebase.firestore.GeoPoint
//import com.google.firebase.firestore.ServerTimestamp
//import java.util.Date
//
//data class Venue(
//    val venueId: String = "",           // Jedinstveni ID dokumenta u Firestore
//    val name: String = "",              // Naziv objekta
//    val address: String = "",           // Adresa
//    val location: GeoPoint? = null,     // GeoPoint (lat, lng)
//    val type: String = "",              // Tip objekta: kafić, bar, restoran
//    val averageRating: Double = 0.0,    // Prosečna ocena iz feedback-a
//    @ServerTimestamp
//    val createdAt: Date? = null         // Vreme kreiranja u Firestore
//)