
package com.meridian.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())

    val uiState: StateFlow<LoginUIState> =
        _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun toggleRegisterMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            errorMessage = null
        )
    }

    fun submit() {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Please enter your email"
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = currentState.copy(
                errorMessage = "Please enter a valid email address"
            )
            return
        }

        if (password.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Please enter your password"
            )
            return
        }

        if (currentState.isRegisterMode && password.length < 6) {
            _uiState.value = currentState.copy(
                errorMessage = "Password must contain at least 6 characters"
            )
            return
        }

        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val result = if (currentState.isRegisterMode) {
                authRepository.register(email, password)
            } else {
                authRepository.login(email, password)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        password = "",
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = exception.localizedMessage
                            ?: "Authentication failed"
                    )
                }
            )
        }
    }
}
