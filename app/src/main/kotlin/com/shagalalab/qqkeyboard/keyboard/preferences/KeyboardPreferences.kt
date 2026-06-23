package com.shagalalab.qqkeyboard.keyboard.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardHeight
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardDimensions
import com.shagalalab.qqkeyboard.keyboard.model.KeyboardLayout
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength
import com.shagalalab.qqkeyboard.keyboard.theme.KeyboardThemes
import org.json.JSONArray

class KeyboardPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        migrateFromLegacyIfNeeded(context)
    }

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
        get() = prefs.getString(KEY_SELECTED_THEME, KeyboardThemes.SystemAuto.name) ?: KeyboardThemes.SystemAuto.name
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

    private fun migrateFromLegacyIfNeeded(context: Context) {
        if (prefs.getBoolean(KEY_MIGRATION_V1_DONE, false)) return

        val legacy = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)

        // Only migrate if the old app wrote any data
        if (legacy.contains(LEGACY_SOUND_KEY) || legacy.contains(LEGACY_LAYOUT_KEY)) {
            prefs.edit {
                putBoolean(KEY_SOUND_ENABLED, legacy.getBoolean(LEGACY_SOUND_KEY, true))
                putBoolean(KEY_VIBRATION_ENABLED, legacy.getBoolean(LEGACY_VIBRATION_KEY, true))

                val legacyLayout = legacy.getString(LEGACY_LAYOUT_KEY, LEGACY_LAYOUT_LATIN)
                val layout = if (legacyLayout == LEGACY_LAYOUT_CYRILLIC) KeyboardLayout.CYRILLIC else KeyboardLayout.LATIN
                putString(KEY_LAST_LAYOUT, layout.name)

                val legacyRow = legacy.getString(LEGACY_FIRST_ROW_KEY, LEGACY_ROW_NUMBERS)
                val topRow = if (legacyRow == LEGACY_ROW_LETTERS) TopRowMode.EXTRA_LETTERS else TopRowMode.NUMBERS
                putString(KEY_TOP_ROW_MODE, topRow.name)

                val legacyTheme = legacy.getString(LEGACY_THEME_KEY, LEGACY_THEME_LIGHT)
                val theme = if (legacyTheme == LEGACY_THEME_DARK) KeyboardThemes.Dark.name else KeyboardThemes.SystemAuto.name
                putString(KEY_SELECTED_THEME, theme)

                val legacyVibrationMs = legacy.getInt(LEGACY_VIBRATION_LEVEL_KEY, 20)
                val strength = when {
                    legacyVibrationMs <= 20 -> VibrationStrength.LIGHT
                    legacyVibrationMs <= 35 -> VibrationStrength.MEDIUM
                    else -> VibrationStrength.STRONG
                }
                putString(KEY_VIBRATION_STRENGTH, strength.name)
            }
        }

        prefs.edit { putBoolean(KEY_MIGRATION_V1_DONE, true) }
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
        private const val KEY_MIGRATION_V1_DONE = "migration_v1_done"
        private const val MAX_RECENT_EMOJIS = KeyboardDimensions.emojiGridColumns * 5

        // Legacy keys from old app (PreferenceManager.getDefaultSharedPreferences)
        private const val LEGACY_SOUND_KEY = "pref_keypress_sound_key"
        private const val LEGACY_VIBRATION_KEY = "pref_keypress_vibration_key"
        private const val LEGACY_VIBRATION_LEVEL_KEY = "pref_keypress_vibration_strength_level"
        private const val LEGACY_THEME_KEY = "pref_keypress_theme"
        private const val LEGACY_THEME_LIGHT = "theme_light"
        private const val LEGACY_THEME_DARK = "theme_dark"
        private const val LEGACY_LAYOUT_KEY = "pref_keypress_default_layout"
        private const val LEGACY_LAYOUT_LATIN = "layout_latin"
        private const val LEGACY_LAYOUT_CYRILLIC = "layout_cyrillic"
        private const val LEGACY_FIRST_ROW_KEY = "pref_keypress_first_row_appearance"
        private const val LEGACY_ROW_NUMBERS = "row_numbers"
        private const val LEGACY_ROW_LETTERS = "row_additional_letters"
    }
}
