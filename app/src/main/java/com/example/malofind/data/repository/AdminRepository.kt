
package com.example.malofind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    suspend fun loginAdmin(
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(
                    IllegalArgumentException(
                        "Please enter your email and password."
                    )
                )
            }

            val authResult = auth
                .signInWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()

            val firebaseUser = authResult.user
                ?: throw Exception("Unable to sign in.")

            val adminDocument = firestore
                .collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val accountType = adminDocument.getString("accountType")
                ?: adminDocument.getString("role")

            if (!accountType.equals("ADMIN", ignoreCase = true)) {
                auth.signOut()

                throw SecurityException(
                    "Access denied. This account is not an administrator."
                )
            }

            Result.success(true)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    fun logoutAdmin() {
        auth.signOut()
    }

    fun isAdminLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun getAdminName(): Result<String> {
        return try {
            val user = auth.currentUser
                ?: throw Exception("No administrator is signed in.")

            val document = firestore
                .collection("users")
                .document(user.uid)
                .get()
                .await()

            val accountType = document.getString("accountType")
                ?: document.getString("role")

            if (!accountType.equals("ADMIN", ignoreCase = true)) {
                auth.signOut()
                throw SecurityException(
                    "This account is not authorized as an administrator."
                )
            }

            val name = document.getString("fullName")
                ?.takeIf { it.isNotBlank() }
                ?: user.email
                ?: "Administrator"

            Result.success(name)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}