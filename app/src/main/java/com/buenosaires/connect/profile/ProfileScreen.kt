package com.buenosaires.connect.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val description by viewModel.description.collectAsState()
    val profilePictureUri by viewModel.profilePictureUri.collectAsState()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onProfilePictureClick() }
            ) {
                if (profilePictureUri != null) {
                    // In a real app, you'd load the image from the URI
                    // For now, a placeholder or a default image for demonstration
                    Image(
                        painter = rememberVectorPainter(Icons.Default.AccountCircle), // Placeholder
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 200) {
                        viewModel.onDescriptionChange(it)
                    }
                },
                label = { Text("Personal Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onSaveClick() },
                enabled = isSaveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    // For preview, we need a dummy ViewModel
    // You would typically use Hilt for injecting the real ViewModel
    val dummyViewModel = object : ProfileViewModel() {
        override val description = MutableStateFlow("This is a dummy description.")
        override val profilePictureUri = MutableStateFlow(null)
        override val isSaveEnabled = MutableStateFlow(true)

        override fun onDescriptionChange(newDescription: String) { /* Do nothing */ }
        override fun onProfilePictureClick() { /* Do nothing */ }
        override fun onSaveClick() { /* Do nothing */ }
    }
    ProfileScreen(viewModel = dummyViewModel)
}