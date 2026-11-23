package com.example.malofind.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.malofind.data.local.MaloFindDatabase
import com.example.malofind.data.local.entity.PropertyEntity
import com.example.malofind.data.model.Property
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PropertySyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("properties").get().await()
            val properties = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: ""
                val description = doc.getString("description") ?: ""
                val location = doc.getString("location") ?: ""
                val city = doc.getString("city") ?: ""
                val latitude = doc.getDouble("latitude") ?: 0.0
                val longitude = doc.getDouble("longitude") ?: 0.0
                val propertyType = doc.getString("propertyType") ?: ""
                val listingType = doc.getString("listingType") ?: ""
                val price = doc.getDouble("price") ?: 0.0
                val currency = doc.getString("currency") ?: "MWK"
                val bedrooms = doc.getLong("bedrooms")?.toInt() ?: 0
                val bathrooms = doc.getLong("bathrooms")?.toInt() ?: 0
                val imageUrl = doc.getString("imageUrl") ?: ""
                val imageUrls = (doc.get("imageUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                val ownerId = doc.getString("ownerId") ?: ""
                val ownerName = doc.getString("ownerName") ?: ""
                val ownerPhone = doc.getString("ownerPhone") ?: ""
                val ownerEmail = doc.getString("ownerEmail") ?: ""
                val isApproved = doc.getBoolean("isApproved") ?: false
                val isAvailable = doc.getBoolean("isAvailable") ?: true
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                Property(
                    id = id,
                    title = title,
                    description = description,
                    location = location,
                    city = city,
                    latitude = latitude,
                    longitude = longitude,
                    propertyType = propertyType,
                    listingType = listingType,
                    price = price,
                    currency = currency,
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    imageUrl = imageUrl,
                    imageUrls = imageUrls,
                    ownerId = ownerId,
                    ownerName = ownerName,
                    ownerPhone = ownerPhone,
                    ownerEmail = ownerEmail,
                    isApproved = isApproved,
                    isAvailable = isAvailable,
                    createdAt = createdAt
                )
            }

            val database = MaloFindDatabase.getDatabase(applicationContext)
            database.propertyDao().clearProperties()
            database.propertyDao().insertProperties(properties.map { PropertyEntity.fromProperty(it) })

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
