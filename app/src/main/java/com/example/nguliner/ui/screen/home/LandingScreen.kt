package com.example.nguliner.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nguliner.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LandingScreen(
    onMitraClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NGULINER",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Temukan jajanan enak di sekitarmu!")

        Spacer(modifier = Modifier.height(48.dp))

        // Tombol Mitra
        Button(
            onClick = onMitraClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
        ) {
            Text("Masuk sebagai Mitra Warung")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Guest
        OutlinedButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onGuestClick()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Lihat-lihat aja (Tamu)", color = Color(0xFF8B4513))
        }
    }
}