package com.example.malofind

import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.PropertyRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PropertyModelTest {

    @Test
    fun property_defaultValues_areCorrect() {
        val property = Property()
        assertEquals("", property.id)
        assertEquals("", property.title)
        assertEquals("", property.city)
        assertEquals("MWK", property.currency)
        assertEquals(0.0, property.price, 0.0)
        assertEquals(0, property.bedrooms)
        assertEquals(0, property.bathrooms)
        assertFalse(property.isApproved)
        assertTrue(property.isAvailable)
    }

    @Test
    fun property_customValues_areSavedCorrectly() {
        val property = Property(
            id = "prop_123",
            title = "Modern Villa",
            city = "Lilongwe",
            price = 450000.0,
            bedrooms = 4,
            bathrooms = 3,
            listingType = "For Rent",
            propertyType = "House",
            isApproved = true,
            isAvailable = true
        )

        assertEquals("prop_123", property.id)
        assertEquals("Modern Villa", property.title)
        assertEquals("Lilongwe", property.city)
        assertEquals(450000.0, property.price, 0.0)
        assertEquals(4, property.bedrooms)
        assertEquals(3, property.bathrooms)
        assertEquals("For Rent", property.listingType)
        assertEquals("House", property.propertyType)
        assertTrue(property.isApproved)
        assertTrue(property.isAvailable)
    }

    @Test
    fun propertyRequest_defaultValues_areCorrect() {
        val request = PropertyRequest()
        assertEquals("", request.id)
        assertEquals("", request.propertyId)
        assertEquals("Pending", request.status)
        assertEquals(1, request.occupants)
    }
}
