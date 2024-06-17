package com.shagalalab.qqkeyboard.settings.vibration

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.R
import com.shagalalab.qqkeyboard.util.SettingsUtil

class VibrationLevelPreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.preferenceStyle,
    defStyleRes: Int = defStyleAttr
) :
    DialogPreference(context!!, attrs, defStyleAttr, defStyleRes) {
    var vibrationLevel = 0
        set(level) {
            field = level
            persistInt(level)
        }


    override fun getSummary(): CharSequence {
        return String.format(context.getString(com.shagalalab.qqkeyboard.R.string.text_with_ms), vibrationLevel)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getInt(index, SettingsUtil.DEFAULT_VIBRATION_LEVEL)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        vibrationLevel = if (defaultValue != null) {
            (defaultValue as Int)
        } else {
            (getPersistedInt(vibrationLevel))
        }
    }

    override fun getDialogLayoutResource(): Int {
        return com.shagalalab.qqkeyboard.R.layout.vibration_level_preference
    }
}
