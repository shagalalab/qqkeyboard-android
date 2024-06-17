package com.shagalalab.qqkeyboard.settings.sound

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.preference.PreferenceDialogFragmentCompat
import com.shagalalab.qqkeyboard.R

class SoundVolumePreferenceDialogFragment : PreferenceDialogFragmentCompat(),
    OnSeekBarChangeListener {
    private var caption: TextView? = null
    private lateinit var seekBar: SeekBar

    override fun onBindDialogView(view: View) {
        caption = view.findViewById<TextView>(R.id.sound_volume_preference_caption)
        seekBar = view.findViewById<SeekBar>(R.id.sound_volume_preference_seekbar)
        seekBar.setOnSeekBarChangeListener(this)

        var soundVolume: Int? = null
        val preference = preference
        if (preference is SoundVolumePreference) {
            soundVolume = preference.soundVolume
        }

        if (soundVolume != null) {
            seekBar.progress = soundVolume
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val volume = seekBar.progress
            setCaption(volume)

            val preference = preference
            if (preference is SoundVolumePreference) {
                if (preference.callChangeListener(volume)) {
                    preference.soundVolume = volume
                    preference.summary = String.format(
                        requireContext().getString(R.string.text_with_percent),
                        volume
                    )
                }
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        setCaption(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    private fun setCaption(progress: Int) {
        caption!!.text =
            String.format(requireContext().getString(R.string.text_with_percent), progress)
    }

    companion object {
        fun newInstance(key: String?): SoundVolumePreferenceDialogFragment {
            val fragment = SoundVolumePreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}
