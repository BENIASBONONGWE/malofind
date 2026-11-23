package com.example.malofind.data.mock

import com.example.malofind.data.model.Property

object MockData {

    val properties = listOf(

        Property(
            id = "property_001",
            title = "Modern Family House",
            description = "A spacious three-bedroom family house in a quiet residential area.",
            location = "Matawale",
            city = "Zomba",
            propertyType = "House",
            listingType = "For Rent",
            price = 250000.0,
            bedrooms = 3,
            bathrooms = 2,
            imageUrl = "",
            ownerId = "owner_001",
            ownerName = "John Banda",
            ownerPhone = "+265 888 000 001",
            ownerEmail = "john@example.com",
            latitude = -15.3850,
            longitude = 35.3188,
            isApproved = true,
            isAvailable = true
        ),

        Property(
            id = "property_002",
            title = "Comfortable City Apartment",
            description = "A modern apartment suitable for students, professionals or a small family.",
            location = "Area 47",
            city = "Lilongwe",
            propertyType = "Apartment",
            listingType = "For Rent",
            price = 350000.0,
            bedrooms = 2,
            bathrooms = 2,
            imageUrl = "",
            ownerId = "owner_002",
            ownerName = "Mary Phiri",
            ownerPhone = "+265 888 000 002",
            ownerEmail = "mary@example.com",
            latitude = -13.9626,
            longitude = 33.7741,
            isApproved = true,
            isAvailable = true
        ),

        Property(
            id = "property_003",
            title = "Spacious Executive House",
            description = "A large four-bedroom property with a spacious compound and parking area.",
            location = "Nyambadwe",
            city = "Blantyre",
            propertyType = "House",
            listingType = "For Sale",
            price = 85000000.0,
            bedrooms = 4,
            bathrooms = 3,
            imageUrl = "",
            ownerId = "owner_003",
            ownerName = "Peter Phiri",
            ownerPhone = "+265 888 000 003",
            ownerEmail = "peter@example.com",
            latitude = -15.7861,
            longitude = 35.0058,
            isApproved = true,
            isAvailable = true
        ),

        Property(
            id = "property_004",
            title = "Affordable Student Room",
            description = "A clean and affordable room suitable for students and young professionals.",
            location = "Chinamwali",
            city = "Zomba",
            propertyType = "Room",
            listingType = "For Rent",
            price = 80000.0,
            bedrooms = 1,
            bathrooms = 1,
            imageUrl = "",
            ownerId = "owner_004",
            ownerName = "Grace Mbewe",
            ownerPhone = "+265 888 000 004",
            ownerEmail = "grace@example.com",
            latitude = -15.3889,
            longitude = 35.3186,
            isApproved = true,
            isAvailable = true
        ),

        Property(
            id = "property_005",
            title = "Modern Apartment",
            description = "A well-finished apartment located close to shops, schools and public transport.",
            location = "Katawa",
            city = "Mzuzu",
            propertyType = "Apartment",
            listingType = "For Sale",
            price = 45000000.0,
            bedrooms = 3,
            bathrooms = 2,
            imageUrl = "",
            ownerId = "owner_005",
            ownerName = "David Gondwe",
            ownerPhone = "+265 888 000 005",
            ownerEmail = "david@example.com",
            latitude = -11.4583,
            longitude = 34.0151,
            isApproved = true,
            isAvailable = true
        ),

        Property(
            id = "property_006",
            title = "Three Bedroom House",
            description = "A comfortable house with a secure yard and convenient access to town.",
            location = "Area 25",
            city = "Lilongwe",
            propertyType = "House",
            listingType = "For Rent",
            price = 300000.0,
            bedrooms = 3,
            bathrooms = 2,
            imageUrl = "",
            ownerId = "owner_006",
            ownerName = "Agnes Tembo",
            ownerPhone = "+265 888 000 006",
            ownerEmail = "agnes@example.com",
            latitude = -13.9319,
            longitude = 33.7876,
            isApproved = true,
            isAvailable = true
        )
    )
}