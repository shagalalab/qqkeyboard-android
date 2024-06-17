package com.shagalalab.qqkeyboard.util

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.compose.compiler.plugins.kotlin.EmptyFunctionMetrics.packageName

 fun Activity.openEnableKeyboard() {
    startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
}

 fun Activity.isInputEnabled(): Boolean {
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