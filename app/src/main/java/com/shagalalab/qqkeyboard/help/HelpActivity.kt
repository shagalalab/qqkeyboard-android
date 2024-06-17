package com.shagalalab.qqkeyboard.help

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ExpandableListView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.settings.ImePreferencesActivity
import com.shagalalab.qqkeyboard.settings.about.AboutActivity

class HelpActivity: AppCompatActivity() {

    private val settingsCallback =
        View.OnClickListener {
            startActivity(
                Intent(
                    this,
                    ImePreferencesActivity::class.java
                )
            )
        }

    private val aboutCallback =
        View.OnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                )
            )
        }

    private val enableKeyboardCallback =
        View.OnClickListener {
            startActivityForResult(
                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS),
                0
            )
        }

    private val chooseKeyboardCallback = View.OnClickListener {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (isInputEnabled()) {
            imm.showInputMethodPicker()
        } else {
            Snackbar.make(
                findViewById(R.id.help_expandable_list),
                R.string.enable_keyboard_first,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.enable, enableKeyboardCallback)
                .show()
        }
    }

    private class ClickableString(private val listener: View.OnClickListener) :
        ClickableSpan() {
        override fun onClick(v: View) {
            listener.onClick(v)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

        val data: MutableList<Pair<String, CharSequence>> = ArrayList()
        data.add(Pair.create(getString(R.string.faq_how_to_install_question), getInstallAnswer()))
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_open_settings_question),
                getSettingsAnswer()
            )
        )
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_change_first_row_question),
                getFirstRowAnswer()
            )
        )
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_change_template_question),
                getTemplateAnswer()
            )
        )
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_change_sound_question),
                getSoundAnswer()
            )
        )
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_change_vibration_question),
                getVibrationAnswer()
            )
        )
        data.add(
            Pair.create(
                getString(R.string.faq_how_to_find_about_app_question),
                getAboutAnswer()
            )
        )

        val expandableListView = findViewById<ExpandableListView>(R.id.help_expandable_list)
        val expandableListViewAdapter = ExpandableListViewAdapter(LayoutInflater.from(this), data)
        expandableListView.setAdapter(expandableListViewAdapter)

    }

    private fun isInputEnabled(): Boolean {
        var isInputEnabled = false
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val mInputMethodProperties = imm.enabledInputMethodList
        for (i in mInputMethodProperties.indices) {
            val imi = mInputMethodProperties[i]
            if (imi.id.contains(packageName)) {
                isInputEnabled = true
            }
        }

        return isInputEnabled
    }

    private fun getInstallAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_install_answer)
        val enableKeyboard = getString(R.string.enable_keyboard)
        val enableKeyboardOff = "[ENABLE_KEYBOARD_OFF]"
        val enableKeyboardOn = "[ENABLE_KEYBOARD_ON]"
        val chooseKeyboard = "[CHOOSE_KEYBOARD]"
        val linkString = "silteme"
        val changeKeyboard = getString(R.string.change_default_keyboard)

        val spannableString = SpannableString(answer)
        val indexOfEnableKeyboard = answer.indexOf(enableKeyboard)
        val indexOfLink1 = answer.indexOf(linkString)
        val indexOfEnableKeyboardOff = answer.indexOf(enableKeyboardOff)
        val indexOfEnableKeyboardOn = answer.indexOf(enableKeyboardOn)
        val indexOfChooseKeyboard = answer.indexOf(chooseKeyboard)
        val indexOfChangeKeyboard = answer.indexOf(changeKeyboard)
        val indexOfLink2 = answer.indexOf(linkString, indexOfChangeKeyboard)

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfEnableKeyboard,
            indexOfEnableKeyboard + enableKeyboard.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(enableKeyboardCallback),
            indexOfLink1,
            indexOfLink1 + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            getScaledDrawable(R.drawable.enable_keyboard_off)?.let { ImageSpan(it) },
            indexOfEnableKeyboardOff,
            indexOfEnableKeyboardOff + enableKeyboardOff.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            getScaledDrawable(R.drawable.enable_keyboard_on)?.let { ImageSpan(it) },
            indexOfEnableKeyboardOn,
            indexOfEnableKeyboardOn + enableKeyboardOn.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfChangeKeyboard,
            indexOfChangeKeyboard + changeKeyboard.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(chooseKeyboardCallback),
            indexOfLink2,
            indexOfLink2 + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            getScaledDrawable(R.drawable.choose_keyboard)?.let { ImageSpan(it) },
            indexOfChooseKeyboard,
            indexOfChooseKeyboard + chooseKeyboard.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
    private fun getSettingsAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_open_settings_answer)
        val settings = getString(R.string.keyboard_settings)
        val linkString = "siltemeni"

        val spannableString = SpannableString(answer)
        val indexOfSettings = answer.indexOf(settings)
        val indexOfLink = answer.indexOf(linkString)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            answer.indexOf(settings),
            indexOfSettings + settings.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(settingsCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getFirstRowAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_change_first_row_answer)
        val letters = getString(R.string.row_additional_letters)
        val numbers = getString(R.string.row_numbers)
        val linkString = "sazlawlarǵa"

        val spannableString = SpannableString(answer)
        val indexOfLetters = answer.indexOf(letters)
        val indexOfNumbers = answer.indexOf(numbers)
        val indexOfLink = answer.indexOf(linkString)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfLetters,
            indexOfLetters + letters.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfNumbers,
            indexOfNumbers + numbers.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(settingsCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getTemplateAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_change_template_answer)
        val light = "jarıq (Material Light)"
        val dark = "qarańǵı (Material Dark)"
        val settingsSection = getString(R.string.keyboard_view_settings)
        val linkString = "sazlawlarǵa"

        val spannableString = SpannableString(answer)
        val indexOfLight = answer.indexOf(light)
        val indexOfDark = answer.indexOf(dark)
        val indexOfSettingsSection = answer.indexOf(settingsSection)
        val indexOfLink = answer.indexOf(linkString)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfLight,
            indexOfLight + light.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfDark,
            indexOfDark + dark.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfSettingsSection,
            indexOfSettingsSection + settingsSection.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(settingsCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getSoundAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_change_sound_answer)
        val sound = getString(R.string.sound_on_keypress)
        val volume = getString(R.string.volume_on_keypress)
        val linkString = "sazlawlarǵa"

        val spannableString = SpannableString(answer)
        val indexOfSound1 = answer.indexOf(sound)
        val indexOfVolume = answer.indexOf(volume)
        val indexOfLink = answer.indexOf(linkString)
        val indexOfSound2 = answer.indexOf(sound, indexOfVolume)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfSound1,
            indexOfSound1 + sound.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfVolume,
            indexOfVolume + volume.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfSound2,
            indexOfSound2 + sound.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(settingsCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getVibrationAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_change_vibration_answer)
        val vibrationOn = getString(R.string.vibrate_on_keypress)
        val vibrationStrength = getString(R.string.vibration_strenth_on_keypress)
        val linkString = "sazlawlarǵa"

        val spannableString = SpannableString(answer)
        val indexOfVibration1 = answer.indexOf(vibrationOn)
        val indexOfStrength = answer.indexOf(vibrationStrength)
        val indexOfLink = answer.indexOf(linkString)
        val indexOfVibration2 = answer.indexOf(vibrationOn, indexOfStrength)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfVibration1,
            indexOfVibration1 + vibrationOn.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfStrength,
            indexOfStrength + vibrationStrength.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfVibration2,
            indexOfVibration2 + vibrationOn.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(settingsCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getAboutAnswer(): CharSequence {
        val answer = getString(R.string.faq_how_to_find_about_app_answer)
        val about = getString(R.string.about_keyboard)
        val linkString = "siltemeni"

        val spannableString = SpannableString(answer)
        val indexOfAbout = answer.indexOf(about)
        val indexOfLink = answer.indexOf(linkString)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            answer.indexOf(about),
            indexOfAbout + about.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ClickableString(aboutCallback),
            answer.indexOf(linkString),
            indexOfLink + linkString.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    private fun getScaledDrawable(@DrawableRes resId: Int): Drawable? {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val scaledWidth = (metrics.widthPixels * 0.9).toInt()

        val d = ContextCompat.getDrawable(this, resId)
        if (d != null) {
            val scaledHeight = d.intrinsicHeight * scaledWidth / d.intrinsicWidth
            d.setBounds(0, 0, scaledWidth, scaledHeight)
        }
        return d
    }
}