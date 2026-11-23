package com.example.malofind.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AppUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "Seeker",
    val status: String = "Active",
    val propertyCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    suspend fun getAllUsers(): List<AppUser> {
        return try {
            val snapshot = usersCollection.get().await()
            snapshot.documents.mapNotNull { it.toAppUser() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllOwners(): List<AppUser> {
        return try {
            val snapshot = usersCollection.get().await()
            snapshot.documents
                .mapNotNull { it.toAppUser() }
                .filter { it.role.equals("OWNER", ignoreCase = true) || it.role.equals("Owner", ignoreCase = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateUserStatus(userId: String, status: String) {
        if (userId.isBlank()) return
        try {
            usersCollection.document(userId).update("status", status).await()
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun deleteUser(userId: String) {
        if (userId.isBlank()) return
        try {
            usersCollection.document(userId).delete().await()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun DocumentSnapshot.toAppUser(): AppUser? {
        if (!exists()) return null
        val rawRole = getString("accountType") ?: getString("role") ?: "Seeker"
        val formattedRole = when (rawRole.uppercase()) {
            "ADMIN" -> "Admin"
            "OWNER" -> "Owner"
            else -> "Seeker"
        }
        val userEmail = getString("email") ?: ""
        return AppUser(
            id = id,
            name = getString("fullName") ?: getString("name") ?: userEmail.ifBlank { "User" },
            email = userEmail,
            phone = getString("phone") ?: getString("phoneNumber") ?: "",
            role = formattedRole,
            status = getString("status") ?: "Active",
            propertyCount = getLong("propertyCount")?.toInt() ?: 0,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }
}
