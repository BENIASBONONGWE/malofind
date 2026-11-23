package com.example.malofind.ui.theme.screens.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.malofind.data.location.GpsLocationHelper
import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.ImageRepository
import com.example.malofind.data.repository.PropertyRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    onBack: () -> Unit,
    onPropertySubmitted: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var propertyType by remember { mutableStateOf("") }
    var listingType by remember { mutableStateOf("Rent") }
    var price by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showSourceDialog by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var isFetchingGps by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val repository = remember { PropertyRepository() }
    val imageRepository = remember { ImageRepository(context) }
    val gpsHelper = remember { GpsLocationHelper(context) }
    val coroutineScope = rememberCoroutineScope()

    val photoFile = remember {
        File.createTempFile("property_cam_", ".jpg", context.cacheDir)
    }
    val cameraUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = selectedImageUris + uris
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUris = selectedImageUris + cameraUri
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(cameraUri)
        }
    }

    val fetchLocation = {
        coroutineScope.launch {
            isFetchingGps = true
            val coords = gpsHelper.getCurrentLocation()
            if (coords != null) {
                latitude = coords.latitude
                longitude = coords.longitude
                if (coords.address.isNotBlank()) location = coords.address
                if (coords.cityName.isNotBlank()) city = coords.cityName
                resultMessage = "GPS coordinates captured successfully!"
                isSuccess = true
            } else {
                resultMessage = "Could not detect GPS location. Ensure location is enabled."
                isSuccess = false
            }
            isFetchingGps = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            fetchLocation()
        } else {
            resultMessage = "Location permission is required to detect GPS coordinates."
            isSuccess = false
        }
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Property", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaloFindDarkGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AddHome, contentDescription = null, tint = MaloFindGold)
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Column {
                        Text(
                            text = "Post your property",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add photos from computer storage, local files, or camera.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Text(
                text = "Property Photos (${selectedImageUris.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaloFindDarkGreen
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindGreen.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUris.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(rememberScrollState())
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedImageUris.forEachIndexed { index, uri ->
                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Property Photo $index",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedImageUris = selectedImageUris.filterIndexed { i, _ -> i != index }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { showSourceDialog = true },
                                modifier = Modifier.size(150.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Text("Add More", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photos",
                                tint = MaloFindGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showSourceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Add Photos (Computer/Local Storage)")
                            }
                        }
                    }
                }
            }

            Text(
                text = "Property Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaloFindDarkGreen
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Property title *") },
                placeholder = { Text("e.g. Modern 3 Bedroom House") },
                singleLine = true,
                enabled = !isSaving
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description *") },
                placeholder = { Text("Describe the property and its features") },
                minLines = 4,
                maxLines = 6,
                enabled = !isSaving
            )

            OutlinedButton(
                onClick = {
                    if (gpsHelper.hasLocationPermission()) {
                        fetchLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && !isFetchingGps
            ) {
                if (isFetchingGps) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaloFindGreen)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Detecting GPS Location...")
                } else {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = MaloFindGreen)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (latitude != 0.0) "GPS Captured (${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)})" else "Use Current GPS Location",
                        color = MaloFindGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location / Area *") },
                placeholder = { Text("e.g. Area 25, near the main road") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                enabled = !isSaving
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City *") },
                placeholder = { Text("e.g. Lilongwe") },
                singleLine = true,
                enabled = !isSaving
            )

            OutlinedTextField(
                value = propertyType,
                onValueChange = { propertyType = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Property type *") },
                placeholder = { Text("e.g. House, Apartment, Room, Shop, Plot") },
                singleLine = true,
                enabled = !isSaving,
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
            )

            Text(
                text = "Listing Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (listingType == "Rent") {
                    Button(onClick = { listingType = "Rent" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("For Rent")
                    }
                } else {
                    OutlinedButton(onClick = { listingType = "Rent" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("For Rent")
                    }
                }

                if (listingType == "Sale") {
                    Button(onClick = { listingType = "Sale" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("For Sale")
                    }
                } else {
                    OutlinedButton(onClick = { listingType = "Sale" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text("For Sale")
                    }
                }
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Price (MWK) *") },
                placeholder = { Text("e.g. 250000") },
                singleLine = true,
                enabled = !isSaving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(imageVector = Icons.Default.Sell, contentDescription = null) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = bedrooms,
                    onValueChange = { bedrooms = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Bedrooms") },
                    placeholder = { Text("e.g. 3") },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = bathrooms,
                    onValueChange = { bathrooms = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Bathrooms") },
                    placeholder = { Text("e.g. 2") },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Text(
                text = "Owner Contact Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaloFindDarkGreen
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone number *") },
                placeholder = { Text("e.g. 0999 123 456") },
                singleLine = true,
                enabled = !isSaving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email address") },
                placeholder = { Text("e.g. owner@example.com") },
                singleLine = true,
                enabled = !isSaving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            if (resultMessage.isNotBlank()) {
                Text(
                    text = resultMessage,
                    color = if (isSuccess) MaloFindGreen else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        resultMessage = "Please enter a property title."
                        isSuccess = false
                        return@Button
                    }
                    if (description.isBlank()) {
                        resultMessage = "Please enter a property description."
                        isSuccess = false
                        return@Button
                    }
                    if (location.isBlank()) {
                        resultMessage = "Please enter the location or area."
                        isSuccess = false
                        return@Button
                    }
                    if (city.isBlank()) {
                        resultMessage = "Please enter the city."
                        isSuccess = false
                        return@Button
                    }
                    if (propertyType.isBlank()) {
                        resultMessage = "Please enter the property type (e.g. House, Apartment)."
                        isSuccess = false
                        return@Button
                    }
                    if (price.isBlank()) {
                        resultMessage = "Please enter the property price."
                        isSuccess = false
                        return@Button
                    }
                    if (phone.isBlank()) {
                        resultMessage = "Please enter your owner phone number."
                        isSuccess = false
                        return@Button
                    }

                    val parsedPrice = price.toDoubleOrNull()
                    if (parsedPrice == null || parsedPrice <= 0.0) {
                        resultMessage = "Enter a valid price greater than zero."
                        isSuccess = false
                        return@Button
                    }

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        resultMessage = "Please sign in before submitting a property."
                        isSuccess = false
                        return@Button
                    }

                    coroutineScope.launch {
                        isSaving = true
                        resultMessage = "Uploading photos & submitting property..."
                        isSuccess = false

                        val uploadedUrls = mutableListOf<String>()
                        for (uri in selectedImageUris) {
                            val url = imageRepository.uploadPropertyImage(uri)
                            if (url.isNotBlank()) {
                                uploadedUrls.add(url)
                            }
                        }

                        val primaryImageUrl = uploadedUrls.firstOrNull() ?: ""

                        val property = Property(
                            title = title.trim(),
                            description = description.trim(),
                            location = location.trim(),
                            city = city.trim(),
                            latitude = latitude,
                            longitude = longitude,
                            propertyType = propertyType.trim(),
                            listingType = if (listingType == "Rent") "For Rent" else "For Sale",
                            price = parsedPrice,
                            currency = "MWK",
                            bedrooms = bedrooms.toIntOrNull() ?: 0,
                            bathrooms = bathrooms.toIntOrNull() ?: 0,
                            imageUrl = primaryImageUrl,
                            imageUrls = uploadedUrls,
                            ownerId = currentUser.uid,
                            ownerName = currentUser.displayName ?: "",
                            ownerPhone = phone.trim(),
                            ownerEmail = email.trim().ifBlank { currentUser.email ?: "" },
                            isApproved = false,
                            isAvailable = true
                        )

                        try {
                            repository.addProperty(property)
                            isSuccess = true
                            resultMessage = "Property submitted successfully with photos!"
                            onPropertySubmitted()
                        } catch (exception: Exception) {
                            resultMessage = "Could not submit property: ${exception.localizedMessage ?: "Please try again."}"
                            isSuccess = false
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Property", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Select Photo Source", fontWeight = FontWeight.Bold) },
            text = { Text("Choose where you would like to select property pictures from your computer storage or camera.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        photoPickerLauncher.launch(arrayOf("image/*"))
                    }
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaloFindGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Computer Storage / Files", color = MaloFindGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraLauncher.launch(cameraUri)
                        } else {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaloFindGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Camera Photo", color = MaloFindGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
