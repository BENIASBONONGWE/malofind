package com.example.malofind.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.malofind.data.repository.NotificationsRepository
import com.google.firebase.auth.FirebaseAuth

class NotificationSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
            val notificationsRepository = NotificationsRepository()
            notificationsRepository.getNotificationsForUser(userId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
