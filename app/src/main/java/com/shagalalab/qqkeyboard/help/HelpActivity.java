package com.shagalalab.qqkeyboard.help;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;

import com.google.android.material.snackbar.Snackbar;
import com.shagalalab.qqkeyboard.R;
import com.shagalalab.qqkeyboard.settings.ImePreferencesActivity;
import com.shagalalab.qqkeyboard.settings.about.AboutActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HelpActivity extends AppCompatActivity {

    private View.OnClickListener settingsCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HelpActivity.this, ImePreferencesActivity.class));;
        }
    };

    private View.OnClickListener aboutCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HelpActivity.this, AboutActivity.class));;
        }
    };

    private View.OnClickListener enableKeyboardCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
        }
    };

    private View.OnClickListener chooseKeyboardCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && isInputEnabled()) {
                imm.showInputMethodPicker();
            } else {
                Snackbar.make(findViewById(R.id.help_expandable_list), R.string.enable_keyboard_first, Snackbar.LENGTH_LONG)
                    .setAction(R.string.enable, enableKeyboardCallback)
                    .show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        List<Pair<String, CharSequence>> data = new ArrayList<>();
        data.add(Pair.create(getString(R.string.faq_how_to_install_question), getInstallAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_open_settings_question), getSettingsAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_change_first_row_question), getFirstRowAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_change_template_question), getTemplateAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_change_sound_question), getSoundAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_change_vibration_question), getVibrationAnswer()));
        data.add(Pair.create(getString(R.string.faq_how_to_find_about_app_question), getAboutAnswer()));

        ExpandableListView expandableListView = findViewById(R.id.help_expandable_list);
        ExpandableListViewAdapter expandableListViewAdapter = new ExpandableListViewAdapter(LayoutInflater.from(this), data);
        expandableListView.setAdapter(expandableListViewAdapter);
    }

    private boolean isInputEnabled() {
        boolean isInputEnabled = false;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
            for (int i = 0; i < mInputMethodProperties.size(); i++) {
                InputMethodInfo imi = mInputMethodProperties.get(i);
                if (imi.getId().contains(getPackageName())) {
                    isInputEnabled = true;
                }
            }
        }

        return isInputEnabled;
    }

    private CharSequence getInstallAnswer() {
        String answer = getString(R.string.faq_how_to_install_answer);
        String enableKeyboard = getString(R.string.enable_keyboard);
        String enableKeyboardOff = "[ENABLE_KEYBOARD_OFF]";
        String enableKeyboardOn = "[ENABLE_KEYBOARD_ON]";
        String chooseKeyboard = "[CHOOSE_KEYBOARD]";
        String linkString = "silteme";
        String changeKeyboard = getString(R.string.change_default_keyboard);

        SpannableString spannableString = new SpannableString(answer);
        int indexOfEnableKeyboard = answer.indexOf(enableKeyboard);
        int indexOfLink1 = answer.indexOf(linkString);
        int indexOfEnableKeyboardOff = answer.indexOf(enableKeyboardOff);
        int indexOfEnableKeyboardOn = answer.indexOf(enableKeyboardOn);
        int indexOfChooseKeyboard = answer.indexOf(chooseKeyboard);
        int indexOfChangeKeyboard = answer.indexOf(changeKeyboard);
        int indexOfLink2 = answer.indexOf(linkString, indexOfChangeKeyboard);

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfEnableKeyboard, indexOfEnableKeyboard + enableKeyboard.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(enableKeyboardCallback), indexOfLink1, indexOfLink1 + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ImageSpan(getScaledDrawable(R.drawable.enable_keyboard_off)), indexOfEnableKeyboardOff, indexOfEnableKeyboardOff + enableKeyboardOff.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ImageSpan(getScaledDrawable(R.drawable.enable_keyboard_on)), indexOfEnableKeyboardOn, indexOfEnableKeyboardOn + enableKeyboardOn.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfChangeKeyboard, indexOfChangeKeyboard + changeKeyboard.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(chooseKeyboardCallback), indexOfLink2, indexOfLink2 + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ImageSpan(getScaledDrawable(R.drawable.choose_keyboard)), indexOfChooseKeyboard, indexOfChooseKeyboard + chooseKeyboard.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private Drawable getScaledDrawable(@DrawableRes int resId) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int scaledWidth = (int) (metrics.widthPixels * 0.9);

        Drawable d = ContextCompat.getDrawable(this, resId);
        if (d != null) {
            int scaledHeight = d.getIntrinsicHeight() * scaledWidth / d.getIntrinsicWidth();
            d.setBounds(0, 0, scaledWidth, scaledHeight);
        }
        return d;
    }

    private CharSequence getSettingsAnswer() {
        String answer = getString(R.string.faq_how_to_open_settings_answer);
        String settings = getString(R.string.keyboard_settings);
        String linkString = "siltemeni";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfSettings = answer.indexOf(settings);
        int indexOfLink = answer.indexOf(linkString);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), answer.indexOf(settings), indexOfSettings + settings.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(settingsCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getFirstRowAnswer() {
        String answer = getString(R.string.faq_how_to_change_first_row_answer);
        String letters = getString(R.string.row_additional_letters);
        String numbers = getString(R.string.row_numbers);
        String linkString = "sazlawlarǵa";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfLetters = answer.indexOf(letters);
        int indexOfNumbers = answer.indexOf(numbers);
        int indexOfLink = answer.indexOf(linkString);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfLetters, indexOfLetters + letters.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfNumbers, indexOfNumbers + numbers.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(settingsCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getTemplateAnswer() {
        String answer = getString(R.string.faq_how_to_change_template_answer);
        String light = "jarıq (Material Light)";
        String dark = "qarańǵı (Material Dark)";
        String settingsSection = getString(R.string.keyboard_view_settings);
        String linkString = "sazlawlarǵa";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfLight = answer.indexOf(light);
        int indexOfDark = answer.indexOf(dark);
        int indexOfSettingsSection = answer.indexOf(settingsSection);
        int indexOfLink = answer.indexOf(linkString);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfLight, indexOfLight + light.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfDark, indexOfDark + dark.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfSettingsSection, indexOfSettingsSection + settingsSection.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(settingsCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getSoundAnswer() {
        String answer = getString(R.string.faq_how_to_change_sound_answer);
        String sound = getString(R.string.sound_on_keypress);
        String volume = getString(R.string.volume_on_keypress);
        String linkString = "sazlawlarǵa";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfSound1 = answer.indexOf(sound);
        int indexOfVolume = answer.indexOf(volume);
        int indexOfLink = answer.indexOf(linkString);
        int indexOfSound2 = answer.indexOf(sound, indexOfVolume);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfSound1, indexOfSound1 + sound.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfVolume, indexOfVolume + volume.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfSound2, indexOfSound2 + sound.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(settingsCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getVibrationAnswer() {
        String answer = getString(R.string.faq_how_to_change_vibration_answer);
        String vibrationOn = getString(R.string.vibrate_on_keypress);
        String vibrationStrength = getString(R.string.vibration_strenth_on_keypress);
        String linkString = "sazlawlarǵa";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfVibration1 = answer.indexOf(vibrationOn);
        int indexOfStrength = answer.indexOf(vibrationStrength);
        int indexOfLink = answer.indexOf(linkString);
        int indexOfVibration2 = answer.indexOf(vibrationOn, indexOfStrength);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfVibration1, indexOfVibration1 + vibrationOn.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfStrength, indexOfStrength + vibrationStrength.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), indexOfVibration2, indexOfVibration2 + vibrationOn.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(settingsCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getAboutAnswer() {
        String answer = getString(R.string.faq_how_to_find_about_app_answer);
        String about = getString(R.string.about_keyboard);
        String linkString = "siltemeni";

        SpannableString spannableString = new SpannableString(answer);
        int indexOfAbout = answer.indexOf(about);
        int indexOfLink = answer.indexOf(linkString);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), answer.indexOf(about), indexOfAbout + about.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableString(aboutCallback), answer.indexOf(linkString), indexOfLink + linkString.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener listener;

        ClickableString(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v);
        }
    }
}
