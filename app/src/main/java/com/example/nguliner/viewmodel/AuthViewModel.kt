package com.example.nguliner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set

    var authResult by mutableStateOf<Result<String>?>(null)
        private set

    fun register(nama: String, email: String, kataSandi: String, alamat: String) {
        viewModelScope.launch {
            isLoading = true

            authResult = repository.register(nama, email, kataSandi, alamat)
            isLoading = false
        }
    }

    fun login(email: String, kataSandi: String) {
        viewModelScope.launch {
            isLoading = true
            authResult = repository.login(email, kataSandi)
            isLoading = false
        }
    }

    fun resetState() {
        authResult = null
    }
}