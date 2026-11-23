
package com.example.malofind.screens.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.malofind.FirestoreTest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirestoreTestScreen() {

    var isLoading by remember { mutableStateOf(false) }
    var result by remember {
        mutableStateOf("Press the button to test Firestore.")
    }
    var isSuccess by remember {
        mutableStateOf<Boolean?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("MaloFind Database Test")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Cloud Firestore",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Test whether MaloFind can save data to Firebase Cloud Firestore."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    result = "Testing connection..."
                    isSuccess = null

                    FirestoreTest.testConnection { success, message ->
                        isLoading = false
                        result = message
                        isSuccess = success
                    }
                },
                enabled = !isLoading
            ) {
                Text("Test Firestore Connection")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = result,
                    color = when (isSuccess) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.error
                        null -> MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}