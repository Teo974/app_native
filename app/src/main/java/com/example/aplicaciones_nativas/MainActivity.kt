package com.example.aplicaciones_nativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicaciones_nativas.ui.screens.AddMomentScreen
import com.example.aplicaciones_nativas.ui.screens.EditMomentScreen
import com.example.aplicaciones_nativas.ui.screens.HomeScreen
import com.example.aplicaciones_nativas.ui.screens.RegistrationScreen
import com.example.aplicaciones_nativas.ui.screens.SettingsScreen
import com.example.aplicaciones_nativas.ui.screens.viewmodel.MainViewModel
import com.example.aplicaciones_nativas.ui.screens.viewmodel.RegistrationViewModel
import com.example.aplicaciones_nativas.ui.theme.Aplicaciones_nativasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Aplicaciones_nativasTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val startDestination by mainViewModel.startDestination.collectAsState()

    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("registration") {
                val registrationViewModel: RegistrationViewModel = hiltViewModel()
                RegistrationScreen { username, email, password ->
                    registrationViewModel.registerUser(username, email, password)
                    navController.navigate("home") {
                        popUpTo("registration") { inclusive = true }
                    }
                }
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("add_moment") {
                AddMomentScreen(navController = navController)
            }
            composable(
                "edit_moment/{momentId}",
                arguments = listOf(navArgument("momentId") { type = NavType.LongType })
            ) {
                EditMomentScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
