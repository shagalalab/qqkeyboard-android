package com.shagalalab.qqkeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.shagalalab.qqkeyboard.help.HelpActivity;
import com.shagalalab.qqkeyboard.settings.ImePreferencesActivity;
import com.shagalalab.qqkeyboard.settings.about.AboutActivity;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.enable_keyboard).setOnClickListener(v -> openEnableKeyboard());
        findViewById(R.id.change_default_keyboard).setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && isInputEnabled()) {
                imm.showInputMethodPicker();
            } else {
                Snackbar.make(findViewById(R.id.enable_keyboard), R.string.enable_keyboard_first, Snackbar.LENGTH_LONG)
                        .setAction(R.string.enable, v1 -> openEnableKeyboard())
                        .show();
            }
        });
        findViewById(R.id.keyboard_settings).setOnClickListener(v -> startActivity(new Intent(this, ImePreferencesActivity.class)));
        findViewById(R.id.about_keyboard).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        findViewById(R.id.help).setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
    }

    private void openEnableKeyboard() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
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
}
