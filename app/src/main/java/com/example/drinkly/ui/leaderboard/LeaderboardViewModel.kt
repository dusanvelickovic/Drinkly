package com.example.drinkly.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.model.User
import com.example.drinkly.data.repository.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository = LeaderboardRepository()
) : ViewModel() {
    private val _usersFlow = MutableStateFlow<Result<List<User>>>(Result.success(emptyList()))
    val usersFlow: StateFlow<Result<List<User>>> = _usersFlow

    /**
     * Dobavi top korisnike sortirane po broju postavljenih ocena
     */
    fun observeTopUsers(limit: Int = 50) {
        viewModelScope.launch {
            leaderboardRepository.observeTopUsers(limit).collect { result ->
                _usersFlow.value = result
            }
        }
    }
}