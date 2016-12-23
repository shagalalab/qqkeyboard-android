package com.shagalalab.qqkeyboard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shagalalab.qqkeyboard.R;

/**
 * Created by atabek on 2/09/2016.
 */

public class SettingsUtil {


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

    public static void setSoundVolume(Context context, int level) {
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.pref_keypress_sound_volume_level), level);
        editor.apply();
    }

    public static int getSoundVolume(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getInt(context.getString(R.string.pref_keypress_sound_volume_level), 50);
    }

    public static void setVibrationLevel(Context context, int level) {
        SharedPreferences sharedPref = getPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.pref_keypress_vibration_strength_level), level);
        editor.apply();
    }

    public static int getVibrationLevel(Context context) {
        SharedPreferences sharedPref = getPreferences(context);
        return sharedPref.getInt(context.getString(R.string.pref_keypress_vibration_strength_level), 20);
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

}
