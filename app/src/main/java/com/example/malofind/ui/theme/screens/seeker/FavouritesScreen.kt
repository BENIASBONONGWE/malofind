package com.example.malofind.ui.theme.screens.seeker

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.model.Property
import com.example.malofind.data.model.PropertyRepository
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.FavouritesRepository
import com.example.malofind.ui.components.PropertyCard
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    favouritesRepository: FavouritesRepository = FavouritesRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var favouriteProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val userId = authRepository.getCurrentUserUid() ?: ""
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        isLoading = true
        if (userId.isNotBlank()) {
            val favIds = favouritesRepository.getFavouritePropertyIds(userId)
            val allApproved = propertyRepository.getApprovedProperties()
            favouriteProperties = allApproved.filter { it.id in favIds }
        }
        isLoading = false
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favourites",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaloFindGreen
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
        } else if (favouriteProperties.isEmpty()) {
            EmptyFavouritesState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onBrowse = onBack
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FavouritesHeader(count = favouriteProperties.size)
                }

                items(
                    items = favouriteProperties,
                    key = { property -> property.id }
                ) { property ->
                    FavouritePropertyCard(
                        property = property,
                        onRemove = {
                            if (userId.isNotBlank()) {
                                scope.launch {
                                    favouritesRepository.toggleFavourite(userId, property.id)
                                    favouriteProperties = favouriteProperties.filter { it.id != property.id }
                                }
                            }
                        },
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavouritesHeader(count: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favourites",
                tint = MaloFindGold,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.size(9.dp))

            Text(
                text = "Saved Properties",
                color = MaloFindTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$count properties saved for later.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FavouritePropertyCard(
    property: Property,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(18.dp)
            )
    ) {
        PropertyCard(
            title = property.title,
            location = "${property.location}, ${property.city}",
            price = formatFavouritePrice(property),
            propertyType = property.listingType,
            bedrooms = property.bedrooms,
            imageUrl = property.imageUrl.ifBlank { property.imageUrls.firstOrNull() ?: "" },
            isFavourite = true,
            onFavouriteClick = onRemove,
            onClick = onClick
        )
    }
}

@Composable
private fun EmptyFavouritesState(
    modifier: Modifier = Modifier,
    onBrowse: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "No favourites",
            tint = MaloFindGold,
            modifier = Modifier.size(75.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "No Favourites Yet",
            color = MaloFindTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Properties you save will appear here so you can easily find them again.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onBrowse,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaloFindGreen
            )
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Browse properties"
            )

            Spacer(modifier = Modifier.size(7.dp))

            Text(text = "Browse Properties")
        }
    }
}

private fun formatFavouritePrice(property: Property): String {
    val formattedPrice = String.format("%,.0f", property.price)
    return if (property.listingType.equals("For Rent", ignoreCase = true)) {
        "MWK $formattedPrice / month"
    } else {
        "MWK $formattedPrice"
    }
}
