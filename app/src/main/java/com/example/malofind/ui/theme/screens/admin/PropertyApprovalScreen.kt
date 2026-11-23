package com.example.malofind.ui.theme.screens.admin

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.PropertyRepository
import com.example.malofind.ui.components.MaloFindTextField
import com.example.malofind.ui.components.MaloFindTopBar
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindDarkGreen
import com.example.malofind.ui.theme.MaloFindError
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PropertyApprovalScreen(
    onBack: () -> Unit,
    propertyRepository: PropertyRepository = PropertyRepository()
) {
    var searchQuery by remember { mutableStateOf("") }
    var pendingProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val loadData = {
        scope.launch {
            isLoading = true
            pendingProperties = propertyRepository.getPendingProperties()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredProperties = pendingProperties.filter { property ->
        property.title.contains(searchQuery, ignoreCase = true) ||
                property.location.contains(searchQuery, ignoreCase = true) ||
                property.city.contains(searchQuery, ignoreCase = true) ||
                property.ownerName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaloFindBackground)
    ) {
        MaloFindTopBar(
            title = "Property Approval",
            showBackButton = true,
            onBackClick = onBack
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaloFindGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MaloFindTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "Search pending properties",
                        leadingIcon = Icons.Default.Search
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${filteredProperties.size} pending approval",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaloFindDarkGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (filteredProperties.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No pending properties to review.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaloFindTextSecondary
                            )
                        }
                    }
                } else {
                    items(
                        items = filteredProperties,
                        key = { it.id }
                    ) { property ->
                        ApprovalPropertyItem(
                            property = property,
                            onViewClick = { selectedProperty = property },
                            onApproveClick = {
                                scope.launch {
                                    propertyRepository.approveProperty(property.id)
                                    loadData()
                                }
                            },
                            onRejectClick = {
                                selectedProperty = property
                                showRejectDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showRejectDialog && selectedProperty != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Property") },
            text = { Text("Are you sure you want to reject and remove '${selectedProperty?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedProperty?.let { prop ->
                            scope.launch {
                                propertyRepository.deleteProperty(prop.id)
                                loadData()
                            }
                        }
                        showRejectDialog = false
                    }
                ) {
                    Text("Reject", color = MaloFindError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ApprovalPropertyItem(
    property: Property,
    onViewClick: () -> Unit,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color = MaloFindGold.copy(alpha = 0.18f), shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaloFindGreen)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = property.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${property.location}, ${property.city}", style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                    Text(text = "Owner: ${property.ownerName.ifBlank { "Owner" }}", style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaloFindGold.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "Pending",
                        color = MaloFindGold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onViewClick) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                }

                OutlinedButton(
                    onClick = onRejectClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }

                Button(
                    onClick = onApproveClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}
