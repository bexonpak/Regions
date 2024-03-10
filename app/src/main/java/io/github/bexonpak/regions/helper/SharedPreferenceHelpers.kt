package io.github.bexonpak.regions.helper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

object SharedPreferenceHelpers {
    private lateinit var sharedPreferences: SharedPreferences
    private val gson: Gson = Gson()

    private const val FAVORITES_KEY = "Favorites"

    fun init(application: Application) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    }

    private fun put(context: Context, key: String, value: Any) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> editor.putString(key, value.toString())
        }
        editor.apply()
    }

    private fun put(context: Context, hashMap: HashMap<String, Any>) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        for ((key, value) in hashMap) {
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is Long -> editor.putLong(key, value)
                else -> editor.putString(key, value.toString())
            }
        }
        editor.apply()
    }

    private fun get(context: Context, key: String, defaultObj: Any?): Any? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (defaultObj) {
            is String -> return sharedPreferences.getString(key, defaultObj)
            is Boolean -> return sharedPreferences.getBoolean(key, defaultObj)
            is Int -> return sharedPreferences.getInt(key, defaultObj)
            is Float -> return sharedPreferences.getFloat(key, defaultObj)
            is Long -> return sharedPreferences.getLong(key, defaultObj)
        }
        return null
    }

    private fun getOrNull(key: String, defaultObj: Any?): Any? {
        return if (sharedPreferences.contains(key)) {
            when (defaultObj) {
                is String -> sharedPreferences.getString(key, defaultObj)
                is Boolean -> sharedPreferences.getBoolean(key, defaultObj)
                is Int -> sharedPreferences.getInt(key, defaultObj)
                is Float -> sharedPreferences.getFloat(key, defaultObj)
                is Long -> sharedPreferences.getLong(key, defaultObj)
                is Set<*> -> sharedPreferences.getStringSet(key, emptySet<String>())
                else -> null
            }
        } else null
    }

    private fun remove(context: Context, key: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.remove(key)
        editor.apply()
    }

    private fun remove(context: Context, keyList: List<String>) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        for (key in keyList) {
            editor.remove(key)
        }
        editor.apply()
    }

    fun getFavorites(context: Context): Array<String> {
        val get = get(context, FAVORITES_KEY, "") as String
        if (get != "") {
            val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
            val fromJson = gson.fromJson(get, type) as HashMap<String, Boolean>
            return fromJson.keys.toTypedArray()
        } else {
            return emptyArray()
        }
    }

    fun addFavorite(context: Context, locale: Locale) {
        val json = get(context, FAVORITES_KEY, "") as String
        if (json != "") {
            val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
            val fromJson = gson.fromJson(json, type) as HashMap<String, Boolean>
            fromJson[locale.toString()] = true
            put(context, FAVORITES_KEY, gson.toJson(fromJson))
        } else {
            val map = HashMap<String, Boolean>()
            map[locale.toString()] = true
            put(context, FAVORITES_KEY, gson.toJson(map))
        }
    }

    fun removeFavorite(context: Context, locale: Locale) {
        val json = get(context, FAVORITES_KEY, "") as String
        Log.d("TAG", "removeFavorite: ${gson.toJson(json)}")
        if (json != "") {
            val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
            val fromJson = gson.fromJson(json, type) as HashMap<String, Boolean>
            fromJson.remove(locale.toString())
            put(context, FAVORITES_KEY, gson.toJson(fromJson))
        }
    }

}