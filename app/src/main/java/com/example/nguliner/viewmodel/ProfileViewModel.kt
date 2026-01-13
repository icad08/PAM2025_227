package com.example.nguliner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    var name by mutableStateOf("")
    var address by mutableStateOf("")
    var email by mutableStateOf("") // Email cuma buat baca, gak diedit

    var isLoading by mutableStateOf(false)
        private set
    var statusMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                isLoading = true
                val result = repository.getUserData(currentUser.uid)
                result.onSuccess { user ->
                    name = user.name
                    address = user.address
                    email = user.email
                }
                isLoading = false
            }
        }
    }

    fun saveProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                isLoading = true
                val result = repository.updateUserProfile(currentUser.uid, name, address)
                statusMessage = result.getOrNull() ?: "Gagal update"
                isLoading = false
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun resetMessage() {
        statusMessage = null
    }
}