
package com.example.malofind.data.model

data class Property(
    val id: String = "",
    val title: String = "",
    val description: String = "",

    // Property location
    val location: String = "",
    val city: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Property details
    val propertyType: String = "",
    val listingType: String = "",
    val price: Double = 0.0,
    val currency: String = "MWK",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,

    // Property images
    val imageUrl: String = "",
    val imageUrls: List<String> = emptyList(),

    // Owner information
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val ownerEmail: String = "",

    // Listing management
    val isApproved: Boolean = false,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
