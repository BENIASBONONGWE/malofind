package com.example.malofind.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.malofind.data.local.dao.PropertyDao
import com.example.malofind.data.local.entity.PropertyEntity

@Database(entities = [PropertyEntity::class], version = 1, exportSchema = false)
abstract class MaloFindDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: MaloFindDatabase? = null

        fun getDatabase(context: Context): MaloFindDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MaloFindDatabase::class.java,
                    "malofind_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
