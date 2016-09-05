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
}
