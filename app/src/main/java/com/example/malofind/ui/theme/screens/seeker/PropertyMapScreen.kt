package com.example.malofind.ui.theme.screens.seeker

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.location.GpsCoordinates
import com.example.malofind.data.location.GpsLocationHelper
import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.PropertyRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyMapScreen(
    propertyId: String,
    onBack: () -> Unit,
    propertyRepository: PropertyRepository = PropertyRepository()
) {
    var property by remember { mutableStateOf<Property?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var userGpsLocation by remember { mutableStateOf<GpsCoordinates?>(null) }
    var isDetectingGps by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val gpsHelper = remember { GpsLocationHelper(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(propertyId) {
        isLoading = true
        property = propertyRepository.getPropertyById(propertyId)
        isLoading = false
    }

    val fetchGps = {
        scope.launch {
            isDetectingGps = true
            userGpsLocation = gpsHelper.getCurrentLocation()
            isDetectingGps = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchGps()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaloFindBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaloFindGreen)
        }
        return
    }

    val currentProperty = property
    if (currentProperty == null) {
        MapPropertyNotFoundScreen(onBack = onBack)
        return
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Property Location",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaloFindGreen
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            MapPlaceholder(
                propertyName = currentProperty.title,
                location = "${currentProperty.location}, ${currentProperty.city}",
                latitude = currentProperty.latitude,
                longitude = currentProperty.longitude,
                userGpsLocation = userGpsLocation
            )

            PropertyLocationCard(
                propertyName = currentProperty.title,
                location = "${currentProperty.location}, ${currentProperty.city}",
                latitude = currentProperty.latitude,
                longitude = currentProperty.longitude,
                onGetDirections = {
                    val lat = if (currentProperty.latitude != 0.0) currentProperty.latitude else -15.7861
                    val lng = if (currentProperty.longitude != 0.0) currentProperty.longitude else 35.0058
                    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(currentProperty.title)})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
                        context.startActivity(browserIntent)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            IconButton(
                onClick = {
                    if (gpsHelper.hasLocationPermission()) {
                        fetchGps()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
            ) {
                if (isDetectingGps) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaloFindGreen)
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My location",
                        tint = if (userGpsLocation != null) MaloFindGold else MaloFindGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPlaceholder(
    propertyName: String,
    location: String,
    latitude: Double,
    longitude: Double,
    userGpsLocation: GpsCoordinates?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaloFindGreen.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(MaloFindGreen.copy(alpha = 0.12f), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Property location",
                    tint = MaloFindGreen,
                    modifier = Modifier.size(55.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Map Location",
                color = MaloFindTextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = propertyName,
                color = MaloFindGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = location,
                color = MaloFindTextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (latitude != 0.0 && longitude != 0.0) {
                Text(
                    text = "GPS: ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}",
                    color = MaloFindGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "GPS location registered in Firestore",
                    color = MaloFindTextSecondary,
                    fontSize = 12.sp
                )
            }

            if (userGpsLocation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your GPS: ${userGpsLocation.cityName} (${String.format("%.4f", userGpsLocation.latitude)}, ${String.format("%.4f", userGpsLocation.longitude)})",
                    color = MaloFindGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PropertyLocationCard(
    propertyName: String,
    location: String,
    latitude: Double,
    longitude: Double,
    onGetDirections: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaloFindGreen.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Property",
                        tint = MaloFindGreen,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = propertyName,
                        color = MaloFindTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = location,
                        color = MaloFindTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGetDirections,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaloFindGold,
                    contentColor = MaloFindTextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Directions"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Get Directions in Google Maps",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MapPropertyNotFoundScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaloFindBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Property not found",
            tint = MaloFindGreen,
            modifier = Modifier.size(70.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Property not found",
            color = MaloFindTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The location for this property could not be found.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaloFindGreen
            )
        ) {
            Text(text = "Go Back")
        }
    }
}
