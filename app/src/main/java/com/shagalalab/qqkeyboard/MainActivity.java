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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.enable_keyboard).setOnClickListener(this);
        findViewById(R.id.change_default_keyboard).setOnClickListener(this);
        findViewById(R.id.keyboard_settings).setOnClickListener(this);
        findViewById(R.id.about_keyboard).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enable_keyboard:
                openEnableKeyboard();
                break;
            case R.id.change_default_keyboard:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && isInputEnabled()) {
                    imm.showInputMethodPicker();
                } else {
                    Snackbar.make(findViewById(R.id.enable_keyboard), R.string.enable_keyboard_first, Snackbar.LENGTH_LONG)
                        .setAction(R.string.enable, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openEnableKeyboard();
                            }
                        })
                        .show();
                }
                break;
            case R.id.keyboard_settings:
                startActivity(new Intent(this, ImePreferencesActivity.class));
                break;
            case R.id.about_keyboard:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            default:
                break;
        }
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
