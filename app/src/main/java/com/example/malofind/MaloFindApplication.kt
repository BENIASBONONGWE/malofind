package com.example.malofind

import android.app.Application
import com.example.malofind.data.worker.NotificationSyncScheduler
import com.example.malofind.data.worker.PropertySyncScheduler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class MaloFindApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            )
            .build()
        db.firestoreSettings = settings

        db.persistentCacheIndexManager?.enableIndexAutoCreation()

        // Schedule professional background workers with WorkManager
        PropertySyncScheduler.scheduleSync(this)
        NotificationSyncScheduler.scheduleSync(this)
    }

    companion object {
        lateinit var instance: MaloFindApplication
            private set
    }
}
