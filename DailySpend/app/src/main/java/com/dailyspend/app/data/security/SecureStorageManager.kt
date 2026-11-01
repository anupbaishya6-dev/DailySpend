package com.dailyspend.app.data.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorageManager(private val context: Context) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "dailyspend_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Gumroad License
    fun isActivated(): Boolean = securePrefs.getBoolean(KEY_IS_ACTIVATED, false)
    fun setActivated(isActivated: Boolean) = securePrefs.edit().putBoolean(KEY_IS_ACTIVATED, isActivated).apply()

    fun getLicenseKey(): String? = securePrefs.getString(KEY_LICENSE_KEY, null)
    fun saveLicense(licenseKey: String, email: String? = null) {
        securePrefs.edit()
            .putString(KEY_LICENSE_KEY, licenseKey)
            .putString(KEY_BUYER_EMAIL, email ?: "")
            .putBoolean(KEY_IS_ACTIVATED, true)
            .putLong(KEY_ACTIVATED_AT, System.currentTimeMillis())
            .apply()
    }

    // User Profile & mPIN Registration
    fun isUserRegistered(): Boolean = !securePrefs.getString(KEY_USER_MPIN, null).isNullOrEmpty()

    fun registerUser(name: String, phone: String, mpin: String) {
        securePrefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_PHONE, phone)
            .putString(KEY_USER_MPIN, mpin)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun verifyMpin(inputMpin: String): Boolean {
        val stored = securePrefs.getString(KEY_USER_MPIN, null)
        return stored == inputMpin
    }

    fun resetMpin(newMpin: String) {
        securePrefs.edit().putString(KEY_USER_MPIN, newMpin).apply()
    }

    fun getUserName(): String = securePrefs.getString(KEY_USER_NAME, "") ?: ""
    fun getUserPhone(): String = securePrefs.getString(KEY_USER_PHONE, "") ?: ""

    fun isLoggedIn(): Boolean = securePrefs.getBoolean(KEY_LOGGED_IN, false)
    fun setLoggedIn(loggedIn: Boolean) = securePrefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()

    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        var id = securePrefs.getString(KEY_STORED_ANDROID_ID, null)
        if (id.isNullOrEmpty()) {
            id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            if (id.isNullOrEmpty()) {
                id = java.util.UUID.randomUUID().toString()
            }
            securePrefs.edit().putString(KEY_STORED_ANDROID_ID, id).apply()
        }
        return id
    }

    // App Preferences
    fun isAutoScanEnabled(): Boolean = securePrefs.getBoolean(KEY_AUTO_SCAN, true)
    fun setAutoScanEnabled(enabled: Boolean) = securePrefs.edit().putBoolean(KEY_AUTO_SCAN, enabled).apply()

    fun isNotificationEnabled(): Boolean = securePrefs.getBoolean(KEY_NOTIFICATIONS, true)
    fun setNotificationEnabled(enabled: Boolean) = securePrefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()

    // Optional Monthly Budget
    fun isMonthlyBudgetEnabled(): Boolean = securePrefs.getBoolean(KEY_BUDGET_ENABLED, false)
    fun setMonthlyBudgetEnabled(enabled: Boolean) = securePrefs.edit().putBoolean(KEY_BUDGET_ENABLED, enabled).apply()

    fun getMonthlyBudget(): Double = securePrefs.getFloat(KEY_MONTHLY_BUDGET, 500f).toDouble()
    fun setMonthlyBudget(budget: Double) = securePrefs.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()

    fun clearAllData() {
        securePrefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_IS_ACTIVATED = "key_is_activated"
        private const val KEY_LICENSE_KEY = "key_license_key"
        private const val KEY_BUYER_EMAIL = "key_buyer_email"
        private const val KEY_ACTIVATED_AT = "key_activated_at"
        private const val KEY_STORED_ANDROID_ID = "key_stored_android_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_PHONE = "key_user_phone"
        private const val KEY_USER_MPIN = "key_user_mpin"
        private const val KEY_LOGGED_IN = "key_logged_in"
        private const val KEY_AUTO_SCAN = "key_auto_scan"
        private const val KEY_NOTIFICATIONS = "key_notifications"
        private const val KEY_BUDGET_ENABLED = "key_budget_enabled"
        private const val KEY_MONTHLY_BUDGET = "key_monthly_budget"
    }
}
