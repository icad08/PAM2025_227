package com.example.nguliner.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.repository.MenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MenuRepository()
    private val context = application.applicationContext

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)
        private set

    fun addMenu(nama: String, hargaStr: String, deskripsi: String, imageUri: Uri?) {
        if (nama.isBlank() || hargaStr.isBlank() || deskripsi.isBlank() || imageUri == null) {
            errorMessage = "Mohon lengkapi semua data & foto!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false

            // Foto jadi teks
            val base64Image = encodeImageToBase64(imageUri)

            if (base64Image != null) {
                val hargaInt = hargaStr.toIntOrNull() ?: 0

                // foto tersimpan sebagai teks
                val saveResult = repository.addMenuToFirestore(
                    nama = nama,
                    harga = hargaInt,
                    deskripsi = deskripsi,
                    imageUrl = base64Image // Masukin kode fotonya disini
                )

                if (saveResult.isSuccess) {
                    isSuccess = true
                } else {
                    errorMessage = "Gagal simpan: ${saveResult.exceptionOrNull()?.message}"
                }
            } else {
                errorMessage = "Gagal memproses foto. Coba foto lain."
            }

            isLoading = false
        }
    }

    // kompress foto, ubah menjadi string
    private suspend fun encodeImageToBase64(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Baca file foto dari Uri
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // 2. ngecilin ukuran
                // Kita resize jadi lebar max 600px
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true)

                // 3. Ubah jadi kode Base64
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Kualitas 70%
                val byteArray = outputStream.toByteArray()

                // Tambahin kode header biar bisa dibaca nanti
                "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun resetState() {
        errorMessage = null
        isSuccess = false
        isLoading = false
    }
}