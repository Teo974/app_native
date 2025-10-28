package com.buenosaires.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.remember
import com.buenosaires.connect.designsystem.BuenosAiresConnectTheme
import com.buenosaires.connect.features.chat.presentation.ChatConversationScreen
import com.buenosaires.connect.features.chat.presentation.ChatListScreen
import com.buenosaires.connect.features.chat.presentation.viewmodel.ChatViewModel
import com.buenosaires.connect.features.experiences.presentation.AddMomentScreen
import com.buenosaires.connect.features.experiences.presentation.EditMomentScreen
import com.buenosaires.connect.features.experiences.presentation.HomeScreen
import com.buenosaires.connect.features.experiences.presentation.detail.MomentDetailScreen
import com.buenosaires.connect.features.onboarding.presentation.AuthScreen
import com.buenosaires.connect.features.onboarding.presentation.LoginScreen
import com.buenosaires.connect.features.onboarding.presentation.SignUpScreen
import com.buenosaires.connect.features.root.presentation.viewmodel.MainViewModel
import com.buenosaires.connect.features.settings.presentation.SettingsScreen
import com.buenosaires.connect.features.profile.presentation.ProfileScreen
import com.buenosaires.connect.features.events.presentation.EventsScreen
import com.buenosaires.connect.features.events.presentation.EventDetailScreen
import com.buenosaires.connect.features.events.presentation.AddEventScreen
import com.buenosaires.connect.features.settings.presentation.viewmodel.LanguageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val languageViewModel: LanguageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageViewModel.loadLanguage(this)
        enableEdgeToEdge()
        setContent {
            BuenosAiresConnectTheme {
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
        NavHost(navController = navController, startDestination = "auth") { // Start at AuthScreen
            composable("auth") {
                AuthScreen(navController = navController)
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            composable("registration") { // This is now SignUpScreen
                SignUpScreen(
                    navController = navController, // Pass navController
                    onSignUpSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("chat") { backStackEntry ->
                val chatViewModel: ChatViewModel = hiltViewModel(backStackEntry)
                ChatListScreen(navController = navController, viewModel = chatViewModel)
            }
            composable(
                "chat/{contact}",
                arguments = listOf(navArgument("contact") { type = NavType.StringType })
            ) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("chat") }
                val chatViewModel: ChatViewModel = hiltViewModel(parentEntry)
                val contact = entry.arguments?.getString("contact") ?: return@composable
                ChatConversationScreen(navController = navController, contact = contact, viewModel = chatViewModel)
            }
            composable("events") {
                EventsScreen(navController = navController)
            }
            composable(
                "event_detail/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                EventDetailScreen(navController = navController, eventId = eventId)
            }
            composable("add_event") {
                AddEventScreen(navController = navController)
            }
            composable(
                "moment_detail/{momentId}",
                arguments = listOf(navArgument("momentId") { type = NavType.LongType })
            ) {
                MomentDetailScreen(navController = navController)
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
            composable("profile") {
                ProfileScreen(navController = navController)
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
