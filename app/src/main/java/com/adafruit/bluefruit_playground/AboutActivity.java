package com.adafruit.bluefruit_playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adafruit.bluefruit_playground.TextLinker.addLink;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutTxt = findViewById(R.id.aboutTxt);
        addLink(aboutTxt, getString(R.string.about_link0_text), getString(R.string.about_link0_url));
        addLink(aboutTxt, getString(R.string.about_link1_text), getString(R.string.about_link1_url));
        addLink(aboutTxt, getString(R.string.about_link2_text), getString(R.string.about_link2_url));
    }

    public void backToPairing(View view) {
        Intent pairingIntent = new Intent(this, PairingActivity.class);
        startActivity(pairingIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backToPairing(null);
    }
}
