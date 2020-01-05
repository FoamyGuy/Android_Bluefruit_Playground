package com.foamyguy.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.companion.BluetoothDeviceFilter;
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
                //Util.Log("after called finish of  going to permission");
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
                    progressDialog.setMessage("Connecting");
                }else if (status == BluefruitService.STATE_CONNECTED){
                    progressDialog.setMessage("Connected");
                }else if (status == BluefruitService.STATE_DISCOVERING){
                    progressDialog.setMessage("Discovering Services");
                }else if (status == BluefruitService.STATE_SERVICES_DISCOVERED){
                    progressDialog.setMessage("Services Discovered");
                    progressDialog.dismiss();
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
                    scanningTxt.setText("Press refresh icon to scan");
                }
            }
        };
        registerReceiver(scanResultReceiver, filter);

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (bluetoothAdapter == null ||
            !bluetoothAdapter.isEnabled() ||
            !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

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
    }

    public void startScan(View view) {
        Intent requestScanIntent = new Intent(BluefruitService.ACTION_REQUEST_SCAN);
        sendBroadcast(requestScanIntent);
        Log.i(TAG, "sent request scan broadcast");
        //scanningTxt.setVisibility(View.VISIBLE);
        scanningTxt.setText("Scanning...");
    }
}
