# DotWeather & Air (Nothing OS Edition)

**Developed by S. M. Mahmud Iqbal**

A hyper-minimalist Android weather and environmental sensor monitor inspired by **Nothing OS 3.0** aesthetics, dot-matrix typography, and the industrial design heritage of Teenage Engineering.

---

## 📱 Features

- **💡 Nothing Glyph Atmosphere Pulse Strip**: Hardware-inspired animated 10-segment LED Glyph light bar that dynamically reacts to atmospheric stability, barometric pressure fluctuations, and wind gusts with breathing spring physics.
- **📈 Interactive 14-Hour Temperature & Precipitation Curve**: Visual Nothing OS step-bar progression chart tracking upcoming hourly temperature trends, interactive inspection chips, and rain chance probability.
- **🌙 Astronomical Lunar Ephemeris & Moon Tracker**: Precision synodic moon phase tracker (New Moon, Waxing Crescent, First Quarter, Gibbous, Full Moon) with procedural 7x7 dot-matrix moon icons, illumination percentage, and orbital trajectory.
- **🛡️ Actionable Environmental Health Directives**: Real-time contextual activity guidance based on WHO air quality benchmarks (outdoor exercise safety, natural window ventilation, and respiratory protection).
- **⚡ Instant Offline Telemetry Caching**: Seamless cold-boot offline caching displaying full saved telemetry immediately without waiting or network delays.
- **🧩 3 Dedicated Nothing OS Home Screen Widgets**:
  1. **Weather Quick Glance (2x2)**: Minimalist Nothing squircle with 7x7 procedural dot-matrix weather icon, live temperature, condition label, high/low range, and signature Nothing Red status dot.
  2. **Hourly Forecast Timeline Strip (4x2)**: Multi-hour horizontal strip featuring upcoming hours (`NOW`, `+1H`, `+2H`, `+3H`), mini dot-matrix condition indicators, temperatures, and rain precipitation percentage.
  3. **Environmental & AQI Hub (2x2)**: Sensor dashboard featuring real-time numeric AQI score, procedural circular dot-matrix LED gauge, PM2.5, wind speed/direction, humidity, and UV index.
- **⚡ 1-Tap Home Screen Pinning**: Pin any of the 3 widgets directly to your Android home screen with 1 tap from inside the app (`AppWidgetManager.requestPinAppWidget()`) or long-press your home screen widget menu.
- **📍 Default Active Location Persistence**: Remembers your chosen city and default temperature unit across app restarts and background termination.
- **🧭 Auto-Detect Location**: Instant GPS & IP-based automatic device geolocation detecting your exact city anywhere worldwide on launch.
- **🌍 Live Global Telemetry**: 100% real-time weather and air quality data powered by Open-Meteo (no API keys required, ultra-fast response).
- **🔍 Multi-Result Geocoding Search**: Search any city worldwide with real-time multi-result matching, state/region information, and 1-tap addition.
- **☀️ Astronomical Solar Cycle**: Precise sunrise and sunset dawn/dusk orbits with daylight duration tracker and celestial visual progress.
- **📊 7-Day Outlook with Temperature Range Bars**: Visual horizontal min-to-max temperature comparison bars across the 7-day forecast.
- **📤 Share Weather Report**: Formatted Nothing OS monospace ASCII weather telemetry report shareable via standard Android share sheet.
- **Dot-Matrix Iconography**: Custom 7x7 LED dot-matrix rasterized icons for Sunny, Partly Cloudy, Cloudy, Rainy, Thunderstorm, Windy, Snow, and Moon phases.
- **NDot Digital Clock & Number System**: True 5x7 dot-matrix numeric displays for live temperature and AQI values.
- **Signature Nothing OS Palette**: Pure `#000000` AMOLED True Black background with high-contrast `#FFFFFF` typography and Nothing Crimson Red (`#D71920`) accents.
- **Tactile Haptic Feedback**: Subtle physical clicks on pill switches, refresh actions, and tab toggles.

---

## 📦 Download Latest APK Release

Download the official signed release APK from the GitHub Releases tab:
- **Latest Release**: [v1.8.0 (GitHub Release)](https://github.com/SMMahmudIqbal/DotWeather/releases/latest)
- **Asset**: `DotWeather-v1.8.0.apk`

---

## 🛠️ Architecture & Tech Stack

- **Target OS**: Android 7.0+ (Min SDK 24, Target SDK 36)
- **UI Framework**: Jetpack Compose 2026 Material3 & RemoteViews AppWidgets
- **Language**: Kotlin 2.2+ & Coroutines (Parallel Telemetry Fetching)
- **Components**: `AtmosphereGlyphBar`, `HourlyTrendGraph`, `LunarEphemerisCard`, `AirQualityDirectivesCard`
- **Widgets**: `WeatherQuickWidgetProvider` (2x2), `WeatherForecastWidgetProvider` (4x2), `WeatherAirQualityWidgetProvider` (2x2)
- **APIs**: Open-Meteo Weather, Open-Meteo Air Quality, Open-Meteo Geocoding, IP Geolocation
- **Developer**: S. M. Mahmud Iqbal
