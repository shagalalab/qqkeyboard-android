package com.shagalalab.qqkeyboard.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;
import androidx.preference.PreferenceManager;

import com.shagalalab.qqkeyboard.R;

public class SettingsUtil {

    public static final int DEFAULT_SOUND_VOLUME = 50;
    public static final int DEFAULT_VIBRATION_LEVEL = 20;

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isKeySoundEnabled(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_keypress_sound_key), true);
    }

    public static boolean isKeyVibrationEnabled(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_keypress_vibration_key), true);
    }

    public static int getSoundVolume(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getInt(context.getString(R.string.pref_keypress_sound_volume_level), DEFAULT_SOUND_VOLUME);
    }

    public static int getVibrationLevel(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getInt(context.getString(R.string.pref_keypress_vibration_strength_level), DEFAULT_VIBRATION_LEVEL);
    }

    public static boolean isLightTheme(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        String lightTheme = context.getString(R.string.pref_keypress_theme_light);
        String savedTheme = sharedPref.getString(context.getString(R.string.pref_keypress_theme), lightTheme);
        return savedTheme.equalsIgnoreCase(lightTheme);
    }

    public static String getDefaultKeyboard(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_keypress_default_layout), context.getString(R.string.pref_keypress_layout_latin));
    }

    public static void setDefaultKeyboard(Context context, @StringRes int layout) {
        getPreferences(context)
                .edit()
                .putString(context.getString(R.string.pref_keypress_default_layout), context.getString(layout))
                .apply();
    }

    public static boolean isKeyboardWithFirstRowNumbers(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        String keyboardFirstRowNumbers = context.getString(R.string.pref_keypress_row_numbers);
        String savedKeyboard = sharedPref.getString(context.getString(R.string.pref_keypress_first_row_appearance), keyboardFirstRowNumbers);
        return savedKeyboard.equalsIgnoreCase(keyboardFirstRowNumbers);
    }

}