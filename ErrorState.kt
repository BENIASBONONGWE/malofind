package com.example.malofind.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary

@Composable
fun ErrorState(
    title: String = "Something went wrong",
    message: String = "We couldn't load the information. Please check your connection and try again.",
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 48.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = "Connection error",
            tint = MaloFindGreen
        )

        Text(
            text = title,
            color = MaloFindTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = message,
            color = MaloFindTextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaloFindGreen,
                contentColor = Color.White
            )
        ) {

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry"
            )

            Text(
                text = "Retry",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}