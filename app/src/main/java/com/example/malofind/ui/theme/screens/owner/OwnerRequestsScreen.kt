package com.example.malofind.ui.theme.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.PropertyRequest
import com.example.malofind.data.repository.PropertyRequestRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerRequestsScreen(
    onBack: () -> Unit,
    onRefresh: () -> Unit = {},
    requestRepository: PropertyRequestRepository = PropertyRequestRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var requests by remember { mutableStateOf<List<PropertyRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val ownerId = authRepository.getCurrentUserUid() ?: ""
    val scope = rememberCoroutineScope()

    val loadData = {
        scope.launch {
            isLoading = true
            if (ownerId.isNotBlank()) {
                requests = requestRepository.getRequestsByOwner(ownerId)
            } else {
                requests = requestRepository.getAllRequests()
            }
            isLoading = false
        }
    }

    LaunchedEffect(ownerId) {
        loadData()
    }

    val pendingCount = requests.count { it.status.equals("Pending", ignoreCase = true) }
    val acceptedCount = requests.count { it.status.equals("Approved", ignoreCase = true) || it.status.equals("Accepted", ignoreCase = true) }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Property Requests",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${requests.size} total enquiries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaloFindTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        loadData()
                        onRefresh()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OwnerRequestStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Pending",
                            value = pendingCount.toString(),
                            color = MaloFindGold
                        )
                        OwnerRequestStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Accepted",
                            value = acceptedCount.toString(),
                            color = MaloFindGreen
                        )
                    }
                }

                item {
                    Text(
                        text = "Recent Enquiries",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaloFindDarkGreen
                    )
                }

                if (requests.isEmpty()) {
                    item {
                        Text(
                            text = "No property requests received yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaloFindTextSecondary,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(
                        items = requests,
                        key = { it.id }
                    ) { request ->
                        OwnerRequestItemCard(
                            request = request,
                            onAccept = {
                                scope.launch {
                                    requestRepository.updateRequestStatus(request.id, "Approved")
                                    loadData()
                                }
                            },
                            onDecline = {
                                scope.launch {
                                    requestRepository.updateRequestStatus(request.id, "Rejected")
                                    loadData()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerRequestStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun OwnerRequestItemCard(
    request: PropertyRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null, tint = MaloFindGreen, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = request.propertyTitle.ifBlank { "Property Enquiry" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = request.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = when (request.status.lowercase()) {
                        "approved", "accepted" -> MaloFindGreen
                        "rejected", "declined" -> Color(0xFFB3261E)
                        else -> MaloFindGold
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaloFindTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = request.seekerName.ifBlank { "Property Seeker" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }

            if (request.seekerPhone.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = MaloFindTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = request.seekerPhone, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                }
            }

            if (request.seekerEmail.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaloFindTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = request.seekerEmail, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                }
            }

            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Message: ${request.notes}", style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
            }

            if (request.status.equals("Pending", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDecline, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Decline")
                    }
                    Button(onClick = onAccept, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Accept")
                    }
                }
            }
        }
    }
}
