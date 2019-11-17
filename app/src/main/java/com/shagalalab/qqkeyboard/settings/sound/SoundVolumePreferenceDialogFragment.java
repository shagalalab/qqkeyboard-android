package com.shagalalab.qqkeyboard.settings.sound;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shagalalab.qqkeyboard.R;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class SoundVolumePreferenceDialogFragment extends PreferenceDialogFragmentCompat
    implements SeekBar.OnSeekBarChangeListener {

    private TextView caption;
    private SeekBar seekBar;

    public static SoundVolumePreferenceDialogFragment newInstance(String key) {
        final SoundVolumePreferenceDialogFragment fragment = new SoundVolumePreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        caption = view.findViewById(R.id.sound_volume_preference_caption);
        seekBar = view.findViewById(R.id.sound_volume_preference_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        Integer soundVolume = null;
        DialogPreference preference = getPreference();
        if (preference instanceof SoundVolumePreference) {
            soundVolume = ((SoundVolumePreference) preference).getSoundVolume();
        }

        if (soundVolume != null) {
            seekBar.setProgress(soundVolume);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int volume = seekBar.getProgress();
            setCaption(volume);

            DialogPreference preference = getPreference();
            if (preference instanceof SoundVolumePreference) {
                SoundVolumePreference soundPreference = (SoundVolumePreference) preference;
                if (soundPreference.callChangeListener(volume)) {
                    soundPreference.setSoundVolume(volume);
                    soundPreference.setSummary(String.format(requireContext().getString(R.string.text_with_percent), volume));
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setCaption(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setCaption(int progress) {
        caption.setText(String.format(requireContext().getString(R.string.text_with_percent), progress));
    }
}
