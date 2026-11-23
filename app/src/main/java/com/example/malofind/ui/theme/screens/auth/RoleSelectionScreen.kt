package com.example.malofind.ui.theme.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.data.repository.AuthRepository
import com.example.malofind.ui.theme.MaloFindCardBorder
import com.example.malofind.ui.theme.MaloFindGold
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary
import kotlinx.coroutines.launch

private enum class AccountRole(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    SEEKER(
        title = "Property Seeker",
        description = "Find houses, rooms and other properties for rent or sale.",
        icon = Icons.Default.PersonSearch
    ),
    OWNER(
        title = "Property Owner / Agent",
        description = "List your properties and manage enquiries from potential clients.",
        icon = Icons.Default.Home
    ),
    ADMIN(
        title = "Administrator",
        description = "Manage users, property listings, approvals and reports.",
        icon = Icons.Default.AdminPanelSettings
    )
}

@Composable
fun RoleSelectionScreen(
    onContinue: (String) -> Unit,
    repository: AuthRepository = AuthRepository()
) {
    var selectedRole by remember {
        mutableStateOf<AccountRole?>(null)
    }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Role",
            color = MaloFindTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Tell us how you plan to use MaloFind",
            color = MaloFindTextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(30.dp))

        RoleCard(
            role = AccountRole.SEEKER,
            selected = selectedRole == AccountRole.SEEKER,
            onClick = { if (!isLoading) selectedRole = AccountRole.SEEKER }
        )

        Spacer(Modifier.height(14.dp))

        RoleCard(
            role = AccountRole.OWNER,
            selected = selectedRole == AccountRole.OWNER,
            onClick = { if (!isLoading) selectedRole = AccountRole.OWNER }
        )

        Spacer(Modifier.height(14.dp))

        RoleCard(
            role = AccountRole.ADMIN,
            selected = selectedRole == AccountRole.ADMIN,
            onClick = { if (!isLoading) selectedRole = AccountRole.ADMIN }
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                selectedRole?.let { role ->
                    val uid = repository.getCurrentUserUid()
                    if (uid != null) {
                        scope.launch {
                            isLoading = true
                            repository.setUserRole(uid, role.name)
                            isLoading = false
                            onContinue(role.name)
                        }
                    } else {
                        onContinue(role.name)
                    }
                }
            },
            enabled = selectedRole != null && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaloFindGreen,
                contentColor = Color.White,
                disabledContainerColor = MaloFindCardBorder,
                disabledContentColor = MaloFindTextSecondary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.height(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    role: AccountRole,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        MaloFindGreen
    } else {
        MaloFindCardBorder
    }

    val backgroundColor = if (selected) {
        MaloFindGreen.copy(alpha = 0.07f)
    } else {
        Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(18.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = if (selected) {
                        MaloFindGold
                    } else {
                        MaloFindGreen.copy(alpha = 0.10f)
                    },
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = role.icon,
                contentDescription = role.title,
                tint = MaloFindGreen,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = role.title,
                color = MaloFindTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = role.description,
                color = MaloFindTextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}
