package com.shagalalab.qqkeyboard.settings.sound;

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

public class SoundVolumePreference extends DialogPreference {

    private int soundVolume;

    public SoundVolumePreference(Context context) {
        this(context, null);
    }

    public SoundVolumePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public SoundVolumePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public SoundVolumePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    int getSoundVolume() {
        return soundVolume;
    }

    void setSoundVolume(int volume) {
        soundVolume = volume;
        persistInt(volume);
    }

    @Override
    public CharSequence getSummary() {
        return String.format(getContext().getString(R.string.text_with_percent), soundVolume);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, SettingsUtil.DEFAULT_SOUND_VOLUME);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        if (defaultValue != null) {
            setSoundVolume((Integer) defaultValue);
        } else {
            setSoundVolume(getPersistedInt(soundVolume));
        }
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.sound_volume_preference;
    }

}
