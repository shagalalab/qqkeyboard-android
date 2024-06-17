package com.shagalalab.qqkeyboard.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.shagalalab.qqkeyboard.R

class ImePreferencesActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings_name)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        supportFragmentManager.beginTransaction().replace(android.R.id.content, PreferencesFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}