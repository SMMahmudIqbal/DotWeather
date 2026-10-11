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

    fun getLastCityIndex(): Int = prefs.getInt("last_city_index", 0)
    fun setLastCityIndex(index: Int) = prefs.edit().putInt("last_city_index", index).apply()

    fun getSavedLocations(): List<SavedLocation> {
        val raw = prefs.getString("saved_locations", null) ?: return emptyList()
        val list = mutableListOf<SavedLocation>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SavedLocation(
                        name = obj.getString("name"),
                        country = obj.getString("country"),
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
                put("name", loc.name)
                put("country", loc.country)
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
