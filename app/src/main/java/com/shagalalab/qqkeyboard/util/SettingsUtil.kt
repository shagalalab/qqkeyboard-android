package com.shagalalab.qqkeyboard.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import com.shagalalab.qqkeyboard.R

class SettingsUtil {

    private companion object{
        const val DEFAULT_SOUND_VOLUME: Int = 50
        const val DEFAULT_VIBRATION_LEVEL: Int = 20
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isKeySoundEnabled(context: Context): Boolean {
        val sharedPref = getPreferences(context)
        return sharedPref.getBoolean(context.getString(R.string.pref_keypress_sound_key), true)
    }

    fun isKeyVibrationEnabled(context: Context): Boolean {
        val sharedPref = getPreferences(context)
        return sharedPref.getBoolean(context.getString(R.string.pref_keypress_vibration_key), true)
    }

    fun getSoundVolume(context: Context): Int {
        val sharedPref = getPreferences(context)
        return sharedPref.getInt(
            context.getString(R.string.pref_keypress_sound_volume_level),
            DEFAULT_SOUND_VOLUME
        )
    }

    fun getVibrationLevel(context: Context): Int {
        val sharedPref = getPreferences(context)
        return sharedPref.getInt(
            context.getString(R.string.pref_keypress_vibration_strength_level),
            DEFAULT_VIBRATION_LEVEL
        )
    }

    fun isLightTheme(context: Context): Boolean {
        val sharedPref = getPreferences(context)
        val lightTheme = context.getString(R.string.pref_keypress_theme_light)
        val savedTheme =
            sharedPref.getString(context.getString(R.string.pref_keypress_theme), lightTheme)
        return savedTheme.equals(lightTheme, ignoreCase = true)
    }

    fun getDefaultKeyboard(context: Context): String? {
        val sharedPref = getPreferences(context)
        return sharedPref.getString(
            context.getString(R.string.pref_keypress_default_layout),
            context.getString(R.string.pref_keypress_layout_latin)
        )
    }

    fun setDefaultKeyboard(context: Context, @StringRes layout: Int) {
        getPreferences(context)
            .edit()
            .putString(
                context.getString(R.string.pref_keypress_default_layout),
                context.getString(layout)
            )
            .apply()
    }

    fun isKeyboardWithFirstRowNumbers(context: Context): Boolean {
        val sharedPref = getPreferences(context)
        val keyboardFirstRowNumbers = context.getString(R.string.pref_keypress_row_numbers)
        val savedKeyboard = sharedPref.getString(
            context.getString(R.string.pref_keypress_first_row_appearance),
            keyboardFirstRowNumbers
        )
        return savedKeyboard.equals(keyboardFirstRowNumbers, ignoreCase = true)
    }

}