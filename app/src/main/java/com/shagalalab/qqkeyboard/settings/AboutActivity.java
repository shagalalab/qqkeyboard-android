package com.shagalalab.qqkeyboard.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shagalalab.qqkeyboard.BuildConfig;
import com.shagalalab.qqkeyboard.R;

/**
 * Created by atabek on 14/12/16.
 */

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.about_keyboard);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.about);

        ((TextView) findViewById(R.id.version)).setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
        findViewById(R.id.google_play_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://search?q=pub:Shag'ala Lab"));
                startActivity(intent);
            }
        });
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
