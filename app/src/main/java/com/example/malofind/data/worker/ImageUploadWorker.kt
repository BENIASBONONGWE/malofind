package com.example.malofind.data.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.malofind.data.repository.ImageRepository

class ImageUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val imageUriString = inputData.getString("imageUri") ?: return Result.failure()
            val propertyId = inputData.getString("propertyId") ?: ""
            val uri = Uri.parse(imageUriString)

            val imageRepository = ImageRepository(applicationContext)
            imageRepository.uploadPropertyImage(uri, propertyId)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
