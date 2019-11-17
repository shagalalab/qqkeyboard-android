package com.shagalalab.qqkeyboard.settings.vibration;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.shagalalab.qqkeyboard.R;
import com.shagalalab.qqkeyboard.util.SettingsUtil;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

/**
 * Created by atabek on 8/09/2016.
 */

public class VibrationLevelPreference extends DialogPreference {
    private int vibrationLevel;

    public VibrationLevelPreference(Context context) {
        this(context, null);
    }

    public VibrationLevelPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public VibrationLevelPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public VibrationLevelPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    int getVibrationLevel() {
        return vibrationLevel;
    }

    void setVibrationLevel(int level) {
        vibrationLevel = level;
        persistInt(level);
    }

    @Override
    public CharSequence getSummary() {
        return String.format(getContext().getString(R.string.text_with_ms), vibrationLevel);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, SettingsUtil.DEFAULT_VIBRATION_LEVEL);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        if (defaultValue != null) {
            setVibrationLevel((Integer) defaultValue);
        } else {
            setVibrationLevel(getPersistedInt(vibrationLevel));
        }
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.vibration_level_preference;
    }

}
