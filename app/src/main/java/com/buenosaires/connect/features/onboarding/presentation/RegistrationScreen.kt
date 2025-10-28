package com.buenosaires.connect.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buenosaires.connect.R
import com.buenosaires.connect.features.onboarding.presentation.viewmodel.RegistrationViewModel
import com.buenosaires.connect.features.onboarding.presentation.viewmodel.RegistrationViewModel.RegistrationError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
   onContinueToHome: () -> Unit,
   viewModel: RegistrationViewModel = hiltViewModel()
) {
   var username by remember { mutableStateOf("") }
   var email by remember { mutableStateOf("") }
   var password by remember { mutableStateOf("") }

   val isLoading by viewModel.isLoading.collectAsState()
   val registrationCompleted by viewModel.registrationCompleted.collectAsState()
   val error by viewModel.error.collectAsState()

   LaunchedEffect(registrationCompleted) {
       if (registrationCompleted) {
           onContinueToHome()
           viewModel.acknowledgeCompletion()
       }
   }

   Scaffold(
       topBar = {
           CenterAlignedTopAppBar(
               title = { Text(text = stringResource(id = R.string.app_name), color = Color.Black) },
               colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
           )
       }
   ) { paddingValues ->
       Column(
           modifier = Modifier
               .fillMaxSize()
               .background(Color.White)
               .padding(paddingValues)
               .padding(24.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
       ) {
           Text(
               text = stringResource(id = R.string.registration_title),
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.fillMaxWidth(),
               color = Color.Black
           )
           Spacer(modifier = Modifier.height(16.dp))
           Card(
               modifier = Modifier
                   .fillMaxWidth()
                   .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
               shape = RoundedCornerShape(12.dp),
               colors = CardDefaults.cardColors(containerColor = Color.White)
           ) {
               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(24.dp),
                   verticalArrangement = Arrangement.spacedBy(12.dp)
               ) {
                   OutlinedTextField(
                       value = username,
                       onValueChange = { username = it },
                       label = { Text(stringResource(id = R.string.username)) },
                       singleLine = true,
                       modifier = Modifier.fillMaxWidth()
                   )
                   OutlinedTextField(
                       value = email,
                       onValueChange = { email = it },
                       label = { Text(stringResource(id = R.string.email)) },
                       singleLine = true,
                       modifier = Modifier.fillMaxWidth()
                   )
                   OutlinedTextField(
                       value = password,
                       onValueChange = { password = it },
                       label = { Text(stringResource(id = R.string.password)) },
                       singleLine = true,
                       visualTransformation = PasswordVisualTransformation(),
                       modifier = Modifier.fillMaxWidth()
                   )
                   error?.let { registrationError ->
                       val message = when (registrationError) {
                           RegistrationError.REQUIRED_FIELDS -> stringResource(id = R.string.registration_error_required)
                           RegistrationError.USERNAME_TAKEN -> stringResource(id = R.string.registration_error_username_taken)
                       }
                       Text(
                           text = message,
                           color = MaterialTheme.colorScheme.error,
                           style = MaterialTheme.typography.bodySmall
                       )
                   }
                   Button(
                       modifier = Modifier.fillMaxWidth(),
                       enabled = !isLoading,
                       onClick = { viewModel.registerUser(username, email, password) }
                   ) {
                       if (isLoading) {
                           CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                       } else {
                           Text(text = stringResource(id = R.string.registration_cta))
                       }
                   }
               }
           }
       }
   }
}
