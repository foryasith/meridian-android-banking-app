package com.meridian.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "banking_app_prefs"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        /*
         * Lab 06 Optional Challenge:
         * Restore the saved theme before the Activity UI is created.
         */
        applySavedTheme()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /*
         * Show DashboardFragment only when MainActivity
         * is first created.
         */
        if (savedInstanceState == null) {

            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(
                    R.id.fragmentContainer,
                    DashboardFragment()
                )
                .commit()
        }
    }

    // =========================================================
    // THEME
    // =========================================================

    private fun applySavedTheme() {

        val prefs = getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val darkModeEnabled = prefs.getBoolean(
            KEY_DARK_MODE,
            false
        )

        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    // =========================================================
    // DASHBOARD NAVIGATION
    // =========================================================

    fun showDashboardFragment() {

        /*
         * Clear Transfer / Confirmation / History
         * entries from the back stack when returning home.
         */
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragmentContainer,
                DashboardFragment()
            )
            .commit()
    }

    // =========================================================
    // TRANSFER NAVIGATION
    // =========================================================

    fun showTransferFragment() {

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragmentContainer,
                TransferFragment()
            )
            .addToBackStack(null)
            .commit()
    }

    // =========================================================
    // HISTORY NAVIGATION
    // =========================================================

    fun showHistoryFragment() {

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragmentContainer,
                HistoryFragment()
            )
            .addToBackStack(null)
            .commit()
    }

    // =========================================================
    // CONFIRMATION NAVIGATION
    // =========================================================

    fun showConfirmationFragment(
        request: TransferRequest
    ) {

        val confirmationFragment =
            ConfirmationFragment()

        val bundle = Bundle().apply {

            putSerializable(
                ConfirmationFragment.ARG_TRANSFER_REQUEST,
                request
            )
        }

        confirmationFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragmentContainer,
                confirmationFragment
            )
            .addToBackStack(null)
            .commit()
    }
}