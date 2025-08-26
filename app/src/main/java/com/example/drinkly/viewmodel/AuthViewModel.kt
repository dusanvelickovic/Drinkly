package com.example.drinkly.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drinkly.data.model.User
import com.example.drinkly.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    public val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun checkAuth(repo: AuthRepository) {
        _isAuthenticated.value = repo.checkAuth()
    }

    suspend fun getAuthUser(): Result<User?> {
        return authRepository.getAuthUser()
    }

    suspend fun updateUser(name: String, email: String, phone: String, bio: String): Result<Unit> {
        return authRepository.updateUser(name, email, phone, bio)
    }

    fun logout() {
        authRepository.logout()
        _isAuthenticated.value = false
    }
}