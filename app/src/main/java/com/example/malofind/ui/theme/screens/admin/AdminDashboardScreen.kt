package com.example.malofind.ui.theme.screens.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.malofind.data.model.PropertyRepository
import com.example.malofind.data.repository.PropertyRequestRepository
import com.example.malofind.data.repository.UserRepository
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onManageUsersClick: () -> Unit = {},
    onManageOwnersClick: () -> Unit = {},
    onManagePropertiesClick: () -> Unit = {},
    onPropertyApprovalClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    userRepository: UserRepository = UserRepository(),
    propertyRepository: PropertyRepository = PropertyRepository(),
    requestRepository: PropertyRequestRepository = PropertyRequestRepository()
) {
    var totalUsersCount by remember { mutableStateOf(0) }
    var totalOwnersCount by remember { mutableStateOf(0) }
    var totalPropertiesCount by remember { mutableStateOf(0) }
    var pendingPropertiesCount by remember { mutableStateOf(0) }
    var approvedPropertiesCount by remember { mutableStateOf(0) }
    var totalRequestsCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val users = userRepository.getAllUsers()
        totalUsersCount = users.size
        totalOwnersCount = users.count { it.role.equals("Owner", ignoreCase = true) }

        val properties = propertyRepository.getAllProperties()
        totalPropertiesCount = properties.size
        approvedPropertiesCount = properties.count { it.isApproved }
        pendingPropertiesCount = properties.count { !it.isApproved }

        val requests = requestRepository.getAllRequests()
        totalRequestsCount = requests.size
    }

    val quickActions = listOf(
        AdminAction(
            title = "Manage Users",
            description = "View and manage registered users",
            icon = Icons.Default.Group,
            onClick = onManageUsersClick
        ),
        AdminAction(
            title = "Manage Owners",
            description = "Review property owners and agents",
            icon = Icons.Default.Person,
            onClick = onManageOwnersClick
        ),
        AdminAction(
            title = "Manage Properties",
            description = "View all submitted properties",
            icon = Icons.Default.Business,
            onClick = onManagePropertiesClick
        ),
        AdminAction(
            title = "Property Approval",
            description = "Review and approve properties",
            icon = Icons.Default.CheckCircle,
            onClick = onPropertyApprovalClick
        ),
        AdminAction(
            title = "Reports",
            description = "View system reports and statistics",
            icon = Icons.Default.Report,
            onClick = onReportsClick
        ),
        AdminAction(
            title = "Settings",
            description = "Manage administrator settings",
            icon = Icons.Default.Settings,
            onClick = onSettingsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaloFindBackground)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaloFindGreen)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Administrator",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MaloFind Management",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            IconButton(onClick = onNotificationsClick) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
            }

            IconButton(onClick = onSettingsClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }

            IconButton(onClick = onLogoutClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Welcome, Administrator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaloFindDarkGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Monitor and manage the MaloFind platform.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaloFindTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalUsersCount.toString(),
                    label = "Users"
                )

                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalOwnersCount.toString(),
                    label = "Owners"
                )

                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalPropertiesCount.toString(),
                    label = "Properties"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = pendingPropertiesCount.toString(),
                    label = "Pending"
                )

                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = approvedPropertiesCount.toString(),
                    label = "Approved"
                )

                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    value = totalRequestsCount.toString(),
                    label = "Requests"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaloFindDarkGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quickActions) { action ->
                    AdminActionCard(action = action)
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaloFindGreen)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun AdminActionCard(
    action: AdminAction
) {
    Card(
        onClick = action.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = MaloFindGold.copy(alpha = 0.18f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = action.icon, contentDescription = action.title, tint = MaloFindGreen, modifier = Modifier.size(24.dp))
            }

            Column {
                Text(text = action.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaloFindDarkGreen)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = action.description, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
            }
        }
    }
}

private data class AdminAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
