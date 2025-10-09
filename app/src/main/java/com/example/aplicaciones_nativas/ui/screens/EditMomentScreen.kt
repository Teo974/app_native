package com.example.aplicaciones_nativas.ui.screens

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
import coil.compose.AsyncImage
import com.example.aplicaciones_nativas.R
import com.example.aplicaciones_nativas.ui.screens.viewmodel.EditMomentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMomentScreen(navController: NavController, viewModel: EditMomentViewModel = hiltViewModel()) {
    val moment by viewModel.moment.collectAsState()

    moment?.let { currentMoment ->
        var description by remember(currentMoment) { mutableStateOf(currentMoment.description) }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(id = R.string.edit_moment)) })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                AsyncImage(
                    model = currentMoment.imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 140) description = it },
                    label = { Text(stringResource(id = R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${description.length} / 140") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "${stringResource(id = R.string.location)}: ${currentMoment.location}")
                Spacer(modifier = Modifier.height(8.dp))
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                Text(text = sdf.format(Date(currentMoment.date)))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.updateMomentDescription(description)
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(id = R.string.save_changes))
                }
            }
        }
    }
}
