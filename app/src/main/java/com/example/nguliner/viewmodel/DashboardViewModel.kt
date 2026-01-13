package com.example.nguliner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nguliner.data.model.Menu
import com.example.nguliner.data.model.User
import com.example.nguliner.data.repository.MenuRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = MenuRepository()
    private val auth = FirebaseAuth.getInstance()

    var menus by mutableStateOf<List<Menu>>(emptyList())
        private set
    var shops by mutableStateOf<List<User>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var isUserLoggedIn by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")

    init {
        checkLoginStatus()
        fetchData()
    }

    fun checkLoginStatus() {
        isUserLoggedIn = auth.currentUser != null
    }

    fun fetchData() {
        viewModelScope.launch {
            isLoading = true
            checkLoginStatus()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Mitra: Ambil Menu
                val result = repository.getMenusByUser(currentUser.uid)
                if (result.isSuccess) menus = result.getOrDefault(emptyList())
            } else {
                // Guest: Ambil Warung
                val result = repository.getAllShops()
                if (result.isSuccess) shops = result.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }
}