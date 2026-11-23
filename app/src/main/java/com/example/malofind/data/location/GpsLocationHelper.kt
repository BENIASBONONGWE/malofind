package com.example.malofind.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class GpsCoordinates(
    val latitude: Double,
    val longitude: Double,
    val cityName: String = "",
    val address: String = ""
)

class GpsLocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GpsCoordinates? {
        if (!hasLocationPermission()) return null

        return try {
            val location: Location? = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()
                ?: fusedLocationClient.lastLocation.await()

            if (location != null) {
                val addressDetails = getAddressFromCoordinates(location.latitude, location.longitude)
                GpsCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = addressDetails.first,
                    address = addressDetails.second
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getAddressFromCoordinates(lat: Double, lng: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Malawi"
                val addressText = addr.getAddressLine(0) ?: "$city ($lat, $lng)"
                Pair(city, addressText)
            } else {
                Pair("Malawi", "Lat: $lat, Lng: $lng")
            }
        } catch (e: Exception) {
            Pair("Malawi", "Lat: $lat, Lng: $lng")
        }
    }
}
