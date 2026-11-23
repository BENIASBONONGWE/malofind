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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.malofind.data.model.PropertyRepository
import com.example.malofind.ui.components.MaloFindTextField
import com.example.malofind.ui.components.MaloFindTopBar
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindError
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindSuccess
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ManagePropertiesScreen(
    onBack: () -> Unit,
    propertyRepository: PropertyRepository = PropertyRepository()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val loadData = {
        scope.launch {
            isLoading = true
            properties = propertyRepository.getAllProperties()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredProperties = properties.filter { property ->
        property.title.contains(searchQuery, ignoreCase = true) ||
                property.location.contains(searchQuery, ignoreCase = true) ||
                property.city.contains(searchQuery, ignoreCase = true) ||
                property.ownerName.contains(searchQuery, ignoreCase = true)
    }

    val totalProperties = properties.size
    val approvedProperties = properties.count { it.isApproved }
    val pendingProperties = properties.count { !it.isApproved }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaloFindBackground)
    ) {
        MaloFindTopBar(
            title = "Manage Properties",
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
                        label = "Search properties",
                        leadingIcon = Icons.Default.Search
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PropertySummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Total",
                            count = totalProperties.toString(),
                            color = MaloFindGreen
                        )
                        PropertySummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Approved",
                            count = approvedProperties.toString(),
                            color = MaloFindSuccess
                        )
                        PropertySummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Pending",
                            count = pendingProperties.toString(),
                            color = MaloFindGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(
                    items = filteredProperties,
                    key = { it.id }
                ) { property ->
                    ManagePropertyItem(
                        property = property,
                        onViewClick = { selectedProperty = property },
                        onDeleteClick = {
                            selectedProperty = property
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && selectedProperty != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Property") },
            text = { Text("Are you sure you want to delete '${selectedProperty?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedProperty?.let { prop ->
                            scope.launch {
                                propertyRepository.deleteProperty(prop.id)
                                loadData()
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaloFindError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PropertySummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun ManagePropertyItem(
    property: Property,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = MaloFindGreen.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaloFindGreen)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = property.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${property.location}, ${property.city}", style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                Text(
                    text = String.format(Locale.US, "MWK %,.0f", property.price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaloFindGreen
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (property.isApproved) MaloFindSuccess.copy(alpha = 0.15f) else MaloFindGold.copy(alpha = 0.18f)
            ) {
                Text(
                    text = if (property.isApproved) "Approved" else "Pending",
                    color = if (property.isApproved) MaloFindSuccess else MaloFindGold,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onViewClick) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = "View", tint = MaloFindGreen)
            }

            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaloFindError)
            }
        }
    }
}
