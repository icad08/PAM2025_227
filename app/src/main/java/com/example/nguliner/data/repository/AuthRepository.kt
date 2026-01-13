package com.example.nguliner.data.repository

import com.example.nguliner.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Fungsi Login
    suspend fun login(email: String, kataSandi: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, kataSandi).await()
            Result.success("Login Berhasil")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi Register
    suspend fun register(nama: String, email: String, kataSandi: String, alamat: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, kataSandi).await()
            val userId = authResult.user?.uid ?: throw Exception("Gagal mendapatkan User ID")

            val newUser = User(
                id = userId,
                name = nama,
                email = email,
                address = alamat
            )

            firestore.collection("users").document(userId).set(newUser).await()
            Result.success("Register Berhasil")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateUserProfile(userId: String, nama: String, alamat: String): Result<String> {
        return try {
            val updateData = mapOf(
                "name" to nama,
                "address" to alamat
            )
            firestore.collection("users").document(userId).update(updateData).await()
            Result.success("Profil berhasil diupdate")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //  Ambil Data User (Buat ditampilin di form edit)
    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}