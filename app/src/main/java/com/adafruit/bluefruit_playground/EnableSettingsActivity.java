package com.adafruit.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class EnableSettingsActivity extends AppCompatActivity {
    private final String TAG = EnableSettingsActivity.class.getSimpleName();
    private int REQUEST_ENABLE_BT = 1;

    TextView detailTxt;

    private boolean requestingLocation = true;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_settings);
        detailTxt = findViewById(R.id.detailTxt);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


    }

    @Override
    protected void onResume() {
        super.onResume();
        int servicesComplete = 0;

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            detailTxt.setText(R.string.NSBluetoothAlwaysUsageDescription);
            requestingLocation = false;
        }else{
            servicesComplete++;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            detailTxt.setText(R.string.settings_detail_location);
            requestingLocation= true;
        }else{
            servicesComplete++;
        }

        if(servicesComplete == 2){
            Intent pairingIntent = new Intent(this, PairingActivity.class);
            startActivity(pairingIntent);
            finish();
            return;
        }
        Log.d(TAG, "onResume()");

    }

    public void requestEnableSettings(View view) {

        if(requestingLocation){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    public void close(View view) {
        finish();
    }
}
