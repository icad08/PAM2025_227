package com.example.nguliner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.model.Menu
import com.example.nguliner.data.repository.MenuRepository
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository = MenuRepository()

    // State buat nampung data menu yang lagi dilihat
    var selectedMenu by mutableStateOf<Menu?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // State buat status hapus (sukses/gagal)
    var deleteState by mutableStateOf<Result<String>?>(null)
        private set

    // Fungsi ambil data menu berdasarkan ID
    fun getMenu(menuId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.getMenuById(menuId)
            if (result.isSuccess) {
                selectedMenu = result.getOrNull()
            } else {
                errorMessage = result.exceptionOrNull()?.message
            }

            isLoading = false
        }
    }

    // Fungsi Hapus Menu
    fun deleteMenu(menuId: String) {
        viewModelScope.launch {
            isLoading = true
            deleteState = repository.deleteMenu(menuId)
            isLoading = false
        }
    }
}