package com.foamyguy.bluefruit_playground;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

public class PermissionActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Log.d(TAG, "permission activity onCreate");

    }

    public void close(View view) {
        finish();
    }

    public void requestPermission(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission state is not granted. Requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!

                Intent i = new Intent(this, PairingActivity.class);
                startActivity(i);
                finish();

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }


    }
}
