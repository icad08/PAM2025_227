package com.example.nguliner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.repository.MenuRepository
import kotlinx.coroutines.launch

class FormMenuViewModel : ViewModel() {
    private val repository = MenuRepository()

    var isLoading by mutableStateOf(false)
        private set

    var formState by mutableStateOf<Result<String>?>(null)
        private set

    // Data Form
    var nama by mutableStateOf("")
    var harga by mutableStateOf("")
    var deskripsi by mutableStateOf("")
    var imageUrl by mutableStateOf("")

    var kategori by mutableStateOf("Makanan")

    fun loadMenuData(menuId: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getMenuById(menuId)
            result.onSuccess { menu ->
                nama = menu.namaMakanan
                harga = menu.harga.toString()
                deskripsi = menu.deskripsi
                imageUrl = menu.imageUrl

                kategori = if (menu.kategori.isNotEmpty()) menu.kategori else "Makanan"
            }
            isLoading = false
        }
    }

    fun saveMenu(menuId: String?) {
        viewModelScope.launch {
            isLoading = true
            val hargaInt = harga.toIntOrNull() ?: 0

            if (menuId == null) {
                formState = repository.addMenuToFirestore(nama, hargaInt, deskripsi, imageUrl, kategori)
            } else {
                formState = repository.updateMenu(menuId, nama, hargaInt, deskripsi, imageUrl, kategori)
            }
            isLoading = false
        }
    }

    fun resetState() {
        formState = null
    }
}