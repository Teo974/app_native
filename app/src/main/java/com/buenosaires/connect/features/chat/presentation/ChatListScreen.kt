package com.buenosaires.connect.features.chat.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buenosaires.connect.R
import com.buenosaires.connect.designsystem.CityBackdrops
import com.buenosaires.connect.designsystem.components.BuenosAiresBottomBar
import com.buenosaires.connect.designsystem.components.CityBackgroundScaffold
import com.buenosaires.connect.features.chat.presentation.viewmodel.ChatPreview
import com.buenosaires.connect.features.chat.presentation.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val previews by viewModel.previews.collectAsState()

    CityBackgroundScaffold(
        imageUrl = CityBackdrops.CHAT,
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.chat_title)) }) },
        bottomBar = { BuenosAiresBottomBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(previews) { preview ->
                ChatPreviewCard(preview = preview) {
                    navController.navigate("chat/${preview.contact}")
                }
            }
        }
    }
}

@Composable
private fun ChatPreviewCard(preview: ChatPreview, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = preview.contact, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = preview.lastTime, style = MaterialTheme.typography.labelSmall)
            }
            Text(text = preview.lastMessage, style = MaterialTheme.typography.bodyMedium)
            if (preview.unreadCount > 0) {
                val unreadText = if (preview.unreadCount == 1) {
                    stringResource(id = R.string.chat_unread_single)
                } else {
                    stringResource(id = R.string.chat_unread_plural, preview.unreadCount)
                }
                Text(
                    text = unreadText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
