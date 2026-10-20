
package com.meridian.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var cryptoManager: BiometricCryptoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cryptoManager = BiometricCryptoManager()

        setupBiometricAuthentication()

        setContent {
            val uiState by loginViewModel.uiState
                .collectAsStateWithLifecycle()

            MaterialTheme {
                LoginScreen(
                    uiState = uiState,
                    onEmailChange = loginViewModel::onEmailChange,
                    onPasswordChange = loginViewModel::onPasswordChange,
                    onSubmit = loginViewModel::submit,
                    onToggleMode = loginViewModel::toggleRegisterMode
                )
            }

            if (uiState.isAuthenticated) {
                androidx.compose.runtime.LaunchedEffect(
                    uiState.isAuthenticated
                ) {
                    openMainActivity()
                }
            }
        }

        // Only existing Firebase sessions can use biometrics.
        if (firebaseAuth.currentUser != null) {
            checkAuthenticationAvailability()
        }
    }

    private fun setupBiometricAuthentication() {

        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    // Firebase session must still exist.
                    if (firebaseAuth.currentUser != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        openMainActivity()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please sign in with your Firebase account",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    Toast.makeText(
                        this@LoginActivity,
                        "Use email and password to sign in",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        this@LoginActivity,
                        "Biometric not recognized. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sign in to Meridian")
            .setSubtitle("Verify your identity to continue")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()
    }

    private fun checkAuthenticationAvailability() {

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val biometricManager = BiometricManager.from(this)

        if (
            biometricManager.canAuthenticate(authenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            startAuthentication()
        } else {
            Toast.makeText(
                this,
                "Sign in using your email and password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startAuthentication() {

        try {
            val cipher = cryptoManager.getEncryptCipher()

            val cryptoObject = BiometricPrompt.CryptoObject(cipher)

            biometricPrompt.authenticate(
                promptInfo,
                cryptoObject
            )
        } catch (exception: Exception) {
            // Some devices do not support CryptoObject with
            // DEVICE_CREDENTIAL. Use the normal system prompt.
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
