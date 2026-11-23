package com.example.malofind.ui.theme.screens.seeker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.model.Property
import com.example.malofind.data.model.PropertyRepository
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.PropertyRequest
import com.example.malofind.data.repository.PropertyRequestRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitRequestScreen(
    propertyId: String,
    onBack: () -> Unit,
    onRequestSubmitted: (PropertyRequest) -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    requestRepository: PropertyRequestRepository = PropertyRequestRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var property by remember { mutableStateOf<Property?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var moveInDate by remember { mutableStateOf("Immediate") }

    var isSubmitting by remember { mutableStateOf(false) }
    var requestSubmitted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(propertyId) {
        isLoading = true
        property = propertyRepository.getPropertyById(propertyId)
        authRepository.getCurrentUserProfile().onSuccess { user ->
            if (name.isBlank()) name = user.fullName
            if (email.isBlank()) email = user.email
            if (phone.isBlank()) phone = user.phone
        }
        isLoading = false
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaloFindBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = MaloFindGreen)
        }
        return
    }

    val currentProperty = property
    if (currentProperty == null) {
        RequestPropertyNotFoundScreen(onBack = onBack)
        return
    }

    if (requestSubmitted) {
        RequestSubmittedScreen(
            propertyTitle = currentProperty.title,
            onBack = onBack
        )
        return
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Request Property",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSubmitting) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {

            RequestPropertySummaryCard(
                title = currentProperty.title,
                location = "${currentProperty.location}, ${currentProperty.city}",
                price = formatSubmitRequestPrice(currentProperty)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Contact Details",
                color = MaloFindTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = moveInDate,
                onValueChange = { moveInDate = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Desired Move-in Date") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("Additional Message / Notes") },
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        val newRequest = PropertyRequest(
                            propertyId = currentProperty.id,
                            propertyTitle = currentProperty.title,
                            propertyLocation = "${currentProperty.location}, ${currentProperty.city}",
                            seekerId = authRepository.getCurrentUserUid() ?: "",
                            seekerName = name.ifBlank { "Seeker" },
                            seekerEmail = email,
                            seekerPhone = phone,
                            ownerId = currentProperty.ownerId,
                            moveInDate = moveInDate,
                            notes = notes,
                            status = "Pending"
                        )
                        requestRepository.addRequest(newRequest)
                        isSubmitting = false
                        requestSubmitted = true
                        onRequestSubmitted(newRequest)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = name.isNotBlank() && phone.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaloFindGreen,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Submit Request", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RequestPropertySummaryCard(
    title: String,
    location: String,
    price: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = MaloFindGreen, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = MaloFindTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = location, color = MaloFindTextSecondary, fontSize = 12.sp)
                Text(text = price, color = MaloFindGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RequestSubmittedScreen(
    propertyTitle: String,
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
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaloFindGreen, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Request Submitted!", color = MaloFindTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Your request for $propertyTitle has been sent to the property owner.", color = MaloFindTextSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)) {
            Text("Done")
        }
    }
}

@Composable
private fun RequestPropertyNotFoundScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaloFindBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Property Not Found", color = MaloFindTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onBack) { Text("Go Back") }
    }
}

private fun formatSubmitRequestPrice(property: Property): String {
    val formattedPrice = String.format("%,.0f", property.price)
    return if (property.listingType.equals("For Rent", ignoreCase = true)) {
        "MWK $formattedPrice / month"
    } else {
        "MWK $formattedPrice"
    }
}
