# DotWeather & Air (Nothing OS Edition)

**Developed by S. M. Mahmud Iqbal**

A hyper-minimalist Android weather and environmental sensor monitor inspired by **Nothing OS 3.0** aesthetics, dot-matrix typography, and the industrial design heritage of Teenage Engineering.

---

## 📱 Features

- **Dot-Matrix Iconography**: Custom 7x7 LED dot-matrix rasterized icons for Sunny, Partly Cloudy, Cloudy, Rainy, Thunderstorm, Windy, and Snow.
- **NDot Digital Clock & Number System**: True 5x7 dot-matrix numeric displays for live temperature and AQI values.
- **Signature Nothing OS Palette**:
  - Pure `#000000` AMOLED True Black background.
  - High-contrast `#FFFFFF` typography.
  - Iconic Nothing Crimson Red (`#D71920`) accent recording dots and indicator lights.
- **Modular Squircle Cards**: 26dp rounded corner cards with subtle inner borders and padding.
- **Interactive Metrics**:
  - Real-time °C / °F unit toggle pill.
  - Multi-city selector chips (London, Tokyo, New York).
  - Hourly Timeline carousel with precipitation percentages.
  - Environmental Sensor Grid (Wind speed/direction, Humidity, Dew point, UV Index, Barometric Pressure).
  - Air Quality Index (AQI) matrix with dotted progress bar and PM2.5, PM10, O3, NO2 readings.
  - 7-Day outlook forecast cards.

---

## 📦 APK Location

The compiled native Android `.APK` is located at:
```
C:\Users\rupai\.gemini\antigravity\scratch\DotWeather\app\build\outputs\apk\debug\app-debug.apk
```

### Installation via ADB:
```powershell
& "C:\Users\rupai\AppData\Local\Android\Sdk\platform-tools\adb.exe" install "C:\Users\rupai\.gemini\antigravity\scratch\DotWeather\app\build\outputs\apk\debug\app-debug.apk"
```

---

## 🛠️ Architecture & Tech Stack

- **Target OS**: Android 7.0+ (Min SDK 24, Target SDK 36)
- **UI Framework**: Jetpack Compose 2026 Material3
- **Language**: Kotlin 2.2+
- **Developer**: S. M. Mahmud Iqbal
