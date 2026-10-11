package com.example.dotweather.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

// Author Attribution: Developed by S. M. Mahmud Iqbal

data class SavedLocation(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isCustom: Boolean = true,
    val isGps: Boolean = false
)

class CityStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dotweather_prefs", Context.MODE_PRIVATE)

    fun getUnitIsCelsius(): Boolean = prefs.getBoolean("is_celsius", true)
    fun setUnitIsCelsius(isCelsius: Boolean) = prefs.edit().putBoolean("is_celsius", isCelsius).apply()

    fun getActiveCityName(): String? = prefs.getString("active_city_name", null)
    fun getActiveCityCountry(): String = prefs.getString("active_city_country", "") ?: ""
    fun getActiveCityLat(): Double = prefs.getString("active_city_lat", null)?.toDoubleOrNull() ?: Double.NaN
    fun getActiveCityLon(): Double = prefs.getString("active_city_lon", null)?.toDoubleOrNull() ?: Double.NaN

    fun setActiveCity(name: String, country: String, lat: Double, lon: Double) {
        prefs.edit()
            .putString("active_city_name", name.uppercase())
            .putString("active_city_country", country.uppercase())
            .putString("active_city_lat", lat.toString())
            .putString("active_city_lon", lon.toString())
            .apply()
    }

    fun getSavedLocations(): List<SavedLocation> {
        val raw = prefs.getString("saved_locations", null) ?: return emptyList()
        val list = mutableListOf<SavedLocation>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SavedLocation(
                        name = obj.getString("name").uppercase(),
                        country = obj.getString("country").uppercase(),
                        lat = obj.getDouble("lat"),
                        lon = obj.getDouble("lon"),
                        isCustom = obj.optBoolean("isCustom", true),
                        isGps = obj.optBoolean("isGps", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveLocations(locations: List<SavedLocation>) {
        val jsonArray = JSONArray()
        for (loc in locations) {
            val obj = JSONObject().apply {
                put("name", loc.name.uppercase())
                put("country", loc.country.uppercase())
                put("lat", loc.lat)
                put("lon", loc.lon)
                put("isCustom", loc.isCustom)
                put("isGps", loc.isGps)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("saved_locations", jsonArray.toString()).apply()
    }
}
