
package com.meridian.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LoginScreen(
    uiState: LoginUIState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit
) {

    // Controls password visibility for Login and Register.
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Meridian Mobile Banking",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (uiState.isRegisterMode) {
                    "Create your account"
                } else {
                    "Sign in to your account"
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email Address") },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field with Show / Hide button
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                enabled = !uiState.isLoading,

                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),


                trailingIcon = {
                    TextButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },


                modifier = Modifier.fillMaxWidth()
            )

            // Firebase error message
            if (uiState.errorMessage != null) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login / Register button
            Button(
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (uiState.isRegisterMode) {
                            "Create Account"
                        } else {
                            "Sign In"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switch between Login and Register
            TextButton(
                onClick = {
                    passwordVisible = false
                    onToggleMode()
                },
                enabled = !uiState.isLoading
            ) {
                Text(
                    if (uiState.isRegisterMode) {
                        "Already have an account? Sign In"
                    } else {
                        "Don't have an account? Register"
                    }
                )
            }
        }
    }
}
