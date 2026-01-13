package com.example.nguliner.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import semua icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nguliner.data.model.Menu
import com.example.nguliner.data.model.User
import com.example.nguliner.ui.components.SmartImageView
import com.example.nguliner.viewmodel.DashboardViewModel
import com.example.nguliner.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onFabClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onShopClick: (String, String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val menuList = viewModel.menus
    val shopList = viewModel.shops
    val isLoading = viewModel.isLoading
    val isUserLoggedIn = viewModel.isUserLoggedIn
    val searchQuery = viewModel.searchQuery

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    // logika search
    val filteredMenus = menuList.filter {
        it.namaMakanan.contains(searchQuery, ignoreCase = true)
    }
    val filteredShops = shopList.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isUserLoggedIn) "Warung Saya" else "Daftar Warung") },
                actions = {
                    // ikon profile
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (isUserLoggedIn) {
                FloatingActionButton(onClick = onFabClick) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Menu")
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {

                // search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(if (isUserLoggedIn) "Cari menu..." else "Cari nama warung...")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // list menu warung
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isUserLoggedIn) {
                        // MITRA VIEW
                        if (filteredMenus.isEmpty()) {
                            item { Text("Menu tidak ditemukan.", Modifier.padding(8.dp)) }
                        } else {
                            items(filteredMenus) { menu ->
                                MenuItemCustom(menu = menu, onClick = { onItemClick(menu.id) })
                            }
                        }
                    } else {
                        // GUEST VIEW
                        if (filteredShops.isEmpty()) {
                            item { Text("Warung tidak ditemukan.", Modifier.padding(8.dp)) }
                        } else {
                            items(filteredShops) { shop ->
                                ShopItemCustom(shop = shop, onClick = { onShopClick(shop.id, shop.name) })
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MenuItemCustom(menu: Menu, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmartImageView(
                imageUrl = menu.imageUrl,
                contentDescription = menu.namaMakanan,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = menu.namaMakanan, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Text(
                    text = CurrencyUtils.toRupiah(menu.harga),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Text(text = menu.deskripsi, fontSize = 14.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ShopItemCustom(shop: User, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
        Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Store, contentDescription = null, tint = Color(0xFF8B4513), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = shop.name.ifBlank { "Warung Tanpa Nama" }, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = shop.address.ifBlank { "Alamat tidak tersedia" }, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}