
package com.meridian.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun register(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user
                ?: return Result.failure(
                    Exception("Registration failed: user not found")
                )

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = result.user
                ?: return Result.failure(
                    Exception("Login failed: user not found")
                )

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
