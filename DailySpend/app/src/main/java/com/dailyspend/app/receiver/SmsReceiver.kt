package com.dailyspend.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import com.dailyspend.app.DailySpendApp
import com.dailyspend.app.R
import com.dailyspend.app.data.local.ExpenseEntity
import com.dailyspend.app.parser.SmsExpenseParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val app = context.applicationContext as? DailySpendApp ?: return
        val secureStorage = app.secureStorage

        // Ensure user is activated and auto-scan is enabled
        if (!secureStorage.isActivated() || !secureStorage.isAutoScanEnabled()) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val fullBodyBuilder = StringBuilder()
        for (sms in messages) {
            fullBodyBuilder.append(sms.displayMessageBody)
        }
        val fullSms = fullBodyBuilder.toString()

        // Parse SMS through USA Bank Parser
        val parsed = SmsExpenseParser.parse(fullSms) ?: return

        val now = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val todayStr = dateFormat.format(now)
        val timeStr = timeFormat.format(now)

        val expense = ExpenseEntity(
            amount = parsed.amount,
            merchant = parsed.merchant,
            bank = parsed.bank,
            category = parsed.category,
            date = todayStr,
            time = timeStr,
            fullSms = fullSms,
            isAuto = true
        )

        // Asynchronously save to Room Database even when app is killed
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.database.expenseDao().insert(expense)

                // Optional silent notification
                if (secureStorage.isNotificationEnabled()) {
                    showNotification(context, expense)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, expense: ExpenseEntity) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val formattedAmount = String.format(Locale.US, "$%.2f", expense.amount)

        val notification = NotificationCompat.Builder(context, DailySpendApp.CHANNEL_ID_EXPENSES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Added $formattedAmount at ${expense.merchant}")
            .setContentText("${expense.bank} · Auto-logged to daily report")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
