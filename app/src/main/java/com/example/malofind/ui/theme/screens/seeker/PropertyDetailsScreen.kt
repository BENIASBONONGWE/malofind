package com.example.malofind.ui.theme.screens.seeker

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.model.Property
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.FavouritesRepository
import com.example.malofind.data.repository.PropertyRepository
import com.example.malofind.ui.components.MaloFindImage
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    propertyId: String,
    onBack: () -> Unit,
    onMapClick: () -> Unit = {},
    onRequestProperty: () -> Unit = {},
    onCallOwner: () -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    favouritesRepository: FavouritesRepository = FavouritesRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var property by remember { mutableStateOf<Property?>(null) }
    var isFavourite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val userId = authRepository.getCurrentUserUid() ?: ""
    val scope = rememberCoroutineScope()

    LaunchedEffect(propertyId) {
        isLoading = true
        property = propertyRepository.getPropertyById(propertyId)
        if (userId.isNotBlank()) {
            val favs = favouritesRepository.getFavouritePropertyIds(userId)
            isFavourite = propertyId in favs
        }
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaloFindBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaloFindGreen)
        }
        return
    }

    val currentProperty = property
    if (currentProperty == null) {
        PropertyNotFoundScreen(onBack = onBack)
        return
    }

    val displayImageUrl = currentProperty.imageUrl.ifBlank {
        currentProperty.imageUrls.firstOrNull() ?: ""
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Property Details",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (userId.isNotBlank()) {
                                scope.launch {
                                    val newFavState = favouritesRepository.toggleFavourite(userId, propertyId)
                                    isFavourite = newFavState
                                }
                            } else {
                                isFavourite = !isFavourite
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavourite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Favourite",
                            tint = if (isFavourite) {
                                MaloFindGold
                            } else {
                                Color.White
                            }
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
                .padding(16.dp)
        ) {

            PropertyImagePlaceholder(
                title = currentProperty.title,
                listingType = currentProperty.listingType,
                imageUrl = displayImageUrl
            )

            Spacer(modifier = Modifier.height(16.dp))

            PropertyHeaderSection(
                title = currentProperty.title,
                location = "${currentProperty.location}, ${currentProperty.city}",
                price = formatDetailsPrice(currentProperty)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PropertyQuickInfoCard(
                bedrooms = currentProperty.bedrooms,
                bathrooms = currentProperty.bathrooms,
                propertyType = currentProperty.propertyType,
                listingType = currentProperty.listingType
            )

            Spacer(modifier = Modifier.height(16.dp))

            PropertyDescriptionSection(
                description = currentProperty.description
            )

            Spacer(modifier = Modifier.height(16.dp))

            OwnerInfoSection(
                ownerName = currentProperty.ownerName.ifBlank { "Property Owner" },
                ownerPhone = currentProperty.ownerPhone.ifBlank { "Contact provided upon request" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMapClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaloFindGreen
                    ),
                    border = BorderStroke(1.dp, MaloFindGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "View Map"
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "View Map",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onRequestProperty,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaloFindGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Request Property",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PropertyImagePlaceholder(
    title: String,
    listingType: String,
    imageUrl: String = ""
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaloFindGreen.copy(alpha = 0.12f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MaloFindImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun PropertyHeaderSection(
    title: String,
    location: String,
    price: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = MaloFindTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaloFindGreen,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = location,
                color = MaloFindTextSecondary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = price,
            color = MaloFindGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PropertyQuickInfoCard(
    bedrooms: Int,
    bathrooms: Int,
    propertyType: String,
    listingType: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoItem(label = "Bedrooms", value = bedrooms.toString())
            InfoItem(label = "Bathrooms", value = bathrooms.toString())
            InfoItem(label = "Type", value = propertyType)
            InfoItem(label = "Status", value = listingType)
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = MaloFindTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = MaloFindTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PropertyDescriptionSection(
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Description",
                color = MaloFindTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description.ifBlank { "No detailed description provided." },
                color = MaloFindTextSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun OwnerInfoSection(
    ownerName: String,
    ownerPhone: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, MaloFindCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = MaloFindGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(23.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Owner",
                    tint = MaloFindGreen
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ownerName,
                    color = MaloFindTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = ownerPhone,
                    color = MaloFindTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PropertyNotFoundScreen(
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
        Text(
            text = "Property Not Found",
            color = MaloFindTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The property listing you are looking for is no longer available.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaloFindGreen
            )
        ) {
            Text("Go Back")
        }
    }
}

private fun formatDetailsPrice(property: Property): String {
    val formattedPrice = String.format("%,.0f", property.price)
    return if (property.listingType.equals("For Rent", ignoreCase = true)) {
        "MWK $formattedPrice / month"
    } else {
        "MWK $formattedPrice"
    }
}
