
package com.example.malofind

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreTest {

    fun testConnection(
        onResult: (Boolean, String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        val testData = hashMapOf(
            "message" to "MaloFind Firestore connection successful",
            "appName" to "MaloFind",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("connection_tests")
            .document("first_test")
            .set(testData)
            .addOnSuccessListener {
                onResult(
                    true,
                    "Firestore connected successfully! Test document saved."
                )
            }
            .addOnFailureListener { exception ->
                onResult(
                    false,
                    "Firestore test failed: ${exception.message}"
                )
            }
    }
}