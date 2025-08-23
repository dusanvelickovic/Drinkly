package com.example.drinkly.data.model

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
