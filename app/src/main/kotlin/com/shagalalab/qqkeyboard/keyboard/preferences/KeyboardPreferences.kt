package com.shagalalab.qqkeyboard.keyboard.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.shagalalab.qqkeyboard.keyboard.compose.COLLECTION_GRID_COLS_SIZE
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength
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

    var topRowMode: TopRowMode
        get() {
            val name = prefs.getString(KEY_TOP_ROW_MODE, TopRowMode.EXTRA_LETTERS.name)
            return try {
                TopRowMode.valueOf(name ?: TopRowMode.EXTRA_LETTERS.name)
            } catch (e: IllegalArgumentException) {
                TopRowMode.EXTRA_LETTERS
            }
        }
        set(value) {
            prefs.edit { putString(KEY_TOP_ROW_MODE, value.name) }
        }

    var autoCapEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CAP, true)
        set(value) { prefs.edit { putBoolean(KEY_AUTO_CAP, value) } }

    var autoSpaceAfterPunctuation: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SPACE_PUNCTUATION, false)
        set(value) { prefs.edit { putBoolean(KEY_AUTO_SPACE_PUNCTUATION, value) } }

    var autoRemoveSpaceBeforePunctuation: Boolean
        get() = prefs.getBoolean(KEY_AUTO_REMOVE_SPACE_PUNCTUATION, true)
        set(value) { prefs.edit { putBoolean(KEY_AUTO_REMOVE_SPACE_PUNCTUATION, value) } }

    var doubleSpacePeriodEnabled: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_SPACE_PERIOD, true)
        set(value) { prefs.edit { putBoolean(KEY_DOUBLE_SPACE_PERIOD, value) } }

    var keyboardHeight: KeyboardHeight
        get() {
            val name = prefs.getString(KEY_KEYBOARD_HEIGHT, KeyboardHeight.DEFAULT.name)
            return try { KeyboardHeight.valueOf(name ?: KeyboardHeight.DEFAULT.name) }
            catch (e: IllegalArgumentException) { KeyboardHeight.DEFAULT }
        }
        set(value) { prefs.edit { putString(KEY_KEYBOARD_HEIGHT, value.name) } }

    var keyBorderEnabled: Boolean
        get() = prefs.getBoolean(KEY_KEY_BORDER, true)
        set(value) { prefs.edit { putBoolean(KEY_KEY_BORDER, value) } }

    var vibrationStrength: VibrationStrength
        get() {
            val name = prefs.getString(KEY_VIBRATION_STRENGTH, VibrationStrength.MEDIUM.name)
            return try { VibrationStrength.valueOf(name ?: VibrationStrength.MEDIUM.name) }
            catch (e: IllegalArgumentException) { VibrationStrength.MEDIUM }
        }
        set(value) { prefs.edit { putString(KEY_VIBRATION_STRENGTH, value.name) } }

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
        private const val KEY_TOP_ROW_MODE = "top_row_mode"
        private const val KEY_RECENT_EMOJIS = "recent_emojis"
        private const val KEY_AUTO_CAP = "auto_cap_enabled"
        private const val KEY_AUTO_SPACE_PUNCTUATION = "auto_space_punctuation"
        private const val KEY_AUTO_REMOVE_SPACE_PUNCTUATION = "auto_remove_space_punctuation"
        private const val KEY_DOUBLE_SPACE_PERIOD = "double_space_period"
        private const val KEY_KEYBOARD_HEIGHT = "keyboard_height"
        private const val KEY_KEY_BORDER = "key_border_enabled"
        private const val KEY_VIBRATION_STRENGTH = "vibration_strength"
        private const val MAX_RECENT_EMOJIS = COLLECTION_GRID_COLS_SIZE * 5
    }
}
