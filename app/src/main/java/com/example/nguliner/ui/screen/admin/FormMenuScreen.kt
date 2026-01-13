package com.example.nguliner.ui.screen.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nguliner.ui.components.SmartImageView
import com.example.nguliner.viewmodel.FormMenuViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormMenuScreen(
    menuId: String?, // Parameter ID (Bisa null)
    onNavigateBack: () -> Unit,
    viewModel: FormMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val formState = viewModel.formState

    // 1. Cek Mode: Kalau ada menuId, suruh ViewModel ambil data lama
    LaunchedEffect(menuId) {
        if (menuId != null) {
            viewModel.loadMenuData(menuId)
        }
    }

    // 2. Cek Status Simpan
    LaunchedEffect(formState) {
        formState?.onSuccess {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onNavigateBack() // Balik otomatis kalau sukses
        }
        formState?.onFailure {
            Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Logic Ambil Gambar dari Galeri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Kompres & Ubah ke Base64
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

            viewModel.imageUrl = base64String
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (menuId == null) "Tambah Menu" else "Edit Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- INPUT GAMBAR ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.imageUrl.isNotEmpty()) {
                        SmartImageView(
                            imageUrl = viewModel.imageUrl,
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                            Text("Klik untuk pilih foto", color = Color.Gray)
                        }
                    }
                }

                // --- FORM INPUT ---
                OutlinedTextField(
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    label = { Text("Nama Makanan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.harga,
                    onValueChange = { viewModel.harga = it },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.deskripsi,
                    onValueChange = { viewModel.deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(
                    onClick = { viewModel.saveMenu(menuId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(if (menuId == null) "Simpan Menu" else "Update Menu")
                }
            }
        }
    }
}