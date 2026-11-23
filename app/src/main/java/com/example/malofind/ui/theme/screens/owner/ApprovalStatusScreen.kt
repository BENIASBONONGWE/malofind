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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindError
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalStatusScreen(
    onBack: () -> Unit,
    onRefresh: () -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val ownerId = authRepository.getCurrentUserUid() ?: ""
    val scope = rememberCoroutineScope()

    val loadData = {
        scope.launch {
            isLoading = true
            if (ownerId.isNotBlank()) {
                properties = propertyRepository.getPropertiesByOwner(ownerId)
            } else {
                properties = propertyRepository.getAllProperties()
            }
            isLoading = false
        }
    }

    LaunchedEffect(ownerId) {
        loadData()
    }

    val approvedCount = properties.count { it.isApproved }
    val pendingCount = properties.count { !it.isApproved }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Approval Status",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your property reviews",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaloFindTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            loadData()
                            onRefresh()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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
                        ApprovalStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Approved",
                            value = approvedCount.toString(),
                            icon = Icons.Default.CheckCircle,
                            iconColor = MaloFindGreen
                        )

                        ApprovalStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Pending",
                            value = pendingCount.toString(),
                            icon = Icons.Default.Pending,
                            iconColor = MaloFindGold
                        )
                    }
                }

                item {
                    Text(
                        text = "Submitted Properties",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaloFindDarkGreen
                    )
                }

                if (properties.isEmpty()) {
                    item {
                        Text(
                            text = "No properties submitted yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaloFindTextSecondary,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(
                        items = properties,
                        key = { property -> property.id }
                    ) { property ->
                        PropertyApprovalCard(property = property)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApprovalStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun PropertyApprovalCard(
    property: Property
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaloFindGreen, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.size(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = property.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaloFindTextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(text = "${property.location}, ${property.city}", style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (property.isApproved) "Status: Approved" else "Status: Pending Review",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (property.isApproved) MaloFindGreen else MaloFindGold
                )
                Icon(
                    imageVector = if (property.isApproved) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (property.isApproved) MaloFindGreen else MaloFindGold
                )
            }
        }
    }
}
