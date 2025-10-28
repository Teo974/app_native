package com.buenosaires.connect.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ConfirmationNumber // Changed from Event to ConfirmationNumber
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.buenosaires.connect.R

private data class BottomNavDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

private val destinations = listOf(
    BottomNavDestination("home", R.string.nav_home, Icons.Filled.Home),
    BottomNavDestination("events", R.string.nav_events, Icons.Filled.ConfirmationNumber), // Moved Events up
    BottomNavDestination("chat", R.string.nav_chat, Icons.AutoMirrored.Filled.Chat), // Moved Chat down
    BottomNavDestination("profile", R.string.nav_profile, Icons.Filled.AccountCircle)
)

@Composable
fun BuenosAiresBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    NavigationBar {
        destinations.forEach { destination ->
            val selected = when (destination.route) {
                "home" -> currentRoute.startsWith("home") ||
                    currentRoute.startsWith("event_detail") ||
                    currentRoute.startsWith("add_moment") ||
                    currentRoute.startsWith("edit_moment")
                "events" -> currentRoute.startsWith("events") // Updated selection logic for events
                "chat" -> currentRoute.startsWith("chat")
                "profile" -> currentRoute.startsWith("profile")
                else -> currentRoute == destination.route
            }
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(destination.icon, contentDescription = stringResource(id = destination.labelRes))
                },
                label = {
                    Text(text = stringResource(id = destination.labelRes))
                }
            )
        }
    }
}
