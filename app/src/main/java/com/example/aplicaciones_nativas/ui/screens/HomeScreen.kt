package com.example.aplicaciones_nativas.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicaciones_nativas.R
import com.example.aplicaciones_nativas.data.Moment
import com.example.aplicaciones_nativas.service.DownloadService
import com.example.aplicaciones_nativas.ui.screens.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val moments by viewModel.moments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    var momentToDownload by remember { mutableStateOf<Moment?>(null) }

    fun startDownload(moment: Moment) {
        val intent = Intent(context, DownloadService::class.java).apply {
            putExtra("image_url", moment.imageUri)
        }
        context.startService(intent)
    }

    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            momentToDownload?.let { startDownload(it) }
        }
    }

    val requestWriteStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        momentToDownload?.let { startDownload(it) }
                    }
                    else -> {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                momentToDownload?.let { startDownload(it) }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_moment") }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_moment_fab))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text(stringResource(id = R.string.search_by_tag)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            LazyColumn {
                items(moments) { moment ->
                    MomentItem(
                        moment = moment,
                        onDelete = { viewModel.deleteMoment(moment) },
                        onMomentClick = { navController.navigate("edit_moment/${moment.id}") },
                        onMomentLongClick = {
                            momentToDownload = moment
                            when (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                            PackageManager.PERMISSION_GRANTED -> {
                                                startDownload(moment)
                                            }
                                            else -> {
                                                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        }
                                    } else {
                                        startDownload(moment)
                                    }
                                }
                                else -> {
                                    requestWriteStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MomentItem(
    moment: Moment,
    onDelete: () -> Unit,
    onMomentClick: () -> Unit,
    onMomentLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMomentClick() },
                    onLongPress = { onMomentLongClick() }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = moment.imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = moment.description, modifier = Modifier.padding(top = 8.dp))
            Text(text = moment.location, modifier = Modifier.padding(top = 4.dp))
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text(text = sdf.format(Date(moment.date)), modifier = Modifier.padding(top = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete_moment))
                }
            }
        }
    }
}
