
package com.meridian.app

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class DashboardViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(
        DashboardUIState(
            balance = "LKR 125,000.00",
            greeting = getGreeting(),
            userEmail = authRepository.getCurrentUser()?.email.orEmpty()
        )
    )

    val uiState: StateFlow<DashboardUIState> =
        _uiState.asStateFlow()

    fun refreshUser() {
        _uiState.value = _uiState.value.copy(
            userEmail = authRepository.getCurrentUser()?.email.orEmpty()
        )
    }

    fun signOut() {
        authRepository.signOut()

        _uiState.value = _uiState.value.copy(
            userEmail = ""
        )
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
