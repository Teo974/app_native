package com.buenosaires.connect.features.events.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.buenosaires.connect.R
import com.buenosaires.connect.designsystem.components.BuenosAiresBottomBar
import com.buenosaires.connect.features.events.data.Event
import com.buenosaires.connect.features.events.data.EventStatus
import com.buenosaires.connect.features.events.presentation.viewmodel.EventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isAdmin = viewModel.isAdmin("admin123") // TODO: Replace with actual logged-in user ID

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(id = R.string.nav_events), color = Color.Black)
                        Text(stringResource(id = R.string.events_tagline), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { BuenosAiresBottomBar(navController) },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { navController.navigate("add_event") }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event_fab_content_description))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                label = { Text(stringResource(id = R.string.event_search_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Filters
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category Filter (Mock categories for now)
                val categories = listOf("Música", "Cultura", "Deportes", "Gastronomía")
                var expandedCategory by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCategory ?: stringResource(id = R.string.event_category_all),
                        onValueChange = {{}},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.event_category_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(id = R.string.event_category_all)) }, onClick = {
                            viewModel.onCategorySelected(null)
                            expandedCategory = false
                        })
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category) }, onClick = {
                                viewModel.onCategorySelected(category)
                                expandedCategory = false
                            })
                        }
                    }
                }

                // Status Filter
                val statuses = EventStatus.values().toList()
                var expandedStatus by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedStatus?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: stringResource(id = R.string.event_status_all),
                        onValueChange = {{}},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.event_status_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(id = R.string.event_status_all)) }, onClick = {
                            viewModel.onStatusSelected(null)
                            expandedStatus = false
                        })
                        statuses.forEach { status ->
                            DropdownMenuItem(text = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) }, onClick = {
                                viewModel.onStatusSelected(status)
                                expandedStatus = false
                            })
                        }
                    }
                }
            }

            // Event List
            if (events.isEmpty()) {
                Text(text = stringResource(id = R.string.event_no_events))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(events) { event ->
                        EventCard(event = event, viewModel = viewModel) {
                            navController.navigate("event_detail/${event.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, viewModel: EventsViewModel, onCardClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onCardClick(event) }
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = event.imageUrl ?: event.imageResId, // Use imageUrl first, then imageResId
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.event_status_prefix, event.status.name.lowercase().replaceFirstChar { it.uppercase() }) ,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.subscribeToEvent(event.id, "currentUserId") }, modifier = Modifier.fillMaxWidth()) { // TODO: Get actual user ID
                Text(stringResource(id = R.string.event_subscribe_button))
            }
        }
    }
}
