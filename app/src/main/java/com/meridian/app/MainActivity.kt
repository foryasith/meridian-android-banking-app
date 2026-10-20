
package com.meridian.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "banking_app_prefs"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        applySavedTheme()

        super.onCreate(savedInstanceState)

        // Prevent opening Dashboard without a Firebase session.
        if (firebaseAuth.currentUser == null) {
            openLoginActivity()
            return
        }

        setContentView(R.layout.activity_main)

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

    override fun onStart() {
        super.onStart()

        // Check the session whenever MainActivity becomes visible.
        if (firebaseAuth.currentUser == null) {
            openLoginActivity()
        }
    }

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

    fun showDashboardFragment() {

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

    fun showConfirmationFragment(
        request: TransferRequest
    ) {

        val confirmationFragment = ConfirmationFragment()

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

    fun signOutAndOpenLogin() {

        // End the Firebase Authentication session.
        firebaseAuth.signOut()

        // Stop the existing session reminder service.
        stopService(
            Intent(this, SessionReminderService::class.java)
        )

        openLoginActivity()
    }

    private fun openLoginActivity() {

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}
