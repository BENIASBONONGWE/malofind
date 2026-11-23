package com.example.malofind.data.model

data class PropertyRequest(
    val id: String = "",
    val propertyId: String = "",
    val propertyTitle: String = "",
    val seekerId: String = "",
    val seekerName: String = "",
    val seekerPhone: String = "",
    val seekerEmail: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val requestType: String = "",
    val message: String = "",
    val status: String = "Pending",
    val paymentStatus: String = "Not Required",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)