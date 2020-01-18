package com.adafruit.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        String helpStr = getIntent().getStringExtra("helpStr");
        TextView helpTxt = findViewById(R.id.helpTxt);

        helpTxt.setText(helpStr);
    }

    public void goBack(View view) {
        Intent returnToIntent = getIntent().getParcelableExtra("returnTo");
        startActivity(returnToIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }
}
