package com.example.nguliner.ui.screen.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    menuId: String?,
    onNavigateBack: () -> Unit,
    viewModel: FormMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val formState = viewModel.formState

    // Logic Dropdown Kategori
    var expanded by remember { mutableStateOf(false) }
    val kategoriOptions = listOf("Makanan", "Minuman", "Cemilan", "Lainnya")

    LaunchedEffect(menuId) {
        if (menuId != null) viewModel.loadMenuData(menuId)
    }

    LaunchedEffect(formState) {
        formState?.onSuccess {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onNavigateBack()
        }
        formState?.onFailure {
            Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // logika pilih dan resize gambar
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            var bitmap = BitmapFactory.decodeStream(inputStream)

            val maxDimension = 800
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height

            var newWidth = originalWidth
            var newHeight = originalHeight

            if (originalWidth > maxDimension || originalHeight > maxDimension) {
                val ratio = Math.min(
                    maxDimension.toDouble() / originalWidth,
                    maxDimension.toDouble() / originalHeight
                )
                newWidth = (originalWidth * ratio).toInt()
                newHeight = (originalHeight * ratio).toInt()

                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            }

            val outputStream = ByteArrayOutputStream()
            // kompress gambar
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
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

                // 1. field gambar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                        Text(
                            text = "Ketuk untuk ganti foto",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                            Text("Klik untuk pilih foto", color = Color.Gray)
                        }
                    }
                }

                // 2. input nama
                OutlinedTextField(
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    label = { Text("Nama Makanan") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. input kategori
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = viewModel.kategori,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        kategoriOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.kategori = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // 4. input harga
                OutlinedTextField(
                    value = viewModel.harga,
                    onValueChange = { viewModel.harga = it },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // 5. input deskripsi
                OutlinedTextField(
                    value = viewModel.deskripsi,
                    onValueChange = { viewModel.deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 6. button simpan
                Button(
                    onClick = { viewModel.saveMenu(menuId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(if (menuId == null) "Simpan Menu" else "Update Menu")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}