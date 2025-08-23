package com.example.drinkly.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkly.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.register(email, password)
            _registerSuccess.value = result.isSuccess
            _registerState.value = if (result.isSuccess) {
                "Success"
            } else {
                result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }
}