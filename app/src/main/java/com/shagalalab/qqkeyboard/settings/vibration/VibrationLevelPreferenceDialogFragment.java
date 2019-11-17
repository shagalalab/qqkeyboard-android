package com.shagalalab.qqkeyboard.settings.vibration;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shagalalab.qqkeyboard.R;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class VibrationLevelPreferenceDialogFragment extends PreferenceDialogFragmentCompat
    implements SeekBar.OnSeekBarChangeListener {

    private TextView caption;
    private SeekBar seekBar;
    private Vibrator vibrator;

    public static VibrationLevelPreferenceDialogFragment newInstance(String key) {
        final VibrationLevelPreferenceDialogFragment fragment = new VibrationLevelPreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        caption = view.findViewById(R.id.vibration_level_preference_caption);
        seekBar = view.findViewById(R.id.vibration_level_preference_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        Integer vibrationLevel = null;
        DialogPreference preference = getPreference();
        if (preference instanceof VibrationLevelPreference) {
            vibrationLevel = ((VibrationLevelPreference) preference).getVibrationLevel();
        }

        if (vibrationLevel != null) {
            seekBar.setProgress(vibrationLevel);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int level = seekBar.getProgress();
            setCaption(level);

            DialogPreference preference = getPreference();
            if (preference instanceof VibrationLevelPreference) {
                VibrationLevelPreference vibrationPreference = (VibrationLevelPreference) preference;
                if (vibrationPreference.callChangeListener(level)) {
                    vibrationPreference.setVibrationLevel(level);
                    vibrationPreference.setSummary(String.format(requireContext().getString(R.string.text_with_ms), level));
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
        getVibrator().vibrate(seekBar.getProgress());
    }

    private Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

        return vibrator;
    }

    private void setCaption(int progress) {
        caption.setText(String.format(requireContext().getString(R.string.text_with_ms), progress));
    }
}
