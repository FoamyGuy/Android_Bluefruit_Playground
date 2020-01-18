package com.adafruit.bluefruit_playground.activities;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.adafruit.bluefruit_playground.R;


public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "OS is greater than 23");
            // NEW PERMISSIONS
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission state is not granted");
                Intent i = new Intent(this, PermissionActivity.class);
                Log.d(TAG, "starting permission activity");
                startActivity(i);
                finish();
                return;
                //Util.Log("after called finish of  going to permission");
            }
        }



    }






    @Override
    protected void onStop() {
        super.onStop();

        //stopService(bluefruitService);

    }
}
