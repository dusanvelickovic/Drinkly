package com.example.drinkly.ui.leaderboard

import com.example.drinkly.data.model.User
import com.example.drinkly.data.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository = LeaderboardRepository()
) {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    /**
     * Dobavi top korisnike sortirane po broju postavljenih ocena
     */
    suspend fun getTopUsers(limit: Int = 50) {
        val result = leaderboardRepository.getTopUsers(limit)
        if (result.isSuccess) {
            val usersData = result.getOrNull() ?: emptyList()
            _users.value = usersData.map { data ->
                User(
                    uid = data["uid"] as? String ?: "",
                    email = data["email"] as String,
                    name = data["name"] as? String ?: "Unknown",
                    phone = data["phone"] as? String ?: "Customer",
                    bio = data["bio"] as? String ?: "",
                    reviewsPosted = (data["reviews_posted"] as? Long)?.toInt() ?: 0
                )
            }
            println("Loaded ${_users.value.size} top users")
        } else {
            println("Failed to load top users: ${result.exceptionOrNull()?.message}")
            _users.value = emptyList()
        }
    }
}