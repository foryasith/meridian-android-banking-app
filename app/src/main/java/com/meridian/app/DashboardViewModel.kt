package com.meridian.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUIState(
            balance = "LKR 125,000.00",
            greeting = getGreeting()
        )
    )

    val uiState: StateFlow<DashboardUIState> =
        _uiState.asStateFlow()

    /*
     * Optional Challenge:
     * Generate a greeting based on the current time.
     */
    private fun getGreeting(): String {

        val hour =
            Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY)

        return when (hour) {

            in 5..11 ->
                "Good morning"

            in 12..16 ->
                "Good afternoon"

            else ->
                "Good evening"
        }
    }
}