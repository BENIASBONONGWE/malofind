package com.example.malofind.ui.theme.screens.seeker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.data.repository.UserProfile
import com.example.malofind.ui.theme.MaloFindBackground
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerProfileScreen(
    onBack: () -> Unit,
    onRequestsClick: () -> Unit = {},
    onFavouritesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    repository: AuthRepository = AuthRepository()
) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(Unit) {
        val result = repository.getCurrentUserProfile()
        result.onSuccess { profile ->
            userProfile = profile
        }
    }

    Scaffold(
        containerColor = MaloFindBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(18.dp)
        ) {

            ProfileHeader(
                name = userProfile?.fullName?.takeIf { it.isNotBlank() } ?: "MaloFind User",
                role = when (userProfile?.accountType) {
                    "SEEKER" -> "Property Seeker"
                    "OWNER" -> "Property Owner"
                    "ADMIN" -> "Administrator"
                    else -> "Property Seeker"
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInformation(
                email = userProfile?.email?.takeIf { it.isNotBlank() } ?: "Not provided",
                phone = userProfile?.phone?.takeIf { it.isNotBlank() } ?: "Not provided"
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Account",
                color = MaloFindTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                icon = Icons.Default.Home,
                title = "My Requests",
                subtitle = "View and track your property requests",
                onClick = onRequestsClick
            )

            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "Favourites",
                subtitle = "View your saved properties",
                onClick = onFavouritesClick
            )

            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "View your latest notifications",
                onClick = onNotificationsClick
            )

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "Manage your app preferences",
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    repository.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaloFindGreen
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Log Out",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    role: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaloFindGreen
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaloFindGold,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaloFindGreen,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = name,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = role,
                color = Color.White.copy(alpha = 0.80f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ProfileInformation(
    email: String,
    phone: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProfileDetailRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = email
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProfileDetailRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = phone
            )
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaloFindGreen,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = label,
                color = MaloFindTextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = MaloFindTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaloFindGreen,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MaloFindTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = MaloFindTextSecondary,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = MaloFindTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
