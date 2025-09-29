package com.example.drinkly.viewmodel

import android.net.Uri
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

    suspend fun updateUserProfileImage(selectedImageUri: Uri): Result<String> {
        val result = authRepository.updateUserProfileImage(selectedImageUri)

        if (result.isSuccess) {
            println("Profile image updated successfully: ${result.getOrNull()}")
            // Ako je ažuriranje slike uspešno, vrati URL slike
        } else {
            // Ako je došlo do greške, vrati grešku
            println("Error updating profile image: ${result.exceptionOrNull()?.message}")
        }

        return result
    }

    suspend fun removeUserProfileImage(): Result<String> {
        val result = authRepository.removeUserImage()

        if (result.isSuccess) {
            println("Profile image removed successfully")
            // Ako je uklanjanje slike uspešno, vrati poruku o uspehu
        } else {
            // Ako je došlo do greške, vrati grešku
            println("Error removing profile image: ${result.exceptionOrNull()?.message}")
        }

        return result
    }

    fun logout() {
        authRepository.logout()
        _isAuthenticated.value = false
    }
}