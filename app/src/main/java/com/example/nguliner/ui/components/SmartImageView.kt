package com.example.nguliner.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun SmartImageView(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {

    val finalModifier = modifier
        .aspectRatio(1f)
        .background(Color.LightGray)

    // biar ga ketarik gambarnya
    val finalContentScale = ContentScale.Crop

    // Cek 1: Apakah ini Link Internet
    if (imageUrl.startsWith("http")) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = finalModifier,
            contentScale = finalContentScale
        )
    }
    // Cek 2: Kalau bukan link, berarti ini Base64
    else {
        //  jadi Bitmap
        val bitmap = remember(imageUrl) {
            try {
                // Hapus header
                val cleanBase64 = imageUrl.substringAfter(",")

                // Decode kode jadi bytes
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                // Ubah bytes jadi Gambar (Bitmap)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        if (bitmap != null) {
            // Kalau berhasil diterjemahin, tampilin Gambarnya
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = finalModifier,
                contentScale = finalContentScale
            )
        } else {
            // Kalau gagal
            Box(
                modifier = finalModifier,
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}