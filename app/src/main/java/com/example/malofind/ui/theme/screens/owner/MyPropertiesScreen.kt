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
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPropertiesScreen(
    onBack: () -> Unit,
    onAddPropertyClick: () -> Unit = {},
    onEditPropertyClick: (String) -> Unit = {},
    onViewPropertyClick: (String) -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val ownerId = authRepository.getCurrentUserUid() ?: ""

    LaunchedEffect(ownerId) {
        isLoading = true
        if (ownerId.isNotBlank()) {
            properties = propertyRepository.getPropertiesByOwner(ownerId)
        } else {
            properties = propertyRepository.getApprovedProperties()
        }
        isLoading = false
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Properties",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${properties.size} properties listed",
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
        } else if (properties.isEmpty()) {
            EmptyPropertiesContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onAddPropertyClick = onAddPropertyClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Your Listings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaloFindDarkGreen
                    )
                }

                item {
                    Text(
                        text = "Manage the properties you have submitted to MaloFind.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaloFindTextSecondary
                    )
                }

                items(
                    items = properties,
                    key = { property -> property.id }
                ) { property ->
                    OwnerPropertyCard(
                        property = property,
                        onEditClick = { onEditPropertyClick(property.id) },
                        onViewClick = { onViewPropertyClick(property.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onAddPropertyClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddHome,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Add Another Property",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerPropertyCard(
    property: Property,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            MaloFindCardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaloFindGreen,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaloFindTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.size(3.dp))

                        Text(
                            text = "${property.location}, ${property.city}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaloFindTextSecondary
                        )
                    }
                }

                PropertyStatusBadge(
                    isApproved = property.isApproved
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formatPrice(property.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaloFindGreen
                    )

                    Text(
                        text = if (property.listingType.equals("Rent", true)) {
                            "For Rent"
                        } else {
                            "For Sale"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaloFindTextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${property.bedrooms} bedrooms",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${property.bathrooms} bathrooms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaloFindTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text("View")
                }

                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text("Edit")
                }
            }
        }
    }
}

@Composable
private fun PropertyStatusBadge(
    isApproved: Boolean
) {
    val backgroundColor = if (isApproved) {
        MaloFindGreen.copy(alpha = 0.12f)
    } else {
        MaloFindGold.copy(alpha = 0.16f)
    }

    val textColor = if (isApproved) {
        MaloFindGreen
    } else {
        MaloFindError
    }

    val statusText = if (isApproved) {
        "Approved"
    } else {
        "Pending"
    }

    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun EmptyPropertiesContent(
    modifier: Modifier = Modifier,
    onAddPropertyClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            tint = MaloFindGreen,
            modifier = Modifier.size(70.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "No Properties Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaloFindDarkGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You have not posted any properties yet. Add your first property to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaloFindTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddPropertyClick
        ) {
            Icon(
                imageVector = Icons.Default.AddHome,
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text("Add Property")
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    return "MWK ${formatter.format(price)}"
}
