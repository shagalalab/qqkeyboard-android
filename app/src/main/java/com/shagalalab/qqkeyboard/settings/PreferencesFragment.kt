package com.shagalalab.qqkeyboard.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.settings.about.AboutActivity
import com.shagalalab.qqkeyboard.settings.sound.SoundVolumePreference
import com.shagalalab.qqkeyboard.settings.sound.SoundVolumePreferenceDialogFragment
import com.shagalalab.qqkeyboard.settings.vibration.VibrationLevelPreference
import com.shagalalab.qqkeyboard.settings.vibration.VibrationLevelPreferenceDialogFragment

class PreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.ime_preferences)

        val about = findPreference<Preference>("about")
        if (about != null) {
            about.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    val intent = Intent(
                        activity,
                        AboutActivity::class.java
                    )
                    startActivity(intent)
                    false
                }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        val dialogFragment: DialogFragment
        when (preference) {
            is SoundVolumePreference -> {
                dialogFragment = SoundVolumePreferenceDialogFragment.newInstance(preference.getKey())
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(requireFragmentManager(), SoundVolumePreference::class.java.name)
            }

            is VibrationLevelPreference -> {
                dialogFragment = VibrationLevelPreferenceDialogFragment.newInstance(preference.getKey())
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(requireFragmentManager(), VibrationLevelPreference::class.java.name)
            }

            else -> {
                super.onDisplayPreferenceDialog(preference)
            }
        }

    }
}