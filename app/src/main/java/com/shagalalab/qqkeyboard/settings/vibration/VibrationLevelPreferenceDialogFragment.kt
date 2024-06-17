package com.shagalalab.qqkeyboard.settings.vibration

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.preference.PreferenceDialogFragmentCompat
import com.shagalalab.qqkeyboard.R


class VibrationLevelPreferenceDialogFragment : PreferenceDialogFragmentCompat(),
    OnSeekBarChangeListener {
    private var caption: TextView? = null
    private lateinit var seekBar: SeekBar
    private var vibrator: Vibrator? = null
        get() {
            if (field == null) {
                field = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            return field
        }

    override fun onBindDialogView(view: View) {
        caption = view.findViewById<TextView>(R.id.vibration_level_preference_caption)
        seekBar = view.findViewById<SeekBar>(R.id.vibration_level_preference_seekbar)
        seekBar.setOnSeekBarChangeListener(this)

        var vibrationLevel: Int? = null
        val preference = preference
        if (preference is VibrationLevelPreference) {
            vibrationLevel = preference.vibrationLevel
        }

        if (vibrationLevel != null) {
            seekBar.setProgress(vibrationLevel)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val level = seekBar.progress
            setCaption(level)

            val preference = preference
            if (preference is VibrationLevelPreference) {
                if (preference.callChangeListener(level)) {
                    preference.vibrationLevel = level
                    preference.summary =
                        String.format(requireContext().getString(R.string.text_with_ms), level)
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
        vibrator!!.vibrate(seekBar.progress.toLong())
    }

    private fun setCaption(progress: Int) {
        caption!!.text =
            String.format(requireContext().getString(R.string.text_with_ms), progress)
    }

    companion object {
        fun newInstance(key: String?): VibrationLevelPreferenceDialogFragment {
            val fragment = VibrationLevelPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}
