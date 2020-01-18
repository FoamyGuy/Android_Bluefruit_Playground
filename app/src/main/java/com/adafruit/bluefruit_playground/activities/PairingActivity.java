package com.adafruit.bluefruit_playground.activities;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.BluefruitService;
import com.adafruit.bluefruit_playground.R;
import com.adafruit.bluefruit_playground.ui.ScanResultAdapter;

public class PairingActivity extends Activity {
    private final String TAG = PairingActivity.class.getSimpleName();
    ListView scanResultList;
    ScanResultAdapter scanResultAdapter;

    TextView scanningTxt;
    BluetoothManager bluetoothManager;
    LocationManager locationManager;
    private BluetoothAdapter bluetoothAdapter;


    private static final int REQUEST_ENABLE_BT = 9;

    Intent bluefruitService;
    BroadcastReceiver scanResultReceiver;
    BroadcastReceiver connectionResultReceiver;
    BroadcastReceiver connectionStatusReceiver;

    ProgressDialog progressDialog;

    boolean stopServiceOnStop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

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
            }
        }

        scanResultList = (ListView) findViewById(R.id.scanResultList);
        scanResultAdapter = new ScanResultAdapter(this, -1);
        scanResultList.setAdapter(scanResultAdapter);

        scanningTxt = findViewById(R.id.scanningTxt);

        scanResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent connectIntent = new Intent(BluefruitService.ACTION_ATTEMPT_CONNECT);
                connectIntent.putExtra("scanResult", scanResultAdapter.getItem(position));
                sendBroadcast(connectIntent);


                progressDialog = ProgressDialog.show(view.getContext(), "In progress", "test1");

            }
        });

        IntentFilter statusFilter = new IntentFilter(BluefruitService.ACTION_CONNECTION_STATUS);
        connectionStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int status = intent.getIntExtra("status", -1);
                if (status == BluefruitService.STATE_CONNECTING){
                    progressDialog.setMessage(getString(R.string.scanner_connecting));
                }else if (status == BluefruitService.STATE_CONNECTED){
                    progressDialog.setMessage(getString(R.string.scanner_connected));
                }else if (status == BluefruitService.STATE_DISCOVERING){
                    progressDialog.setMessage(getString(R.string.scanner_discoveringservices));
                }else if (status == BluefruitService.STATE_SERVICES_DISCOVERED){
                    progressDialog.setMessage(getString(R.string.scanner_servicesdisconvered));
                    progressDialog.dismiss();

                    stopServiceOnStop = false;

                    Intent modulesIntent = new Intent(context, ModulesListActivity.class);
                    startActivity(modulesIntent);
                    finish();
                }
            }
        };
        registerReceiver(connectionStatusReceiver, statusFilter);

        IntentFilter filter = new IntentFilter(BluefruitService.ACTION_SCAN_RESULT_AVAILABLE);
        filter.addAction(BluefruitService.ACTION_SCANNING_STOPPED);
        scanResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(BluefruitService.ACTION_SCAN_RESULT_AVAILABLE)) {
                    ScanResult incScanResult = intent.getParcelableExtra("scanResult");
                    Log.i(TAG, "onReceive Scan Result " + incScanResult.getDevice().getAddress());
                    scanResultAdapter.insertOrUpdate(incScanResult);
                }else if(intent.getAction().equals(BluefruitService.ACTION_SCANNING_STOPPED)){
                    //scanningTxt.setVisibility(View.INVISIBLE);
                    scanningTxt.setText(getString(R.string.scanner_press_to_search));
                }
            }
        };
        registerReceiver(scanResultReceiver, filter);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Ensures Bluetooth and Location is available on the device and it is enabled. If not,
        // go to th enable settings activity
        if (bluetoothAdapter == null ||
            !bluetoothAdapter.isEnabled() ||
            !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            stopServiceOnStop = false;
            Intent enableSettingsIntent = new Intent(this, EnableSettingsActivity.class);
            startActivity(enableSettingsIntent);
            finish();

        } else {
            bluefruitService = new Intent(this, BluefruitService.class);
            startService(bluefruitService);

            Intent requestScanIntent = new Intent(BluefruitService.ACTION_REQUEST_SCAN);
            sendBroadcast(requestScanIntent);
            Log.i(TAG, "sent request scan broadcast");
            scanningTxt.setVisibility(View.VISIBLE);
        }



        IntentFilter connectionFilter = new IntentFilter(BluefruitService.ACTION_CHECK_CONNECTION_RESULT);
        connectionResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int connectedState = intent.getIntExtra("connected", BluefruitService.STATE_DISCONNECTED);
                Log.i(TAG, "onReceive connected state " + connectedState);
                unregisterReceiver(this);
                if (connectedState == BluefruitService.STATE_CONNECTED) {
                    stopServiceOnStop = false;
                    Intent modulesListIntent = new Intent(PairingActivity.this, ModulesListActivity.class);
                    startActivity(modulesListIntent);
                    finish();
                }

            }
        };
        registerReceiver(connectionResultReceiver, connectionFilter);

        Intent checkConnectionIntent = new Intent(BluefruitService.ACTION_CHECK_CONNECTION);
        sendBroadcast(checkConnectionIntent);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(scanResultReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }

        try {
            unregisterReceiver(connectionStatusReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }

        try {
            unregisterReceiver(connectionResultReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }
        if(stopServiceOnStop) {
            Intent disconnectIntent = new Intent(BluefruitService.ACTION_DISCONNECT);
            sendBroadcast(disconnectIntent);

            Intent exitServiceIntent = new Intent(BluefruitService.ACTION_STOP_SERVICE);
            sendBroadcast(exitServiceIntent);
        }

        finish();
    }

    public void startAboutActivity(View view) {
        stopServiceOnStop = false;
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void startScan(View view) {
        Intent requestScanIntent = new Intent(BluefruitService.ACTION_REQUEST_SCAN);
        sendBroadcast(requestScanIntent);
        Log.i(TAG, "sent request scan broadcast");
        //scanningTxt.setVisibility(View.VISIBLE);
        scanningTxt.setText(R.string.scanner_searching);
    }

    public void exit(View view) {
        finish();
    }

    public void scanningTxtClick(View view) {
        if(scanningTxt.getText().toString().equals(getString(R.string.scanner_press_to_search))){
            startScan(view);
        }
    }

    public void startPairingHelp(View view) {
        Intent pairingHelpIntent = new Intent(this, PairingHelpActivity.class);
        startActivity(pairingHelpIntent);
    }
}
