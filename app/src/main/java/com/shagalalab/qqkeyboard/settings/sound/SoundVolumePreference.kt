package com.shagalalab.qqkeyboard.settings.sound

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference

import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.util.SettingsUtil

class SoundVolumePreference(private val context: Context, private val attrs: AttributeSet?): DialogPreference(context, attrs) {

    var soundVolume = 0
        set(volume) {
            field = volume
            persistInt(volume)
        }

    override fun getSummary(): CharSequence {
        return String.format(context.getString(R.string.text_with_percent), soundVolume)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, SettingsUtil.DEFAULT_SOUND_VOLUME)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        soundVolume = if (defaultValue != null) {
            (defaultValue as Int)
        } else {
            (getPersistedInt(soundVolume))
        }
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.sound_volume_preference
    }

}