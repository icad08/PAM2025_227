package com.example.nguliner.data.model

data class Menu(
    val id: String = "",
    val userId: String = "",       // ID pemilik warung
    val namaMakanan: String = "",
    val harga: Int = 0,
    val deskripsi: String = "",
    val imageUrl: String = ""
)