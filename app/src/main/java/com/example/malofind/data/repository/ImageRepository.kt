package com.example.malofind.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class ImageRepository(
    private val context: Context,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun uploadPropertyImage(uri: Uri, propertyId: String = UUID.randomUUID().toString()): String {
        // Prioritize Base64 encoding so property images are embedded in Firestore
        // and universally viewable by all seekers across all devices.
        val base64Url = compressUriToBase64(uri)
        if (base64Url != null) {
            return base64Url
        }

        // Fallback to Firebase Storage if Base64 conversion is unavailable
        return try {
            val storageRef = storage.reference.child("properties/$propertyId/${UUID.randomUUID()}.jpg")
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            uri.toString()
        }
    }

    private fun compressUriToBase64(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

            val maxDimension = 700
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = (maxDimension.toFloat() / Math.max(width, height)).coerceAtMost(1.0f)

            val scaledBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                (width * scale).toInt(),
                (height * scale).toInt(),
                true
            )

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
}
