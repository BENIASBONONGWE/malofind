package com.example.malofind.ui.theme.screens.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sell
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.ImageRepository
import com.example.malofind.data.repository.PropertyRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    propertyId: String,
    onBack: () -> Unit,
    onPropertyUpdated: () -> Unit = {},
    repository: PropertyRepository = PropertyRepository()
) {
    var existingProperty by remember { mutableStateOf<Property?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var propertyType by remember { mutableStateOf("") }
    var listingType by remember { mutableStateOf("Rent") }
    var price by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imageRepository = remember { ImageRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    LaunchedEffect(propertyId) {
        isLoading = true
        val prop = repository.getPropertyById(propertyId)
        if (prop != null) {
            existingProperty = prop
            title = prop.title
            description = prop.description
            location = prop.location
            city = prop.city
            propertyType = prop.propertyType
            listingType = prop.listingType
            price = if (prop.price > 0) prop.price.toLong().toString() else ""
            bedrooms = prop.bedrooms.toString()
            bathrooms = prop.bathrooms.toString()
            phone = prop.ownerPhone
            email = prop.ownerEmail
            imageUrl = prop.imageUrl
        }
        isLoading = false
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Property", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSaving) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->

        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaloFindGreen)
            }
        } else {
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
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaloFindGold)
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        Column {
                            Text(
                                text = "Update listing details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Keep your property details & photos current for potential clients.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Text(
                    text = "Property Photo",
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
                    border = BorderStroke(1.dp, MaloFindGreen.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayModel = selectedImageUri ?: imageUrl.ifBlank { null }
                        if (displayModel != null) {
                            AsyncImage(
                                model = displayModel,
                                contentDescription = "Property Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)
                            ) {
                                Text("Change Photo")
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add Photo",
                                    tint = MaloFindGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text("Select Photo from Gallery")
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
                    label = { Text("Property title") },
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    minLines = 4,
                    maxLines = 6,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Location") },
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("City") },
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = propertyType,
                    onValueChange = { propertyType = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Property type") },
                    singleLine = true,
                    enabled = !isSaving
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val rentSelected = listingType.equals("Rent", ignoreCase = true) || listingType.equals("For Rent", ignoreCase = true)
                    val saleSelected = listingType.equals("Sale", ignoreCase = true) || listingType.equals("For Sale", ignoreCase = true)

                    if (rentSelected) {
                        Button(onClick = { listingType = "For Rent" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                            Icon(Icons.Default.Home, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("For Rent")
                        }
                    } else {
                        OutlinedButton(onClick = { listingType = "For Rent" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                            Icon(Icons.Default.Home, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("For Rent")
                        }
                    }

                    if (saleSelected) {
                        Button(onClick = { listingType = "For Sale" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                            Icon(Icons.Default.Sell, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("For Sale")
                        }
                    } else {
                        OutlinedButton(onClick = { listingType = "For Sale" }, modifier = Modifier.weight(1f), enabled = !isSaving) {
                            Icon(Icons.Default.Sell, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("For Sale")
                        }
                    }
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Price (MWK)") },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                        singleLine = true,
                        enabled = !isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = bathrooms,
                        onValueChange = { bathrooms = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Bathrooms") },
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
                    label = { Text("Phone number") },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email address") },
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
                        val parsedPrice = price.toDoubleOrNull() ?: 0.0
                        val parsedBedrooms = bedrooms.toIntOrNull() ?: 0
                        val parsedBathrooms = bathrooms.toIntOrNull() ?: 0

                        coroutineScope.launch {
                            isSaving = true
                            resultMessage = "Uploading photo & saving changes..."

                            var finalImageUrl = imageUrl
                            if (selectedImageUri != null) {
                                finalImageUrl = imageRepository.uploadPropertyImage(selectedImageUri!!, propertyId)
                            }

                            val updated = (existingProperty ?: Property()).copy(
                                id = propertyId,
                                title = title.trim(),
                                description = description.trim(),
                                location = location.trim(),
                                city = city.trim(),
                                propertyType = propertyType.trim(),
                                listingType = listingType,
                                price = parsedPrice,
                                bedrooms = parsedBedrooms,
                                bathrooms = parsedBathrooms,
                                imageUrl = finalImageUrl,
                                ownerPhone = phone.trim(),
                                ownerEmail = email.trim()
                            )

                            try {
                                repository.updateProperty(updated)
                                isSuccess = true
                                resultMessage = "Property updated successfully!"
                                onPropertyUpdated()
                            } catch (e: Exception) {
                                isSuccess = false
                                resultMessage = "Could not update property: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = title.isNotBlank() && price.isNotBlank() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
