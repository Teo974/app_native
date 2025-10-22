package com.buenosaires.connect.features.experiences.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.buenosaires.connect.R
import com.buenosaires.connect.designsystem.CityBackdrops
import com.buenosaires.connect.designsystem.components.CityBackgroundScaffold
import com.buenosaires.connect.features.experiences.presentation.viewmodel.EditMomentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMomentScreen(
    navController: NavController,
    viewModel: EditMomentViewModel = hiltViewModel()
) {
    val momentState by viewModel.moment.collectAsState()
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(momentState) {
        momentState?.let { moment ->
            description = moment.description
            location = moment.location
            imageUri = moment.imageUri
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateImage(uri)
            imageUri = uri.toString()
        }
    }

    CityBackgroundScaffold(
        imageUrl = CityBackdrops.EXPERIENCES,
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.edit_moment_title)) })
        }
    ) { padding ->
        momentState?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.edit_moment_headline),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start
                        )
                        imageUri?.let { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text(stringResource(id = R.string.edit_moment_update_image))
                        }
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(stringResource(id = R.string.description)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(stringResource(id = R.string.location)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.updateMoment(description, location)
                                navController.popBackStack()
                            }
                        ) {
                            Text(stringResource(id = R.string.save_changes))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
