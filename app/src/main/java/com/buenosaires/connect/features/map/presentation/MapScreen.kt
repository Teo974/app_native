package com.buenosaires.connect.features.map.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.buenosaires.connect.R
import com.buenosaires.connect.designsystem.CityBackdrops
import com.buenosaires.connect.designsystem.components.BuenosAiresBottomBar
import com.buenosaires.connect.designsystem.components.CityBackgroundScaffold
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private data class MapPoint(
    val latitude: Double,
    val longitude: Double,
    val titleRes: Int
)

private val mapPoints = listOf(
    MapPoint(-34.6345, -58.3630, R.string.map_highlight_loboca_title),
    MapPoint(-34.5880, -58.4307, R.string.map_highlight_palermo_title),
    MapPoint(-34.5889, -58.3925, R.string.map_highlight_recoleta_title)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val preferences = context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, preferences)
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val buenosAires = remember { GeoPoint(-34.6037, -58.3816) }
    val mapView = remember(context) {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(buenosAires)
            minZoomLevel = 3.0
            maxZoomLevel = 19.0
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    CityBackgroundScaffold(
        imageUrl = CityBackdrops.MAP,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.map_title)) })
        },
        bottomBar = { BuenosAiresBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(id = R.string.map_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        factory = { mapView },
                        update = { map ->
                            map.controller.setZoom(12.0)
                            map.controller.setCenter(buenosAires)
                            if (map.overlays.none { it is Marker }) {
                                val centerTitle = context.getString(R.string.map_marker_center)
                                map.overlays.add(createMarker(map, buenosAires, centerTitle))
                                mapPoints.forEach { point ->
                                    val geoPoint = GeoPoint(point.latitude, point.longitude)
                                    val title = context.getString(point.titleRes)
                                    map.overlays.add(createMarker(map, geoPoint, title))
                                }
                                map.invalidate()
                            }
                        }
                    )
                }
            }
            HighlightCard(
                title = stringResource(id = R.string.map_highlight_loboca_title),
                description = stringResource(id = R.string.map_highlight_loboca_description)
            )
            HighlightCard(
                title = stringResource(id = R.string.map_highlight_palermo_title),
                description = stringResource(id = R.string.map_highlight_palermo_description)
            )
            HighlightCard(
                title = stringResource(id = R.string.map_highlight_recoleta_title),
                description = stringResource(id = R.string.map_highlight_recoleta_description)
            )
        }
    }
}

@Composable
private fun HighlightCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null)
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun createMarker(mapView: MapView, position: GeoPoint, title: String): Marker =
    Marker(mapView).apply {
        this.position = position
        this.title = title
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
