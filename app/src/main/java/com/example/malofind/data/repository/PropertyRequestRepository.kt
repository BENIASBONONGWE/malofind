package com.example.malofind.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class PropertyRequest(
    val id: String = "",
    val propertyId: String = "",
    val propertyTitle: String = "",
    val propertyLocation: String = "",
    val seekerId: String = "",
    val seekerName: String = "",
    val seekerEmail: String = "",
    val seekerPhone: String = "",
    val ownerId: String = "",
    val moveInDate: String = "",
    val occupants: Int = 1,
    val notes: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val createdAt: Long = System.currentTimeMillis()
)

class PropertyRequestRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val requestsCollection = firestore.collection("requests")

    suspend fun addRequest(request: PropertyRequest): String {
        val doc = requestsCollection.document()
        val requestToSave = request.copy(
            id = doc.id,
            createdAt = System.currentTimeMillis()
        )
        doc.set(requestToSave.toMap()).await()
        return doc.id
    }

    suspend fun getRequestsBySeeker(seekerId: String): List<PropertyRequest> {
        if (seekerId.isBlank()) return emptyList()
        return try {
            val snapshot = requestsCollection.get().await()
            snapshot.documents
                .mapNotNull { it.toPropertyRequest() }
                .filter { it.seekerId == seekerId }
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRequestsByOwner(ownerId: String): List<PropertyRequest> {
        if (ownerId.isBlank()) return emptyList()
        return try {
            val snapshot = requestsCollection.get().await()
            snapshot.documents
                .mapNotNull { it.toPropertyRequest() }
                .filter { it.ownerId == ownerId }
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllRequests(): List<PropertyRequest> {
        return try {
            val snapshot = requestsCollection.get().await()
            snapshot.documents
                .mapNotNull { it.toPropertyRequest() }
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateRequestStatus(requestId: String, status: String) {
        if (requestId.isBlank()) return
        try {
            requestsCollection.document(requestId).update("status", status).await()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun PropertyRequest.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "propertyId" to propertyId,
            "propertyTitle" to propertyTitle,
            "propertyLocation" to propertyLocation,
            "seekerId" to seekerId,
            "seekerName" to seekerName,
            "seekerEmail" to seekerEmail,
            "seekerPhone" to seekerPhone,
            "ownerId" to ownerId,
            "moveInDate" to moveInDate,
            "occupants" to occupants,
            "notes" to notes,
            "status" to status,
            "createdAt" to createdAt
        )
    }

    private fun DocumentSnapshot.toPropertyRequest(): PropertyRequest? {
        if (!exists()) return null
        return PropertyRequest(
            id = getString("id") ?: id,
            propertyId = getString("propertyId") ?: "",
            propertyTitle = getString("propertyTitle") ?: "",
            propertyLocation = getString("propertyLocation") ?: "",
            seekerId = getString("seekerId") ?: "",
            seekerName = getString("seekerName") ?: "",
            seekerEmail = getString("seekerEmail") ?: "",
            seekerPhone = getString("seekerPhone") ?: "",
            ownerId = getString("ownerId") ?: "",
            moveInDate = getString("moveInDate") ?: "",
            occupants = getLong("occupants")?.toInt() ?: 1,
            notes = getString("notes") ?: "",
            status = getString("status") ?: "Pending",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }
}
