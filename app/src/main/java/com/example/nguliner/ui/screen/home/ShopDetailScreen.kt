package com.example.nguliner.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nguliner.data.model.Menu
import com.example.nguliner.data.repository.MenuRepository
import com.example.nguliner.ui.components.SmartImageView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    shopId: String,
    shopName: String,
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    // fecth manual
    val repository = remember { MenuRepository() }
    var menuList by remember { mutableStateOf<List<Menu>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(shopId) {
        scope.launch {
            isLoading = true
            // Ambil menu khusus warung ini
            val result = repository.getMenusByUser(shopId)
            if (result.isSuccess) {
                menuList = result.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shopName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                if (menuList.isEmpty()) {
                    item { Text("Warung ini belum punya menu.", Modifier.padding(16.dp)) }
                } else {
                    items(menuList) { menu ->
                        MenuItemCustom(menu = menu, onClick = { onItemClick(menu.id) })
                    }
                }
            }
        }
    }
}