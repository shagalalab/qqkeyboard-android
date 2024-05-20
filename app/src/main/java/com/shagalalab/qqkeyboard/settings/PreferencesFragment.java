package com.shagalalab.qqkeyboard.settings;

import android.content.Intent;
import android.os.Bundle;

import com.shagalalab.qqkeyboard.R;
import com.shagalalab.qqkeyboard.settings.about.AboutActivity;
import com.shagalalab.qqkeyboard.settings.sound.SoundVolumePreference;
import com.shagalalab.qqkeyboard.settings.sound.SoundVolumePreferenceDialogFragment;
import com.shagalalab.qqkeyboard.settings.vibration.VibrationLevelPreference;
import com.shagalalab.qqkeyboard.settings.vibration.VibrationLevelPreferenceDialogFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.ime_preferences);

        Preference about = findPreference("about");
        if (about != null) {
            about.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                return false;
            });
        }
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        DialogFragment dialogFragment;
        if (preference instanceof SoundVolumePreference) {
            dialogFragment = SoundVolumePreferenceDialogFragment.Companion.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), SoundVolumePreference.class.getName());
        } else if (preference instanceof VibrationLevelPreference) {
            dialogFragment = VibrationLevelPreferenceDialogFragment.Companion.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), VibrationLevelPreference.class.getName());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}