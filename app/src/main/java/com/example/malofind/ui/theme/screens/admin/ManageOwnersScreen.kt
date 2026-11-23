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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.malofind.data.repository.AppUser
import com.example.malofind.data.repository.UserRepository
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOwnersScreen(
    onBack: () -> Unit,
    userRepository: UserRepository = UserRepository()
) {
    var owners by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedOwner by remember { mutableStateOf<AppUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val loadData = {
        scope.launch {
            isLoading = true
            owners = userRepository.getAllOwners()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    val filteredOwners = owners.filter { owner ->
        owner.name.contains(searchQuery, ignoreCase = true) ||
                owner.email.contains(searchQuery, ignoreCase = true) ||
                owner.phone.contains(searchQuery, ignoreCase = true)
    }

    val activeOwners = owners.count { it.status == "Active" }
    val pendingOwners = owners.count { it.status == "Pending" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage Owners", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Property Owner Management",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaloFindTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Review and manage property owners registered on MaloFind.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaloFindTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search owners...") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OwnerSummaryCard(title = "Total", value = owners.size.toString(), modifier = Modifier.weight(1f))
                OwnerSummaryCard(title = "Active", value = activeOwners.toString(), modifier = Modifier.weight(1f))
                OwnerSummaryCard(title = "Pending", value = pendingOwners.toString(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Registered Owners",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaloFindTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaloFindGreen)
                }
            } else if (filteredOwners.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No owners found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaloFindTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredOwners,
                        key = { it.id }
                    ) { owner ->
                        OwnerCard(
                            owner = owner,
                            onViewClick = { selectedOwner = owner },
                            onApproveClick = {
                                scope.launch {
                                    userRepository.updateUserStatus(owner.id, "Active")
                                    loadData()
                                }
                            },
                            onSuspendClick = {
                                scope.launch {
                                    userRepository.updateUserStatus(owner.id, "Suspended")
                                    loadData()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    selectedOwner?.let { owner ->
        AlertDialog(
            onDismissRequest = { selectedOwner = null },
            title = { Text(text = "Owner Information", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Name: ${owner.name}", fontWeight = FontWeight.SemiBold)
                    Text(text = "Email: ${owner.email}")
                    Text(text = "Phone: ${owner.phone}")
                    Text(text = "Status: ${owner.status}")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOwner = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun OwnerSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = MaloFindCardBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaloFindGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
        }
    }
}

@Composable
private fun OwnerCard(
    owner: AppUser,
    onViewClick: () -> Unit,
    onApproveClick: () -> Unit,
    onSuspendClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = MaloFindCardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = MaloFindGreen.copy(alpha = 0.12f), shape = RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Owner", tint = MaloFindGreen)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = owner.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaloFindTextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = owner.email, style = MaterialTheme.typography.bodySmall, color = MaloFindTextSecondary)
                }

                IconButton(onClick = onViewClick) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "View owner", tint = MaloFindGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Status: ${owner.status}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = when (owner.status) {
                    "Active" -> MaloFindGreen
                    "Pending" -> MaloFindGold
                    else -> Color.Gray
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (owner.status == "Pending") {
                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaloFindGreen)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Approve")
                    }
                } else {
                    Button(
                        onClick = onSuspendClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF666666))
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Suspend")
                    }
                }
            }
        }
    }
}
