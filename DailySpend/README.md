# DailySpend - Automatic SMS Expense Tracker (Paid Android App)

### Customer Onboarding & Security Flow
1. **First Launch (Gumroad License)**:
   Customer opens app -> pastes Gumroad license key (e.g. `ABCD-1234-EFGH-5678`) -> taps "Activate".
2. **Landing Splash (3-4 Seconds)**:
   Displays "License Verified", introduces core background features, and transitions automatically.
3. **Account Registration**:
   User registers with Name, Phone number, and sets a 4-digit mPIN.
4. **Subsequent Opens (mPIN Lock)**:
   User only needs to enter their 4-digit mPIN to unlock. Includes "Forgot mPIN" recovery via phone number.
5. **Customer Support**:
   Direct in-app contact with official customer care: `thebill.customercare@gmail.com`.
6. **Optional Monthly Budget**:
   Budget is optional; if toggled on and spending exceeds limit, app alerts:
   *"Exceeded monthly budget by $XX.XX"*.

---

## 🚀 How to Build Signed Release APK for Gumroad

### 1. Generate Keystore (One-time)
```bash
keytool -genkey -v -keystore dailyspend-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias dailyspend
```

### 2. Configure `app/build.gradle.kts`
Set your release signing credentials and build:
```bash
./gradlew assembleRelease
```
Your APK will be located at:
`app/build/outputs/apk/release/app-release.apk`
