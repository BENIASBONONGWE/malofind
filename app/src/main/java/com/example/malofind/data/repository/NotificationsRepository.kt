package com.example.malofind.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "info", // "request", "success", "property", "info", "warning"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

class NotificationsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val notificationsCollection = firestore.collection("notifications")

    suspend fun getNotificationsForUser(userId: String): List<AppNotification> {
        if (userId.isBlank()) return emptyList()
        return try {
            val snapshot = notificationsCollection.get().await()
            snapshot.documents
                .mapNotNull { it.toAppNotification() }
                .filter { it.userId == userId || it.userId == "all" || it.userId.isBlank() }
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addNotification(notification: AppNotification): String {
        val doc = notificationsCollection.document()
        val toSave = notification.copy(
            id = doc.id,
            createdAt = System.currentTimeMillis()
        )
        doc.set(toSave.toMap()).await()
        return doc.id
    }

    private fun AppNotification.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "title" to title,
            "message" to message,
            "type" to type,
            "isRead" to isRead,
            "createdAt" to createdAt
        )
    }

    private fun DocumentSnapshot.toAppNotification(): AppNotification? {
        if (!exists()) return null
        return AppNotification(
            id = getString("id") ?: id,
            userId = getString("userId") ?: "",
            title = getString("title") ?: "",
            message = getString("message") ?: "",
            type = getString("type") ?: "info",
            isRead = getBoolean("isRead") ?: false,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }
}
