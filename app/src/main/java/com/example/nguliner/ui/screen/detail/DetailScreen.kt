package com.example.nguliner.ui.screen.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nguliner.ui.components.SmartImageView
import com.example.nguliner.utils.CurrencyUtils
import com.example.nguliner.viewmodel.DetailViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    menuId: String,
    navigateBack: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(menuId) {
        viewModel.getMenu(menuId)
    }

    LaunchedEffect(viewModel.deleteState) {
        viewModel.deleteState?.onSuccess {
            Toast.makeText(context, "Menu berhasil dihapus!", Toast.LENGTH_SHORT).show()
            navigateBack()
        }
        viewModel.deleteState?.onFailure {
            Toast.makeText(context, "Gagal hapus: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val menu = viewModel.selectedMenu
    val isLoading = viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Menu") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Cek: Apakah User Login dan Apakah dia Pemilik Menu ini?
                    if (menu != null && currentUser != null && currentUser.uid == menu.userId) {

                        // 1. tombol edit
                        IconButton(onClick = {
                            onEditClick(menuId)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        // 2. tombol hapus
                        IconButton(onClick = {
                            viewModel.deleteMenu(menuId)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (menu != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SmartImageView(
                    imageUrl = menu.imageUrl,
                    contentDescription = menu.namaMakanan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                )

                Column(modifier = Modifier.padding(16.dp)) {

                    // label kategori
                    if (menu.kategori.isNotEmpty()) {
                        AssistChip(
                            onClick = {},
                            label = { Text(menu.kategori) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            border = null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Nama Makanan
                    Text(
                        text = menu.namaMakanan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Harga
                    Text(
                        text = CurrencyUtils.toRupiah(menu.harga),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Deskripsi:", fontWeight = FontWeight.Bold)
                    Text(
                        text = menu.deskripsi,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Menu tidak ditemukan :(")
            }
        }
    }
}