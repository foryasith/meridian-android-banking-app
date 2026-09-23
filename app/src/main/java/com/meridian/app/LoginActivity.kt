package com.meridian.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var biometricPrompt:
            BiometricPrompt

    private lateinit var promptInfo:
            BiometricPrompt.PromptInfo

    private lateinit var cryptoManager:
            BiometricCryptoManager


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_login
        )

        hidePasswordForm()

        /*
         * Challenge 1
         * Create the manager responsible for
         * Android Keystore and Cipher handling.
         */
        cryptoManager =
            BiometricCryptoManager()


        /*
         * Biometric callbacks must update the UI
         * on the main thread.
         */
        val executor =
            ContextCompat.getMainExecutor(this)


        biometricPrompt =
            BiometricPrompt(
                this,
                executor,

                object :
                    BiometricPrompt.AuthenticationCallback() {


                    /*
                     * AUTHENTICATION SUCCESS
                     */
                    override fun onAuthenticationSucceeded(
                        result:
                        BiometricPrompt.AuthenticationResult
                    ) {

                        super.onAuthenticationSucceeded(
                            result
                        )

                        /*
                         * Challenge 1:
                         *
                         * If biometric authentication was
                         * performed using CryptoObject,
                         * the authenticated Cipher can be
                         * obtained from the result.
                         */
                        val authenticatedCipher =
                            result.cryptoObject?.cipher


                        if (authenticatedCipher != null) {

                            Toast.makeText(
                                this@LoginActivity,
                                "Biometric authentication successful",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            /*
                             * Device credential authentication
                             * may not provide the same
                             * CryptoObject flow.
                             */
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                        openMainActivity()
                    }


                    /*
                     * AUTHENTICATION ERROR
                     *
                     * The authentication session has ended.
                     */
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {

                        super.onAuthenticationError(
                            errorCode,
                            errString
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            errString,
                            Toast.LENGTH_SHORT
                        ).show()


                        /*
                         * Keep the application's own
                         * username/password form available
                         * as an additional fallback.
                         */
                        showPasswordForm()
                    }


                    /*
                     * ONE FAILED BIOMETRIC ATTEMPT
                     */
                    override fun onAuthenticationFailed() {

                        super.onAuthenticationFailed()

                        Toast.makeText(
                            this@LoginActivity,
                            "Biometric not recognized. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        /*
                         * Do NOT reveal the password form here.
                         *
                         * The system prompt is still active
                         * and the user may retry.
                         */
                    }
                }
            )


        /*
         * CHALLENGE 2
         *
         * Accept either:
         *
         * 1. Strong biometric authentication
         * OR
         * 2. Device PIN / pattern / password
         */
        promptInfo =
            BiometricPrompt.PromptInfo
                .Builder()

                .setTitle(
                    "Sign in to Meridian"
                )

                .setSubtitle(
                    "Verify your identity to continue"
                )

                .setAllowedAuthenticators(
                    BiometricManager
                        .Authenticators
                        .BIOMETRIC_STRONG or

                            BiometricManager
                                .Authenticators
                                .DEVICE_CREDENTIAL
                )

                /*
                 * CHALLENGE 3
                 *
                 * Allows compatible passive biometric
                 * authentication to complete without an
                 * additional confirmation tap.
                 */
                .setConfirmationRequired(
                    false
                )
                .build()

        checkAuthenticationAvailability()
    }


    /*
     * CHECK AUTHENTICATION AVAILABILITY
     */

    private fun checkAuthenticationAvailability() {

        val biometricManager =
            BiometricManager.from(this)


        /*
         * Challenge 2:
         *
         * Check the exact same authenticator combination
         * that the prompt will accept.
         */
        val authenticators =
            BiometricManager
                .Authenticators
                .BIOMETRIC_STRONG or

                    BiometricManager
                        .Authenticators
                        .DEVICE_CREDENTIAL


        when (
            biometricManager.canAuthenticate(
                authenticators
            )
        ) {


            BiometricManager.BIOMETRIC_SUCCESS -> {

                startAuthentication()
            }


            BiometricManager
                .BIOMETRIC_ERROR_NONE_ENROLLED -> {

                Toast.makeText(
                    this,
                    "No biometric or device credential is enrolled.",
                    Toast.LENGTH_LONG
                ).show()

                showPasswordForm()
            }


            BiometricManager
                .BIOMETRIC_ERROR_NO_HARDWARE -> {

                Toast.makeText(
                    this,
                    "Biometric hardware is not available.",
                    Toast.LENGTH_LONG
                ).show()

                showPasswordForm()
            }


            BiometricManager
                .BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                Toast.makeText(
                    this,
                    "Biometric hardware is currently unavailable.",
                    Toast.LENGTH_LONG
                ).show()

                showPasswordForm()
            }


            else -> {

                showPasswordForm()
            }
        }
    }


    /*
     * START AUTHENTICATION
     */

    private fun startAuthentication() {

        try {

            /*
             * Challenge 1:
             *
             * Create a Cipher protected by
             * Android Keystore.
             */
            val cipher =
                cryptoManager
                    .getEncryptCipher()


            val cryptoObject =
                BiometricPrompt
                    .CryptoObject(
                        cipher
                    )


            /*
             * Connect authentication to the
             * cryptographic operation.
             */
            biometricPrompt.authenticate(
                promptInfo,
                cryptoObject
            )

        } catch (exception: Exception) {

            /*
             * Some device/API/authenticator combinations
             * may not support this exact CryptoObject +
             * credential configuration.
             *
             * The authentication feature must remain
             * usable, so fall back to the normal system
             * authentication prompt.
             */

            biometricPrompt.authenticate(
                promptInfo
            )
        }
    }


    /*
     * HIDE APP PASSWORD FORM
     */

    private fun hidePasswordForm() {

        findViewById<View>(
            R.id.passwordFormGroup
        ).visibility =
            View.GONE
    }

    /*
     * SHOW APP PASSWORD FORM
     */

    private fun showPasswordForm() {

        val passwordForm =
            findViewById<View>(
                R.id.passwordFormGroup
            )

        passwordForm.visibility =
            View.VISIBLE


        val btnLogin =
            findViewById<Button>(
                R.id.btnLogin
            )


        btnLogin.setOnClickListener {

            loginWithPassword()
        }
    }


    /*
     * APPLICATION PASSWORD FALLBACK
     */

    private fun loginWithPassword() {

        val etUsername =
            findViewById<EditText>(
                R.id.etUsername
            )


        val etPassword =
            findViewById<EditText>(
                R.id.etPassword
            )

        etUsername.error = null
        etPassword.error = null


        val username =
            etUsername
                .text
                .toString()
                .trim()


        val password =
            etPassword
                .text
                .toString()
                .trim()


        /*
         * Username validation.
         */
        if (username.isEmpty()) {

            etUsername.error =
                "Enter your username"

            etUsername.requestFocus()

            return
        }


        /*
         * Password validation.
         */
        if (password.isEmpty()) {

            etPassword.error =
                "Enter your password"

            etPassword.requestFocus()

            return
        }

        Toast.makeText(
            this,
            "Login successful",
            Toast.LENGTH_SHORT
        ).show()


        openMainActivity()
    }

    /*
     * OPEN DASHBOARD
     */

    private fun openMainActivity() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )


        startActivity(
            intent
        )

        finish()
    }
}