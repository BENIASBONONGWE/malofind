package com.example.malofind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val accountType: String = "", // "SEEKER", "OWNER", "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<UserProfile> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Please enter your email address and password.")
            )
        }

        val firebaseUser = try {
            withTimeoutOrNull(12000L) {
                val authResult = auth
                    .signInWithEmailAndPassword(trimmedEmail, password)
                    .await()
                authResult.user
            } ?: return Result.failure(Exception("Connection timeout. Please check your emulator or network internet connection."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return Result.failure(Exception("Incorrect password or email format. Please check your credentials."))
        } catch (e: FirebaseAuthInvalidUserException) {
            return Result.failure(Exception("No account found matching this email address. Please create an account."))
        } catch (e: FirebaseNetworkException) {
            return Result.failure(Exception("Network error. Please check your emulator Wi-Fi or internet connection."))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Sign in failed. Please check your credentials."
            return Result.failure(Exception(msg))
        }

        return try {
            withTimeoutOrNull(8000L) {
                val document = firestore
                    .collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                if (document.exists()) {
                    var role = (document.getString("accountType")
                        ?: document.getString("role") ?: "").uppercase()

                    if (role.isBlank()) {
                        val propQuery = firestore.collection("properties").whereEqualTo("ownerId", firebaseUser.uid).get().await()
                        role = if (!propQuery.isEmpty) "OWNER" else "SEEKER"
                    }

                    UserProfile(
                        uid = firebaseUser.uid,
                        fullName = document.getString("fullName") ?: "",
                        email = document.getString("email") ?: firebaseUser.email ?: "",
                        phone = document.getString("phone")
                            ?: document.getString("phoneNumber") ?: "",
                        accountType = role,
                        createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } else {
                    val propQuery = firestore.collection("properties").whereEqualTo("ownerId", firebaseUser.uid).get().await()
                    val role = if (!propQuery.isEmpty) "OWNER" else "SEEKER"
                    UserProfile(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        accountType = role
                    )
                }
            }?.let { Result.success(it) } ?: Result.success(
                UserProfile(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    accountType = "SEEKER"
                )
            )
        } catch (e: Exception) {
            Result.success(
                UserProfile(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    accountType = "SEEKER"
                )
            )
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        accountType: String
    ): Result<UserProfile> {
        val trimmedEmail = email.trim()
        val trimmedName = fullName.trim()
        val trimmedPhone = phone.trim()
        val normalizedRole = accountType.uppercase().trim()

        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (trimmedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your email address."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val firebaseUser = try {
            withTimeoutOrNull(12000L) {
                val authResult = auth
                    .createUserWithEmailAndPassword(trimmedEmail, password)
                    .await()
                authResult.user
            } ?: return Result.failure(Exception("Connection timeout during registration. Please check your network."))
        } catch (e: FirebaseAuthUserCollisionException) {
            return Result.failure(Exception("An account with this email address already exists. Please sign in instead."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return Result.failure(Exception("Invalid email format or password."))
        } catch (e: FirebaseNetworkException) {
            return Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Registration failed. Please try again."
            return Result.failure(Exception(msg))
        }

        val profile = UserProfile(
            uid = firebaseUser.uid,
            fullName = trimmedName,
            email = trimmedEmail,
            phone = trimmedPhone,
            accountType = normalizedRole,
            createdAt = System.currentTimeMillis()
        )

        try {
            withTimeoutOrNull(6000L) {
                val userData = hashMapOf(
                    "uid" to profile.uid,
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                    "phone" to profile.phone,
                    "accountType" to profile.accountType,
                    "createdAt" to profile.createdAt
                )

                firestore
                    .collection("users")
                    .document(firebaseUser.uid)
                    .set(userData)
                    .await()
            }
        } catch (e: Exception) {
            // Ignore
        }

        return Result.success(profile)
    }

    suspend fun setUserRole(
        uid: String,
        accountType: String
    ): Result<Boolean> {
        return try {
            val normalizedRole = accountType.uppercase().trim()
            withTimeoutOrNull(6000L) {
                firestore
                    .collection("users")
                    .document(uid)
                    .update("accountType", normalizedRole)
                    .await()
            }
            Result.success(true)
        } catch (exception: Exception) {
            try {
                withTimeoutOrNull(6000L) {
                    firestore
                        .collection("users")
                        .document(uid)
                        .set(mapOf("accountType" to accountType.uppercase().trim()), SetOptions.merge())
                        .await()
                }
                Result.success(true)
            } catch (e: Exception) {
                Result.success(true)
            }
        }
    }

    suspend fun getCurrentUserProfile(): Result<UserProfile> {
        val currentUser = auth.currentUser
            ?: return Result.failure(Exception("No user currently signed in."))

        return try {
            val profile = withTimeoutOrNull(6000L) {
                val document = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (document.exists()) {
                    var role = (document.getString("accountType")
                        ?: document.getString("role") ?: "").uppercase()

                    if (role.isBlank()) {
                        val propQuery = firestore.collection("properties").whereEqualTo("ownerId", currentUser.uid).get().await()
                        role = if (!propQuery.isEmpty) "OWNER" else "SEEKER"
                    }

                    UserProfile(
                        uid = currentUser.uid,
                        fullName = document.getString("fullName") ?: "",
                        email = document.getString("email") ?: currentUser.email ?: "",
                        phone = document.getString("phone")
                            ?: document.getString("phoneNumber") ?: "",
                        accountType = role,
                        createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } else {
                    UserProfile(
                        uid = currentUser.uid,
                        email = currentUser.email ?: "",
                        accountType = "SEEKER"
                    )
                }
            } ?: UserProfile(uid = currentUser.uid, email = currentUser.email ?: "", accountType = "SEEKER")
            Result.success(profile)
        } catch (exception: Exception) {
            val profile = UserProfile(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                accountType = "SEEKER"
            )
            Result.success(profile)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            val trimmedEmail = email.trim()
            if (trimmedEmail.isBlank()) {
                return Result.failure(IllegalArgumentException("Please enter your email address."))
            }
            withTimeoutOrNull(8000L) {
                auth.sendPasswordResetEmail(trimmedEmail).await()
            } ?: return Result.failure(Exception("Connection timeout. Please check your network."))
            Result.success(true)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("No account found matching this email address.") )
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception("Network error. Please check your internet connection."))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
