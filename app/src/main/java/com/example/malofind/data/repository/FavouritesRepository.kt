package com.example.malofind.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavouritesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getFavouritePropertyIds(userId: String): Set<String> {
        if (userId.isBlank()) return emptySet()
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("favourites")
                .get()
                .await()
            snapshot.documents.map { it.id }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    suspend fun toggleFavourite(userId: String, propertyId: String): Boolean {
        if (userId.isBlank() || propertyId.isBlank()) return false
        val docRef = firestore
            .collection("users")
            .document(userId)
            .collection("favourites")
            .document(propertyId)

        return try {
            val doc = docRef.get().await()
            if (doc.exists()) {
                docRef.delete().await()
                false
            } else {
                docRef.set(mapOf("propertyId" to propertyId, "createdAt" to System.currentTimeMillis())).await()
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
