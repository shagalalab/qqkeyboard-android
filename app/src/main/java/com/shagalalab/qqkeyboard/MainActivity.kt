package com.shagalalab.qqkeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shagalalab.qqkeyboard.help.HelpActivity
import com.shagalalab.qqkeyboard.settings.ImePreferencesActivity
import com.shagalalab.qqkeyboard.settings.about.AboutActivity

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        findViewById<View>(R.id.enable_keyboard).setOnClickListener { openEnableKeyboard() }
        findViewById<View>(R.id.change_default_keyboard).setOnClickListener {
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (isInputEnabled()) {
                imm.showInputMethodPicker()
            } else {
                Snackbar.make(
                    findViewById(R.id.enable_keyboard),
                    R.string.enable_keyboard_first,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.enable) { openEnableKeyboard() }
                    .show()
            }
        }
        findViewById<View>(R.id.keyboard_settings).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ImePreferencesActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.about_keyboard).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.help).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HelpActivity::class.java
                )
            )
        }

    }

    private fun openEnableKeyboard() {
        startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
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
}