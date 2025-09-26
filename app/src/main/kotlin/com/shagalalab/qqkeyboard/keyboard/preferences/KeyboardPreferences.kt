package com.shagalalab.qqkeyboard.keyboard.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.shagalalab.qqkeyboard.keyboard.compose.COLLECTION_GRID_COLS_SIZE
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import org.json.JSONArray

class KeyboardPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastUsedLayout: KeyboardLayout
        get() {
            val layoutName = prefs.getString(KEY_LAST_LAYOUT, KeyboardLayout.LATIN.name)
            return try {
                val layout = KeyboardLayout.valueOf(layoutName ?: KeyboardLayout.LATIN.name)
                // Only return language layouts, default others to LATIN
                if (layout == KeyboardLayout.LATIN || layout == KeyboardLayout.CYRILLIC) {
                    layout
                } else {
                    KeyboardLayout.LATIN
                }
            } catch (e: IllegalArgumentException) {
                KeyboardLayout.LATIN
            }
        }
        set(value) {
            // Only persist language layouts
            if (value == KeyboardLayout.LATIN || value == KeyboardLayout.CYRILLIC) {
                prefs.edit { putString(KEY_LAST_LAYOUT, value.name) }
            }
        }

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) {
            prefs.edit { putBoolean(KEY_SOUND_ENABLED, value) }
        }

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) {
            prefs.edit { putBoolean(KEY_VIBRATION_ENABLED, value) }
        }

    var selectedTheme: String
        get() = prefs.getString(KEY_SELECTED_THEME, "Light") ?: "Light"
        set(value) {
            prefs.edit { putString(KEY_SELECTED_THEME, value) }
        }

    var recentEmojis: List<String>
        get() {
            val jsonString = prefs.getString(KEY_RECENT_EMOJIS, "[]") ?: "[]"
            return try {
                val jsonArray = JSONArray(jsonString)
                List(jsonArray.length()) { i -> jsonArray.getString(i) }
            } catch (e: Exception) {
                emptyList()
            }
        }
        set(value) {
            val jsonArray = JSONArray()
            value.forEach { emoji -> jsonArray.put(emoji) }
            prefs.edit { putString(KEY_RECENT_EMOJIS, jsonArray.toString()) }
        }

    fun addRecentEmoji(emoji: String) {
        val currentList = recentEmojis.toMutableList()

        // Remove emoji if it already exists (to move it to front)
        currentList.remove(emoji)

        // Add emoji to the front
        currentList.add(0, emoji)

        // Limit the list size
        if (currentList.size > MAX_RECENT_EMOJIS) {
            currentList.removeAt(currentList.size - 1)
        }

        recentEmojis = currentList
    }

    companion object {
        private const val PREFS_NAME = "qq_keyboard_prefs"
        private const val KEY_LAST_LAYOUT = "last_used_layout"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_RECENT_EMOJIS = "recent_emojis"
        private const val MAX_RECENT_EMOJIS = COLLECTION_GRID_COLS_SIZE * 5
    }
}
