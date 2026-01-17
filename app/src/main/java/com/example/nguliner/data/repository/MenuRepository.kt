package com.example.nguliner.data.repository

import com.example.nguliner.data.model.Menu
import com.example.nguliner.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // 1. Simpan Menu Baru
    suspend fun addMenuToFirestore(
        nama: String,
        harga: Int,
        deskripsi: String,
        imageUrl: String,
        kategori: String
    ): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("User belum login!")
            val menuId = firestore.collection("menus").document().id
            val newMenu = Menu(menuId, user.uid, nama, harga, deskripsi, imageUrl, kategori)

            firestore.collection("menus").document(menuId).set(newMenu).await()
            Result.success("Menu berhasil disimpan!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Ambil Semua Warung (Untuk Guest)
    suspend fun getAllShops(): Result<List<User>> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            val shops = snapshot.toObjects(User::class.java)
            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Ambil Menu berdasarkan User ID (Untuk Mitra & Shop Detail Guest)
    suspend fun getMenusByUser(userId: String): Result<List<Menu>> {
        return try {
            val snapshot = firestore.collection("menus")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val menus = snapshot.toObjects(Menu::class.java)
            Result.success(menus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Ambil 1 Menu berdasarkan ID
    suspend fun getMenuById(menuId: String): Result<Menu> {
        return try {
            val document = firestore.collection("menus").document(menuId).get().await()
            val menu = document.toObject(Menu::class.java)
            if (menu != null) {
                Result.success(menu)
            } else {
                Result.failure(Exception("Menu tidak ditemukan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Hapus Menu
    suspend fun deleteMenu(menuId: String): Result<String> {
        return try {
            firestore.collection("menus").document(menuId).delete().await()
            Result.success("Menu berhasil dihapus")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // 6. Update Menu
    suspend fun updateMenu(
        menuId: String,
        nama: String,
        harga: Int,
        deskripsi: String,
        imageUrl: String,
        kategori: String
    ): Result<String> {
        return try {
            val updateData = mapOf(
                "namaMakanan" to nama,
                "harga" to harga,
                "deskripsi" to deskripsi,
                "imageUrl" to imageUrl,
                "kategori" to kategori
            )
            firestore.collection("menus").document(menuId).update(updateData).await()
            Result.success("Menu berhasil diupdate!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}