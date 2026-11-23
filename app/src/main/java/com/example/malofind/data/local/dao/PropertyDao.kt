package com.example.malofind.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.malofind.data.local.entity.PropertyEntity

@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties WHERE isApproved = 1 AND isAvailable = 1 ORDER BY createdAt DESC")
    fun getApprovedProperties(): List<PropertyEntity>

    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun getAllProperties(): List<PropertyEntity>

    @Query("SELECT * FROM properties WHERE isApproved = 0 ORDER BY createdAt DESC")
    fun getPendingProperties(): List<PropertyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperties(properties: List<PropertyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: PropertyEntity)

    @Query("DELETE FROM properties WHERE id = :propertyId")
    fun deleteProperty(propertyId: String): Int

    @Query("DELETE FROM properties")
    fun clearProperties(): Int
}
