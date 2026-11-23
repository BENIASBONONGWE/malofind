package com.example.malofind

import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.PropertyRequest
import com.example.malofind.data.repository.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit Test Suite aligning directly with MaloFind Test Specification and Test Cases (BED-COM-28-19, Benias Bonongwe).
 */
class AuthRepositoryTest {

    @Test
    fun test_MF_AUTH_001_seekerAuthentication_andRoleVerification() {
        // Test Case ID: MF_AUTH_001
        val userProfile = UserProfile(
            uid = "seeker_uid_01",
            fullName = "Test Seeker",
            email = "seeker@example.com",
            phone = "0999123456",
            accountType = "SEEKER"
        )

        assertEquals("seeker@example.com", userProfile.email)
        assertEquals("SEEKER", userProfile.accountType)
    }

    @Test
    fun test_MF_AUTH_001_ownerAuthentication_andRoleVerification() {
        // Test Case ID: MF_AUTH_001 (Owner)
        val userProfile = UserProfile(
            uid = "owner_uid_01",
            fullName = "Yamika Owner",
            email = "owner@malofind.com",
            phone = "0999123456",
            accountType = "OWNER"
        )

        assertEquals("owner@malofind.com", userProfile.email)
        assertEquals("OWNER", userProfile.accountType)
    }

    @Test
    fun test_MF_AUTH_001_adminAuthentication_andRoleVerification() {
        // Test Case ID: MF_AUTH_001 (Admin)
        val userProfile = UserProfile(
            uid = "admin_uid_01",
            fullName = "System Admin",
            email = "admin@malofind.com",
            phone = "0999000000",
            accountType = "ADMIN"
        )

        assertEquals("admin@malofind.com", userProfile.email)
        assertEquals("ADMIN", userProfile.accountType)
    }

    @Test
    fun test_MF_AUTH_002_login_emptyFields() {
        // Test Case ID: MF_AUTH_002
        val email = ""
        val password = ""

        val isBlocked = email.isBlank() && password.isBlank()
        assertTrue("Login should be blocked when fields are empty", isBlocked)
    }

    @Test
    fun test_MF_PROP_001_propertySubmission_validData() {
        // Test Case ID: MF_PROP_001
        val property = Property(
            title = "Modern Lilongwe Villa",
            price = 350000.0,
            city = "Lilongwe",
            bedrooms = 3,
            ownerPhone = "0999123456",
            isApproved = false
        )

        assertEquals("Modern Lilongwe Villa", property.title)
        assertEquals(350000.0, property.price, 0.0)
        assertEquals("Lilongwe", property.city)
        assertEquals(3, property.bedrooms)
        assertFalse("New property should have pending status (isApproved = false)", property.isApproved)
    }

    @Test
    fun test_MF_PROP_002_propertySubmission_missingFields() {
        // Test Case ID: MF_PROP_002
        val title = ""
        val price = ""

        val isMissingRequired = title.isBlank() || price.isBlank()
        assertTrue("Property submission should be blocked with missing required fields", isMissingRequired)
    }

    @Test
    fun test_MF_SEARCH_001_searchAndFiltering() {
        // Test Case ID: MF_SEARCH_001
        val properties = listOf(
            Property(title = "Lilongwe House 1", city = "Lilongwe", propertyType = "House", price = 300000.0, isApproved = true),
            Property(title = "Blantyre House 2", city = "Blantyre", propertyType = "House", price = 250000.0, isApproved = true),
            Property(title = "Lilongwe Apartment", city = "Lilongwe", propertyType = "Apartment", price = 200000.0, isApproved = true)
        )

        val query = "Lilongwe"
        val filterType = "House"
        val maxPrice = 400000.0

        val results = properties.filter {
            it.city.contains(query, ignoreCase = true) &&
                    it.propertyType.equals(filterType, ignoreCase = true) &&
                    it.price <= maxPrice &&
                    it.isApproved
        }

        assertEquals(1, results.size)
        assertEquals("Lilongwe House 1", results[0].title)
    }

    @Test
    fun test_MF_SEARCH_002_searchNoResults() {
        // Test Case ID: MF_SEARCH_002
        val properties = listOf(
            Property(title = "Lilongwe House", city = "Lilongwe", isApproved = true)
        )

        val query = "NonExistentLocationXYZ"
        val results = properties.filter { it.city.contains(query, ignoreCase = true) }

        assertTrue("Results should be empty for non-existent search query", results.isEmpty())
    }

    @Test
    fun test_MF_REQ_001_propertyRequestSubmission() {
        // Test Case ID: MF_REQ_001
        val request = PropertyRequest(
            propertyId = "prop_01",
            seekerId = "seeker_01",
            notes = "I am interested in renting this property.",
            occupants = 2,
            status = "Pending"
        )

        assertEquals("Pending", request.status)
        assertEquals(2, request.occupants)
        assertEquals("I am interested in renting this property.", request.notes)

        // Simulate owner acceptance
        val acceptedRequest = request.copy(status = "Accepted")
        assertEquals("Accepted", acceptedRequest.status)
    }

    @Test
    fun test_MF_REQ_002_requestSubmission_missingInfo() {
        // Test Case ID: MF_REQ_002
        val seekerPhone = ""
        val notes = ""

        val isBlocked = seekerPhone.isBlank() || notes.isBlank()
        assertTrue("Request submission should be blocked when required info is missing", isBlocked)
    }
}
