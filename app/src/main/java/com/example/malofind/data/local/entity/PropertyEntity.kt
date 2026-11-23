package com.example.malofind.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.malofind.data.model.Property

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val propertyType: String,
    val listingType: String,
    val price: Double,
    val currency: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val imageUrl: String,
    val imageUrlsJson: String,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val isApproved: Boolean,
    val isAvailable: Boolean,
    val createdAt: Long
) {
    fun toProperty(): Property {
        val urls = if (imageUrlsJson.isNotBlank()) imageUrlsJson.split(",") else emptyList()
        return Property(
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
            imageUrls = urls,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerPhone = ownerPhone,
            ownerEmail = ownerEmail,
            isApproved = isApproved,
            isAvailable = isAvailable,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromProperty(property: Property): PropertyEntity {
            return PropertyEntity(
                id = property.id,
                title = property.title,
                description = property.description,
                location = property.location,
                city = property.city,
                latitude = property.latitude,
                longitude = property.longitude,
                propertyType = property.propertyType,
                listingType = property.listingType,
                price = property.price,
                currency = property.currency,
                bedrooms = property.bedrooms,
                bathrooms = property.bathrooms,
                imageUrl = property.imageUrl,
                imageUrlsJson = property.imageUrls.joinToString(","),
                ownerId = property.ownerId,
                ownerName = property.ownerName,
                ownerPhone = property.ownerPhone,
                ownerEmail = property.ownerEmail,
                isApproved = property.isApproved,
                isAvailable = property.isAvailable,
                createdAt = property.createdAt
            )
        }
    }
}
