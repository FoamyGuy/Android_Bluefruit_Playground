package com.adafruit.bluefruit_playground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LightActivity extends ModuleActivity {
    private final String TAG = LightActivity.class.getSimpleName();
    BroadcastReceiver lightDataReceiver;
    TextView lightTxt;
    ProgressBar lightProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        lightTxt = (TextView) findViewById(R.id.lightTxt);
        lightProgress = (ProgressBar) findViewById(R.id.lightProgress);

        IntentFilter lightDataFilter = new IntentFilter(BluefruitService.ACTION_LIGHT_DATA_AVAILABLE);
        lightDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int light = intent.getIntExtra("light", 0);
                Log.i(TAG, "onReceive temperature data: " + light);
                lightProgress.setProgress(light);
                lightTxt.setText(String.valueOf(light));
            }
        };
        registerReceiver(lightDataReceiver, lightDataFilter);

        Intent enableLightNotify = new Intent(BluefruitService.ACTION_ENABLE_LIGHT_NOTIFY);
        sendBroadcast(enableLightNotify);
    }

    public void startHelpActivity(View view) {
        Intent returnToIntent = new Intent(this, LightActivity.class);
        stopServiceOnStop = false;
        Intent helpIntent = new Intent(this, HelpActivity.class);
        helpIntent.putExtra("helpStr", getString(R.string.lightsensor_help));
        helpIntent.putExtra("returnTo", returnToIntent);
        startActivity(helpIntent);
    }


    @Override
    protected void onStop() {
        super.onStop();

        Intent i = new Intent(BluefruitService.ACTION_DISABLE_LIGHT_NOTIFY);
        sendBroadcast(i);
        Log.d(TAG, "sent disable light notify");

        try {
            unregisterReceiver(lightDataReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }
    }
}
