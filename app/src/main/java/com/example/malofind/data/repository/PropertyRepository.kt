package com.example.malofind.data.repository

import com.example.malofind.MaloFindApplication
import com.example.malofind.data.local.MaloFindDatabase
import com.example.malofind.data.local.entity.PropertyEntity
import com.example.malofind.data.model.Property
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PropertyRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val propertiesCollection = firestore.collection("properties")

    private val propertyDao by lazy {
        try {
            MaloFindDatabase.getDatabase(MaloFindApplication.instance).propertyDao()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Save a new property to Firestore and local Room cache.
     */
    suspend fun addProperty(property: Property): String = withContext(Dispatchers.IO) {
        val document = if (property.id.isNotBlank()) {
            propertiesCollection.document(property.id)
        } else {
            propertiesCollection.document()
        }

        val propertyToSave = property.copy(
            id = document.id,
            createdAt = if (property.createdAt > 0) property.createdAt else System.currentTimeMillis()
        )

        document.set(propertyToSave.toFirestoreMap()).await()
        propertyDao?.insertProperty(PropertyEntity.fromProperty(propertyToSave))
        document.id
    }

    /**
     * Retrieve approved properties (Offline-First: Network -> Room Cache fallback).
     */
    suspend fun getApprovedProperties(): List<Property> = withContext(Dispatchers.IO) {
        try {
            val snapshot = propertiesCollection.get().await()
            val properties = snapshot.documents
                .mapNotNull { it.toProperty() }
                .filter { it.isApproved && it.isAvailable }
                .sortedByDescending { it.createdAt }

            // Cache locally in Room
            propertyDao?.insertProperties(properties.map { PropertyEntity.fromProperty(it) })
            properties
        } catch (e: Exception) {
            // Fallback to local Room SQLite cache when offline
            propertyDao?.getApprovedProperties()?.map { it.toProperty() } ?: emptyList()
        }
    }

    /**
     * Retrieve all properties for Admin management (Offline-First: Network -> Room Cache fallback).
     */
    suspend fun getAllProperties(): List<Property> = withContext(Dispatchers.IO) {
        try {
            val snapshot = propertiesCollection.get().await()
            val properties = snapshot.documents
                .mapNotNull { it.toProperty() }
                .sortedByDescending { it.createdAt }

            propertyDao?.insertProperties(properties.map { PropertyEntity.fromProperty(it) })
            properties
        } catch (e: Exception) {
            propertyDao?.getAllProperties()?.map { it.toProperty() } ?: emptyList()
        }
    }

    /**
     * Retrieve pending properties (Offline-First: Network -> Room Cache fallback).
     */
    suspend fun getPendingProperties(): List<Property> = withContext(Dispatchers.IO) {
        try {
            val snapshot = propertiesCollection.get().await()
            val properties = snapshot.documents
                .mapNotNull { it.toProperty() }
                .filter { !it.isApproved }
                .sortedByDescending { it.createdAt }

            propertyDao?.insertProperties(properties.map { PropertyEntity.fromProperty(it) })
            properties
        } catch (e: Exception) {
            propertyDao?.getPendingProperties()?.map { it.toProperty() } ?: emptyList()
        }
    }

    /**
     * Approve a property listing in Firestore and update local Room cache.
     */
    suspend fun approveProperty(propertyId: String) = withContext(Dispatchers.IO) {
        if (propertyId.isBlank()) return@withContext
        try {
            propertiesCollection.document(propertyId).update("isApproved", true).await()
            // Update local room cache
            val existing = propertyDao?.getAllProperties()?.find { it.id == propertyId }
            if (existing != null) {
                propertyDao?.insertProperty(existing.copy(isApproved = true))
            }
        } catch (e: Exception) {
            // Offline write queuing handled by Firestore, but we can also update local room optimistically
            val existing = propertyDao?.getAllProperties()?.find { it.id == propertyId }
            if (existing != null) {
                propertyDao?.insertProperty(existing.copy(isApproved = true))
            }
        }
    }

    /**
     * Delete a property listing from Firestore and local Room cache.
     */
    suspend fun deleteProperty(propertyId: String) = withContext(Dispatchers.IO) {
        if (propertyId.isBlank()) return@withContext
        try {
            propertiesCollection.document(propertyId).delete().await()
            propertyDao?.deleteProperty(propertyId)
        } catch (e: Exception) {
            propertyDao?.deleteProperty(propertyId)
        }
    }

    /**
     * Retrieve one property by ID (Network -> Room Cache fallback).
     */
    suspend fun getPropertyById(propertyId: String): Property? = withContext(Dispatchers.IO) {
        if (propertyId.isBlank()) return@withContext null
        try {
            val document = propertiesCollection.document(propertyId).get().await()
            val property = document.toProperty()
            if (property != null) {
                propertyDao?.insertProperty(PropertyEntity.fromProperty(property))
            }
            property
        } catch (e: Exception) {
            propertyDao?.getAllProperties()?.find { it.id == propertyId }?.toProperty()
        }
    }

    /**
     * Retrieve properties belonging to an owner (Network -> Room Cache fallback).
     */
    suspend fun getPropertiesByOwner(ownerId: String): List<Property> = withContext(Dispatchers.IO) {
        if (ownerId.isBlank()) return@withContext emptyList()
        try {
            val snapshot = propertiesCollection.get().await()
            val properties = snapshot.documents
                .mapNotNull { it.toProperty() }
                .filter { it.ownerId == ownerId }
                .sortedByDescending { it.createdAt }

            propertyDao?.insertProperties(properties.map { PropertyEntity.fromProperty(it) })
            properties
        } catch (e: Exception) {
            propertyDao?.getAllProperties()
                ?.map { it.toProperty() }
                ?.filter { it.ownerId == ownerId }
                ?: emptyList()
        }
    }

    /**
     * Update an existing property in Firestore and local Room cache.
     */
    suspend fun updateProperty(property: Property) = withContext(Dispatchers.IO) {
        require(property.id.isNotBlank()) { "Property ID is required to update a property." }
        val updates = property.toFirestoreMap().toMutableMap()
        updates.remove("createdAt")
        propertiesCollection.document(property.id).update(updates).await()
        propertyDao?.insertProperty(PropertyEntity.fromProperty(property))
    }

    /**
     * Convert the Property object into a Firestore map.
     */
    private fun Property.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "location" to location,
            "city" to city,
            "latitude" to latitude,
            "longitude" to longitude,
            "propertyType" to propertyType,
            "listingType" to listingType,
            "price" to price,
            "currency" to currency,
            "bedrooms" to bedrooms,
            "bathrooms" to bathrooms,
            "imageUrl" to imageUrl,
            "imageUrls" to imageUrls,
            "ownerId" to ownerId,
            "ownerName" to ownerName,
            "ownerPhone" to ownerPhone,
            "ownerEmail" to ownerEmail,
            "isApproved" to isApproved,
            "isAvailable" to isAvailable,
            "createdAt" to createdAt
        )
    }

    /**
     * Convert a Firestore document into a Property object.
     */
    private fun DocumentSnapshot.toProperty(): Property? {
        if (!exists()) return null

        return Property(
            id = getString("id") ?: this.id,
            title = getString("title") ?: "",
            description = getString("description") ?: "",
            location = getString("location") ?: "",
            city = getString("city") ?: "",
            latitude = getDouble("latitude") ?: 0.0,
            longitude = getDouble("longitude") ?: 0.0,
            propertyType = getString("propertyType") ?: "",
            listingType = getString("listingType") ?: "",
            price = getDouble("price")
                ?: getLong("price")?.toDouble()
                ?: 0.0,
            currency = getString("currency") ?: "MWK",
            bedrooms = getLong("bedrooms")?.toInt() ?: 0,
            bathrooms = getLong("bathrooms")?.toInt() ?: 0,
            imageUrl = getString("imageUrl") ?: "",
            imageUrls = get("imageUrls")
                .let { value ->
                    (value as? List<*>)
                        ?.filterIsInstance<String>()
                        ?: emptyList()
                },
            ownerId = getString("ownerId") ?: "",
            ownerName = getString("ownerName") ?: "",
            ownerPhone = getString("ownerPhone") ?: "",
            ownerEmail = getString("ownerEmail") ?: "",
            isApproved = getBoolean("isApproved") ?: false,
            isAvailable = getBoolean("isAvailable") ?: true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }
}
