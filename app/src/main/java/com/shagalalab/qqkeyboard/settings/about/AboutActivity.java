package com.shagalalab.qqkeyboard.settings.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.shagalalab.qqkeyboard.BuildConfig;
import com.shagalalab.qqkeyboard.R;
import com.shagalalab.qqkeyboard.util.ViewUtils;

/**
 * Created by atabek on 14/12/16.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.about_qqkeyboard);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.about);

        ((TextView) findViewById(R.id.version)).setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
        findViewById(R.id.google_play_banner).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=pub:Shag'ala Lab"));
            startActivity(intent);
        });

        ViewUtils.applyInsetsToView(findViewById(android.R.id.content));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
