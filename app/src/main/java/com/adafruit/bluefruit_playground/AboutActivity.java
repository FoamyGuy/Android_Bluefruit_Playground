package com.adafruit.bluefruit_playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Add a link to the TextView which is given.
     *
     * @param textView       the field containing the text
     * @param patternToMatch a regex pattern to put a link around
     * @param link           the link to add
     */
    public static void addLink(TextView textView, String patternToMatch,
                               final String link) {
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return link;
            }
        };
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
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
