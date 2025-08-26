package com.shagalalab.qqkeyboard.keyboard.preferences

import android.content.Context
import android.content.SharedPreferences
import com.shagalalab.qqkeyboard.keyboard.model.LayoutType

class KeyboardPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var lastUsedLayout: LayoutType
        get() {
            val layoutName = prefs.getString(KEY_LAST_LAYOUT, LayoutType.LATIN.name)
            return try {
                LayoutType.valueOf(layoutName ?: LayoutType.LATIN.name)
            } catch (e: IllegalArgumentException) {
                LayoutType.LATIN
            }
        }
        set(value) {
            prefs.edit().putString(KEY_LAST_LAYOUT, value.name).apply()
        }
    
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
        }
    
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()
        }
    
    var selectedTheme: String
        get() = prefs.getString(KEY_SELECTED_THEME, "Light") ?: "Light"
        set(value) {
            prefs.edit().putString(KEY_SELECTED_THEME, value).apply()
        }
    
    companion object {
        private const val PREFS_NAME = "qq_keyboard_prefs"
        private const val KEY_LAST_LAYOUT = "last_used_layout"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_SELECTED_THEME = "selected_theme"
    }
}