## DailySpend Production R8 Obfuscation Rules ##

# Preserve Room database entities and DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Gumroad API DTO models for Gson serialization
-keepclassmembers class com.dailyspend.app.data.api.** { <fields>; }
-keep class com.dailyspend.app.data.api.GumroadVerifyResponse { *; }
-keep class com.dailyspend.app.data.api.GumroadPurchase { *; }

# Preserve Android Security Crypto for EncryptedSharedPreferences
-keep class androidx.security.crypto.** { *; }

# Keep BroadcastReceiver for Telephony SMS
-keep class com.dailyspend.app.receiver.SmsReceiver { *; }

# Obfuscate sensitive business logic
-repackageclasses 'com.dailyspend.app.internal'
-allowaccessmodification
