package com.dailyspend.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyspend.app.DailySpendApp

@Composable
fun SupportScreen() {
    val context = LocalContext.current
    val secureStorage = DailySpendApp.instance.secureStorage
    val supportEmail = DailySpendApp.SUPPORT_EMAIL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Customer Support", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF18181B))
        Text("Official helpdesk for DailySpend USA buyers", fontSize = 12.sp, color = Color(0xFF71717A), modifier = Modifier.padding(top = 2.dp, bottom = 20.dp))

        // Direct Email Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Official Support Mail", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA1A1AA))
                Spacer(modifier = Modifier.height(4.dp))
                Text(supportEmail, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF18181B))
                Text("Typical response time: 12-24 hours", fontSize = 11.sp, color = Color(0xFF10B981), modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

                Button(
                    onClick = {
                        val license = secureStorage.getLicenseKey() ?: "N/A"
                        val deviceId = secureStorage.getAndroidId()
                        val uri = Uri.parse("mailto:$supportEmail?subject=DailySpend%20Support%20Inquiry&body=Hi%20Support,%0A%0ALicense:%20$license%0ADevice:%20$deviceId")
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18181B))
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compose Support Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FAQs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF18181B), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Frequently Asked Questions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF18181B))
                }

                Spacer(modifier = Modifier.height(12.dp))
                FaqItem("How does SMS auto-capture work?", "A BroadcastReceiver listens to Telephony.SMS_RECEIVED with priority 999 to parse bank debits even when app is closed.")
                FaqItem("Supported USA Banks", "Chase, Bank of America, Wells Fargo, American Express, Citi, and Capital One.")
                FaqItem("Device Limit", "Your Gumroad key is locked to up to 2 devices. Contact support if you switch phones.")
            }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(question, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF18181B))
        Text(answer, fontSize = 11.sp, color = Color(0xFF71717A), modifier = Modifier.padding(top = 2.dp))
    }
}
