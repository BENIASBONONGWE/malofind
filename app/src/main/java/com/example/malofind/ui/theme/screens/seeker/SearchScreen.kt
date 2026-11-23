package com.example.malofind.ui.theme.screens.seeker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.malofind.ui.components.FilterChipRow
import com.example.malofind.ui.components.MaloFindSearchBar
import com.example.malofind.ui.components.PropertyCard
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit = {},
    propertyRepository: PropertyRepository = PropertyRepository(),
    favouritesRepository: FavouritesRepository = FavouritesRepository(),
    authRepository: AuthRepository = AuthRepository()
) {
    var properties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var favouriteIds by remember { mutableStateOf(setOf<String>()) }
    val userId = authRepository.getCurrentUserUid() ?: ""
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        properties = propertyRepository.getApprovedProperties()
        if (userId.isNotBlank()) {
            favouriteIds = favouritesRepository.getFavouritePropertyIds(userId)
        }
    }

    val filteredProperties = remember(
        properties,
        searchQuery,
        selectedFilter
    ) {
        properties.filter { property ->
            val matchesSearch = searchQuery.isBlank() ||
                    property.title.contains(searchQuery, ignoreCase = true) ||
                    property.location.contains(searchQuery, ignoreCase = true) ||
                    property.city.contains(searchQuery, ignoreCase = true) ||
                    property.propertyType.contains(searchQuery, ignoreCase = true)

            val matchesFilter = selectedFilter == "All" ||
                    property.listingType.equals(selectedFilter, ignoreCase = true) ||
                    property.propertyType.equals(selectedFilter, ignoreCase = true)

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Properties",
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
            item { SearchIntroduction() }

            item {
                MaloFindSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search by location, house or property type..."
                )
            }

            item {
                FilterChipRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            item {
                Text(
                    text = "${filteredProperties.size} properties found",
                    color = MaloFindTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (filteredProperties.isEmpty()) {
                item { SearchEmptyState() }
            } else {
                items(
                    items = filteredProperties,
                    key = { property -> property.id }
                ) { property ->
                    SearchPropertyCard(
                        property = property,
                        isFavourite = property.id in favouriteIds,
                        onFavouriteClick = {
                            if (userId.isNotBlank()) {
                                scope.launch {
                                    val isNowFav = favouritesRepository.toggleFavourite(userId, property.id)
                                    favouriteIds = if (isNowFav) {
                                        favouriteIds + property.id
                                    } else {
                                        favouriteIds - property.id
                                    }
                                }
                            } else {
                                favouriteIds = if (property.id in favouriteIds) {
                                    favouriteIds - property.id
                                } else {
                                    favouriteIds + property.id
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
private fun SearchIntroduction() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Find a Property",
            color = MaloFindTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Search for houses, apartments and rooms available across Malawi.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun SearchPropertyCard(
    property: Property,
    isFavourite: Boolean,
    onFavouriteClick: () -> Unit,
    onClick: () -> Unit
) {
    PropertyCard(
        title = property.title,
        location = "${property.location}, ${property.city}",
        price = formatSearchPrice(property),
        propertyType = property.listingType,
        bedrooms = property.bedrooms,
        imageUrl = property.imageUrl.ifBlank { property.imageUrls.firstOrNull() ?: "" },
        isFavourite = isFavourite,
        onFavouriteClick = onFavouriteClick,
        onClick = onClick
    )
}

@Composable
private fun SearchEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 45.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "No properties",
            tint = MaloFindGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "No properties found",
            color = MaloFindTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try another location, property type or listing type.",
            color = MaloFindTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

private fun formatSearchPrice(property: Property): String {
    val formattedPrice = String.format("%,.0f", property.price)
    return if (property.listingType.equals("For Rent", ignoreCase = true)) {
        "MWK $formattedPrice / month"
    } else {
        "MWK $formattedPrice"
    }
}
