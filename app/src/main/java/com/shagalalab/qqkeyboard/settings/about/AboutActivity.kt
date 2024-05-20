package com.shagalalab.qqkeyboard.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shagalalab.qqkeyboard.BuildConfig
import com.shagalalab.qqkeyboard.R

class AboutActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(R.string.about_qqkeyboard)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        setContentView(R.layout.activity_about)

        (findViewById<View>(R.id.about_textview_version_number) as TextView).text =
            String.format(getString(R.string.version), BuildConfig.VERSION_NAME)
        findViewById<View>(R.id.google_play_banner).setOnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("market://search?q=pub:Shag'ala Lab"))
            startActivity(intent)
        }
    }
}