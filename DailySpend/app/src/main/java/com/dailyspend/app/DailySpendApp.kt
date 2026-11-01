package com.dailyspend.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.dailyspend.app.data.local.AppDatabase
import com.dailyspend.app.data.security.SecureStorageManager

class DailySpendApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var secureStorage: SecureStorageManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Room Database
        database = AppDatabase.getInstance(this)

        // Initialize EncryptedSharedPreferences (AES-256)
        secureStorage = SecureStorageManager(this)

        // Create silent notification channel for auto-logged transactions
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_EXPENSES,
                "DailySpend Transaction Alerts",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Silent notifications when bank SMS transactions are auto-logged"
                enableVibration(false)
                setShowBadge(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_EXPENSES = "dailyspend_auto_expense_channel"
        const val SUPPORT_EMAIL = "thebill.customercare@gmail.com"
        lateinit var instance: DailySpendApp
            private set
    }
}
