package com.example.malofind.ui.theme.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.malofind.data.model.Property
import com.example.malofind.data.model.PropertyRepository
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.PropertyRequest
import com.example.malofind.data.repository.PropertyRequestRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    onAddPropertyClick: () -> Unit = {},
    onMyPropertiesClick: () -> Unit = {},
    onApprovalStatusClick: () -> Unit = {},
    onRequestsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    requestRepository: PropertyRequestRepository = PropertyRequestRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var requests by remember { mutableStateOf<List<PropertyRequest>>(emptyList()) }
    val ownerId = authRepository.getCurrentUserUid() ?: ""

    LaunchedEffect(ownerId) {
        if (ownerId.isNotBlank()) {
            properties = propertyRepository.getPropertiesByOwner(ownerId)
            requests = requestRepository.getRequestsByOwner(ownerId)
        } else {
            properties = propertyRepository.getApprovedProperties()
            requests = requestRepository.getAllRequests()
        }
    }

    val totalProperties = properties.size
    val approvedCount = properties.count { it.isApproved }
    val pendingCount = properties.count { !it.isApproved }
    val requestCount = requests.size

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Owner Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Manage your properties",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaloFindTextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                    }

                    IconButton(onClick = onProfileClick) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }

                    IconButton(onClick = onLogoutClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { WelcomeOwnerCard() }

            item {
                Text(
                    text = "Property Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Properties",
                        value = totalProperties.toString(),
                        icon = Icons.Default.Home,
                        iconBackground = MaloFindGreen
                    )

                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Approved",
                        value = approvedCount.toString(),
                        icon = Icons.Default.Verified,
                        iconBackground = MaloFindGreen
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending",
                        value = pendingCount.toString(),
                        icon = Icons.Default.Pending,
                        iconBackground = MaloFindGold
                    )

                    DashboardStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Requests",
                        value = requestCount.toString(),
                        icon = Icons.Default.Person,
                        iconBackground = MaloFindDarkGreen
                    )
                }
            }

            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OwnerActionCard(
                    icon = Icons.Default.AddHome,
                    title = "Add New Property",
                    description = "Post a new house or property on MaloFind.",
                    onClick = onAddPropertyClick
                )
            }

            item {
                OwnerActionCard(
                    icon = Icons.Default.Home,
                    title = "My Properties",
                    description = "View and manage all properties you have posted.",
                    onClick = onMyPropertiesClick
                )
            }

            item {
                OwnerActionCard(
                    icon = Icons.Default.Verified,
                    title = "Approval Status",
                    description = "Check which properties are approved or pending.",
                    onClick = onApprovalStatusClick
                )
            }

            item {
                OwnerActionCard(
                    icon = Icons.Default.Person,
                    title = "Property Requests",
                    description = "View requests from people interested in your properties.",
                    onClick = onRequestsClick
                )
            }
        }
    }
}

@Composable
private fun WelcomeOwnerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaloFindDarkGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(color = MaloFindGold, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Welcome, Property Owner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Keep your property information up to date.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun DashboardStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconBackground: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color = iconBackground.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconBackground, modifier = Modifier.size(23.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaloFindDarkGreen)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun OwnerActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = MaloFindGreen.copy(alpha = 0.10f), shape = RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaloFindGreen, modifier = Modifier.size(25.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
            }

            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Open", tint = MaloFindGreen)
        }
    }
}
