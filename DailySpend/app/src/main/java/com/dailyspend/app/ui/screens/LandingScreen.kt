package com.dailyspend.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(
    licenseKey: String,
    onTimeout: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top verified badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp)
                .background(Color(0xFFECFDF5), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("License Verified & Locked", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF065F46))
        }

        // Center Content
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Welcome to DailySpend", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF18181B))
            Text("Automatic SMS Expense Tracker for USA Banks", fontSize = 13.sp, color = Color(0xFF71717A), modifier = Modifier.padding(top = 6.dp))

            Spacer(modifier = Modifier.height(24.dp))

            FeatureCard("Background SMS Capture", "Auto-saves bank debits even when app is killed", Icons.Default.FlashOn, Color(0xFFF59E0B))
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCard("4-Digit mPIN Security", "Fast access each time you open the app", Icons.Default.Lock, Color(0xFF10B981))
            Spacer(modifier = Modifier.height(10.dp))
            FeatureCard("100% Offline Room DB", "Private on-device encrypted database", Icons.Default.Storage, Color(0xFF3B82F6))
        }

        // Bottom progress & skip
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Continuing to account setup in 3s...", fontSize = 12.sp, color = Color(0xFFA1A1AA))
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF18181B),
                trackColor = Color(0xFFF4F4F5)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onTimeout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FeatureCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF18181B))
            Text(desc, fontSize = 11.sp, color = Color(0xFF71717A))
        }
    }
}
