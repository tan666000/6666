// MapScreen.kt
package com.steadywj.wjfakelocation.manager.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.steadywj.wjfakelocation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    var showDrawer by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nav_map)) },
                navigationIcon = {
                    IconButton(onClick = { showDrawer = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "菜单")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 搜索功能 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* 定位到当前位�?*/ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "当前位置")
            }
        }
    ) { paddingValues ->
        // 集成高德地图 MapView
        AMapView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            initialLatitude = currentLat,
            initialLongitude = currentLng,
            zoomLevel = zoomLevel,
            onMapReady = { aMap ->
                Log.d("MapScreen", "高德地图加载完成")
            },
            onMapClick = { lat, lng ->
                // 点击地图回调
                onLocationSelected?.invoke(lat, lng)
            }
        )
    }
}
