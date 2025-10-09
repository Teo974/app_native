package com.example.aplicaciones_nativas.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicaciones_nativas.R
import com.example.aplicaciones_nativas.ui.screens.viewmodel.AddMomentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMomentScreen(navController: NavController, viewModel: AddMomentViewModel = hiltViewModel()) {
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val location by viewModel.location.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                viewModel.fetchLocation()
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.add_moment)) })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 140) description = it },
                label = { Text(stringResource(id = R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${description.length} / 140") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "${stringResource(id = R.string.location)}: $location")
            Spacer(modifier = Modifier.height(8.dp))
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text(text = sdf.format(Date()))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text(stringResource(id = R.string.select_image))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    imageUri?.let {
                        viewModel.addMoment(it, description)
                        navController.popBackStack()
                    }
                },
                enabled = imageUri != null
            ) {
                Text(stringResource(id = R.string.add_moment))
            }
        }
    }
}
