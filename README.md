# DotWeather & Air (Nothing OS Edition)

**Developed by S. M. Mahmud Iqbal**

A hyper-minimalist Android weather and environmental sensor monitor inspired by **Nothing OS 3.0** aesthetics, dot-matrix typography, and the industrial design heritage of Teenage Engineering.

---

## 📱 Features

- **📍 Auto-Detect Location**: Instant GPS & IP-based automatic device geolocation detecting your exact city anywhere worldwide on launch.
- **🌍 Live Global Telemetry**: 100% real-time weather and air quality data powered by Open-Meteo (no API keys required, ultra-fast response).
- **💾 Persistent Storage**: Custom saved locations, preferred temperature units (°C / °F), and active city index persist across app restarts.
- **🔍 Multi-Result Geocoding Search**: Search any city worldwide with real-time multi-result matching, state/region information, and 1-tap addition.
- **⚠ Contextual Weather Advisories**: Dynamic Nothing OS smart alerts for precipitation, thunderstorms, high UV radiation, gusty winds, freezing temperatures, and air pollution.
- **☀️ Astronomical Solar Cycle**: Precise sunrise and sunset dawn/dusk orbits with daylight duration tracker and celestial visual progress.
- **📊 7-Day Outlook with Temperature Range Bars**: Visual horizontal min-to-max temperature comparison bars across the 7-day forecast.
- **📤 Share Weather Report**: Formatted Nothing OS monospace ASCII weather telemetry report shareable via standard Android share sheet.
- **Dot-Matrix Iconography**: Custom 7x7 LED dot-matrix rasterized icons for Sunny, Partly Cloudy, Cloudy, Rainy, Thunderstorm, Windy, and Snow.
- **NDot Digital Clock & Number System**: True 5x7 dot-matrix numeric displays for live temperature and AQI values.
- **Signature Nothing OS Palette**: Pure `#000000` AMOLED True Black background with high-contrast `#FFFFFF` typography and Nothing Crimson Red (`#D71920`) accents.
- **Environmental Sensor Grid**: Wind speed/direction, Humidity, Dew point, UV Index, Barometric Pressure with atmosphere tendency, and Visibility range.
- **Air Quality Matrix**: European AQI meter with dotted LED progress bar and live PM2.5, PM10, O3, NO2 pollutant readings.
- **Tactile Haptic Feedback**: Subtle physical clicks on pill switches, refresh actions, and tab toggles.

---

## 📦 Download Latest APK Release

Download the official signed release APK from the GitHub Releases tab:
- **Latest Release**: [v1.4.0 (GitHub Release)](https://github.com/SMMahmudIqbal/DotWeather/releases/latest)
- **Asset**: `DotWeather-v1.4.0.apk`

---

## 🛠️ Architecture & Tech Stack

- **Target OS**: Android 7.0+ (Min SDK 24, Target SDK 36)
- **UI Framework**: Jetpack Compose 2026 Material3
- **Language**: Kotlin 2.2+ & Coroutines (Parallel Telemetry Fetching)
- **APIs**: Open-Meteo Weather, Open-Meteo Air Quality, Open-Meteo Geocoding, IP Geolocation
- **Developer**: S. M. Mahmud Iqbal
