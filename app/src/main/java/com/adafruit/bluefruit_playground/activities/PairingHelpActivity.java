package com.adafruit.bluefruit_playground.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.R;

import static com.adafruit.bluefruit_playground.ui.TextLinker.addLink;

public class PairingHelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_help);

        TextView tip1DetailTxt = findViewById(R.id.scannerTip1Detail);
        addLink(tip1DetailTxt, getString(R.string.scannerproblems_tip1_link_text), getString(R.string.scannerproblems_tip1_link_url));
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
