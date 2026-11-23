package com.example.malofind.data.worker

import android.content.Context
import android.net.Uri
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object ImageUploadScheduler {

    fun enqueueImageUpload(context: Context, imageUri: Uri, propertyId: String) {
        val data = Data.Builder()
            .putString("imageUri", imageUri.toString())
            .putString("propertyId", propertyId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(uploadRequest)
    }
}
