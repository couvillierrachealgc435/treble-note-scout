package com.treblenote.scout.data.local

import android.content.Context
import android.content.SharedPreferences

class ScoutPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun intsWithPrefix(prefix: String): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        prefs.all.forEach { entry ->
            val value = entry.value
            if (entry.key.startsWith(prefix) && value is Int) {
                result[entry.key.removePrefix(prefix)] = value
            }
        }
        return result
    }

    private companion object {
        const val STORE_NAME = "treble_note_scout"
    }
}
