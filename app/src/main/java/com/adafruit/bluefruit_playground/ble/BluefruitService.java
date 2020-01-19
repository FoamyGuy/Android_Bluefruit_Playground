package com.adafruit.bluefruit_playground.ble;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.adafruit.bluefruit_playground.R;
import com.adafruit.bluefruit_playground.activities.modules.NeopixelActivity;
import com.adafruit.bluefruit_playground.neopixelanimations.JSONLightSequence;
import com.adafruit.bluefruit_playground.neopixelanimations.NeopixelSequence;
import com.adafruit.bluefruit_playground.neopixelanimations.PulseLightSequence;
import com.adafruit.bluefruit_playground.neopixelanimations.RotateLightSequence;


import org.json.JSONArray;
import org.json.JSONException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.core.app.NotificationCompat;

public class BluefruitService extends Service {


    private final String TAG = BluefruitService.class.getSimpleName();
    public static final UUID UUID_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_PERIOD = UUID.fromString("adaf0001-c332-42a8-93bd-25e905756cb8");

    public static final UUID UUID_BUTTONS_SERVICE = UUID.fromString("adaf0600-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_BUTTONS_CHARACTERISTIC = UUID.fromString("adaf0601-c332-42a8-93bd-25e905756cb8");

    public static final UUID UUID_LIGHT_SERVICE = UUID.fromString("adaf0300-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_LIGHT_CHARACTERISTIC = UUID.fromString("adaf0301-c332-42a8-93bd-25e905756cb8");

    public static final UUID UUID_ACCELEROMETER_SERVICE = UUID.fromString("adaf0200-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_ACCELEROMETER_CHARACTERISTIC = UUID.fromString("adaf0201-c332-42a8-93bd-25e905756cb8");

    public static final UUID UUID_TEMP_SERVICE = UUID.fromString("adaf0100-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_TEMP_CHARACTERISTIC = UUID.fromString("adaf0101-c332-42a8-93bd-25e905756cb8");

    public static final UUID UUID_NEOPIXEL_SERVICE = UUID.fromString("adaf0900-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_TONE_SERVICE = UUID.fromString("adaf0c00-c332-42a8-93bd-25e905756cb8");
    public static final String UUID_NEOPIXEL_PIN_CHARACTERISTIC = "adaf0901-c332-42a8-93bd-25e905756cb8";
    public static final String UUID_NEOPIXEL_TYPE_CHARACTERISTIC = "adaf0902-c332-42a8-93bd-25e905756cb8";
    public static final UUID UUID_NEOPIXEL_DATA_CHARACTERISTIC = UUID.fromString("adaf0903-c332-42a8-93bd-25e905756cb8");
    public static final UUID UUID_TONE_CHARACTERISTIC = UUID.fromString("adaf0c01-c332-42a8-93bd-25e905756cb8");

    public static final boolean SWITCH_POSITION_LEFT = false;
    public static final boolean SWITCH_POSITION_RIGHT = true;
    public static final boolean BUTTON_POSITION_PRESSED = true;
    public static final boolean BUTTON_POSITION_UNPRESSED = false;

    public final static String ACTION_GATT_CONNECTED = "com.adafruit.bluefruit_playground.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.adafruit.bluefruit_playground.ACTION_GATT_DISCONNECTED";
    

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;

    private int animationDelay = 0;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCOVERING = 3;
    public static final int STATE_SERVICES_DISCOVERED = 4;

    public static final int STATE_WRITING = 3;
    public static final int STATE_NOT_WRITING = 4;

    private BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;
    private int writingState = STATE_NOT_WRITING;

    public static final String ACTION_REQUEST_SCAN = "com.adafruit.bluefruit_playground.ACTION_REQUEST_SCAN";
    public static final String ACTION_SCANNING_STOPPED = "com.adafruit.bluefruit_playground.ACTION_SCANNING_STOPPED";
    public static final String ACTION_SCAN_RESULT_AVAILABLE = "com.adafruit.bluefruit_playground.ACTION_SCAN_RESULT_AVAILABLE";


    public static final String ACTION_ATTEMPT_CONNECT = "com.adafruit.bluefruit_playground.ACTION_ATTEMPT_CONNECT";
    public static final String ACTION_DISCONNECT = "com.adafruit.bluefruit_playground.ACTION_DISCONNECT";

    public static final String ACTION_CHECK_CONNECTION = "com.adafruit.bluefruit_playground.ACTION_CHECK_CONNECTION";
    public static final String ACTION_CHECK_CONNECTION_RESULT = "com.adafruit.bluefruit_playground.ACTION_CHECK_CONNECTION_RESULT";
    public static final String ACTION_STOP_SERVICE = "com.adafruit.bluefruit_playground.ACTION_STOP_SERVICE";

    public static final String ACTION_CONNECTION_STATUS = "com.adafruit.bluefruit_playground.ACTION_CONNECTION_STATUS";


    public static final String ACTION_ENABLE_TEMPERATURE_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_ENABLE_TEMPERATURE_NOTIFY";
    public static final String ACTION_ENABLE_ACCELEROMETER_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_ENABLE_ACCELEROMETER_NOTIFY";
    public static final String ACTION_ENABLE_BUTTONS_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_ENABLE_BUTTONS_NOTIFY";
    public static final String ACTION_ENABLE_LIGHT_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_ENABLE_LIGHT_NOTIFY";

    public static final String ACTION_DISABLE_TEMPERATURE_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_DISABLE_TEMPERATURE_NOTIFY";
    public static final String ACTION_DISABLE_LIGHT_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_DISABLE_LIGHT_NOTIFY";
    public static final String ACTION_DISABLE_BUTTONS_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_DISABLE_BUTTONS_NOTIFY";
    public static final String ACTION_DISABLE_ACCELEROMETER_NOTIFY = "com.adafruit.bluefruit_playground.ACTION_DISABLE_ACCELEROMETER_NOTIFY";

    public static final String ACTION_PLAY_TONE = "com.adafruit.bluefruit_playground.ACTION_PLAY_TONE";
    public static final String ACTION_SET_NEOPIXELS = "com.adafruit.bluefruit_playground.ACTION_SET_NEOPIXELS";


    public static final String ACTION_PLAY_NEOPIXEL_ANIMATION = "com.adafruit.bluefruit_playground.ACTION_PLAY_NEOPIXEL_ANIMATION";
    public static final String ACTION_SET_NEOPIXEL_ANIMATION_DELAY = "com.adafruit.bluefruit_playground.ACTION_SET_NEOPIXEL_ANIMATION_DELAY";
    public static final String ACTION_STOP_NEOPIXEL_ANIMATION = "com.adafruit.bluefruit_playground.ACTION_STOP_NEOPIXEL_ANIMATION";

    public static final String ACTION_LIGHT_DATA_AVAILABLE = "com.adafruit.bluefruit_playground.ACTION_LIGHT_DATA_AVAILABLE";
    public static final String ACTION_ACCELEROMETER_DATA_AVAILABLE = "com.adafruit.bluefruit_playground.ACTION_ACCELEROMETER_DATA_AVAILABLE";
    public static final String ACTION_BUTTONS_DATA_AVAILABLE = "com.adafruit.bluefruit_playground.ACTION_BUTTONS_DATA_AVAILABLE";
    public static final String ACTION_TEMPERATURE_DATA_AVAILABLE = "com.adafruit.bluefruit_playground.ACTION_TEMPERATURE_DATA_AVAILABLE";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    ScanCallback mLeScanCallback;

    BluetoothGattCallback gattCallback;

    BroadcastReceiver bluefruitReceiver;


    ArrayList<Intent> toneActions;

    ArrayList<Intent> neopixelActions;

    Intent nextNeopixelAction;

    boolean animatingNeopixels;

    NeopixelSequence curSequence;

    NotificationManager notificationManager;

    private BluetoothGattCharacteristic neopixelDataCharacteristic;
    BluetoothGattCharacteristic toneCharacteristic;
    BluetoothGattCharacteristic tempCharacteristic;
    BluetoothGattCharacteristic lightCharacteristic;
    BluetoothGattCharacteristic buttonsCharacteristic;
    BluetoothGattCharacteristic accelerometerCharacteristic;
    BluetoothGattCharacteristic accelerometerPeriodCharacteristic;

    BluetoothLeScanner bluetoothLeScanner;

    public BluefruitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        toneActions = new ArrayList<>();
        neopixelActions = new ArrayList<>();

        handler = new Handler();

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        showNotification();

        //private LeDeviceListAdapter leDeviceListAdapter;

        mLeScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.d(TAG, result.getDevice().getAddress());
                Log.d(TAG, "name: " + result.getDevice().getName());

                if (null != result.getDevice().getName() &&
                        result.getDevice().getName().equals("Bluefruit52") &&
                        connectionState == STATE_DISCONNECTED) {

                    //scanLeDevice(false);
                    //bluetoothGatt = result.getDevice().connectGatt(BluefruitService.this, true, gattCallback);

                    Intent scanResultIntent = new Intent(ACTION_SCAN_RESULT_AVAILABLE);
                    scanResultIntent.putExtra("scanResult", result);
                    sendBroadcast(scanResultIntent);
                }

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };


        IntentFilter bluefruitReceiverFilter = new IntentFilter(ACTION_SET_NEOPIXELS);
        bluefruitReceiverFilter.addAction(ACTION_PLAY_TONE);
        bluefruitReceiverFilter.addAction(ACTION_ATTEMPT_CONNECT);
        bluefruitReceiverFilter.addAction(ACTION_REQUEST_SCAN);
        bluefruitReceiverFilter.addAction(ACTION_STOP_SERVICE);
        bluefruitReceiverFilter.addAction(ACTION_CHECK_CONNECTION);
        bluefruitReceiverFilter.addAction(ACTION_ENABLE_TEMPERATURE_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_DISABLE_TEMPERATURE_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_ENABLE_LIGHT_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_DISABLE_LIGHT_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_ENABLE_BUTTONS_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_DISABLE_BUTTONS_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_ENABLE_ACCELEROMETER_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_DISABLE_ACCELEROMETER_NOTIFY);
        bluefruitReceiverFilter.addAction(ACTION_PLAY_NEOPIXEL_ANIMATION);
        bluefruitReceiverFilter.addAction(ACTION_SET_NEOPIXEL_ANIMATION_DELAY);
        bluefruitReceiverFilter.addAction(ACTION_STOP_NEOPIXEL_ANIMATION);
        bluefruitReceiverFilter.addAction(ACTION_DISCONNECT);


        bluefruitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(ACTION_SET_NEOPIXELS)) {
                    if(animatingNeopixels){
                        animatingNeopixels = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (writingState == STATE_NOT_WRITING) {
                                    setNeopixels(intent);
                                } else {
                                    neopixelActions.add(intent);
                                }
                            }
                        }, 300);
                    }else{
                        if (writingState == STATE_NOT_WRITING) {
                            setNeopixels(intent);
                        } else {
                            //neopixelActions.add(intent);
                            nextNeopixelAction = intent;
                        }
                    }


                } else if (action.equals(ACTION_PLAY_TONE)) {
                    if (writingState == STATE_NOT_WRITING) {
                        playTone(intent);
                    } else {
                        toneActions.add(intent);
                    }

                } else if (action.equals(ACTION_ATTEMPT_CONNECT)) {

                    Intent connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_CONNECTING);
                    sendBroadcast(connectionStatusIntent);

                    ScanResult result = intent.getParcelableExtra("scanResult");
                    bluetoothGatt = result.getDevice().connectGatt(BluefruitService.this, true, gattCallback);

                } else if (action.equals(ACTION_REQUEST_SCAN)) {

                    Log.d(TAG, "receded REQUEST_SCAN");

                    if (!mScanning) {
                        scanLeDevice(true);
                    }

                } else if (action.equals(ACTION_STOP_SERVICE)) {
                    Log.d(TAG, "received stopService");
                    try {
                        bluetoothLeScanner.stopScan(mLeScanCallback);
                    } catch (IllegalStateException e) {
                        //e.printStackTrace();
                        // BT Adapter got turned off before scan period was complete.
                    }

                    if(bluetoothGatt != null) {
                        bluetoothGatt.close();
                    }
                    notificationManager.cancelAll();
                    stopSelf();
                } else if (action.equals(ACTION_CHECK_CONNECTION)) {
                    Intent checkConnResult = new Intent(ACTION_CHECK_CONNECTION_RESULT);
                    checkConnResult.putExtra("connected", connectionState);
                    sendBroadcast(checkConnResult);
                } else if (action.equals(ACTION_ENABLE_TEMPERATURE_NOTIFY)) {
                    enableCharacteristicNotifications(tempCharacteristic, true);
                } else if (action.equals(ACTION_DISABLE_TEMPERATURE_NOTIFY)) {
                    Log.d(TAG, "received disable temp notify");
                    enableCharacteristicNotifications(tempCharacteristic, false);
                } else if (action.equals(ACTION_ENABLE_LIGHT_NOTIFY)) {
                    enableCharacteristicNotifications(lightCharacteristic, true);
                } else if (action.equals(ACTION_DISABLE_LIGHT_NOTIFY)) {
                    Log.d(TAG, "received disable temp notify");
                    enableCharacteristicNotifications(lightCharacteristic, false);
                } else if (action.equals(ACTION_ENABLE_BUTTONS_NOTIFY)) {
                    enableCharacteristicNotifications(buttonsCharacteristic, true);
                } else if (action.equals(ACTION_DISABLE_BUTTONS_NOTIFY)) {
                    Log.d(TAG, "received disable temp notify");
                    enableCharacteristicNotifications(buttonsCharacteristic, false);
                } else if (action.equals(ACTION_ENABLE_ACCELEROMETER_NOTIFY)) {
                    enableCharacteristicNotifications(accelerometerCharacteristic, true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            accelerometerPeriodCharacteristic.setValue(200, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                            boolean listenResult = bluetoothGatt.writeCharacteristic(accelerometerPeriodCharacteristic);
                            Log.d(TAG, "listenresult: " + listenResult);
                        }
                    }, 1000);
                } else if (action.equals(ACTION_DISABLE_ACCELEROMETER_NOTIFY)) {
                    Log.d(TAG, "received disable accel notify");
                    enableCharacteristicNotifications(accelerometerCharacteristic, false);
                } else if (action.equals(ACTION_PLAY_NEOPIXEL_ANIMATION)) {
                    String animationType = intent.getStringExtra("animationType");
                    int delay = intent.getIntExtra("delay", 0);
                    animationDelay = delay;
                    if (animationType.equals("sweep")) {
                        curSequence = new JSONLightSequence(context, "sweep.json", false);
                    } else if (animationType.equals("pulse")) {
                        curSequence = new PulseLightSequence();
                    } else if (animationType.equals("rotate")) {
                        curSequence = new RotateLightSequence();
                    } else if (animationType.equals("sizzle")) {
                        curSequence = new JSONLightSequence(context, "alternating_pixels.json", true);
                    } else {
                        curSequence = new RotateLightSequence();
                    }
                    animatingNeopixels = true;

                    setNeopixels(curSequence.getCurrentFrame());
                } else if (action.equals(ACTION_SET_NEOPIXEL_ANIMATION_DELAY)) {
                    animationDelay = intent.getIntExtra("delay", 0);
                } else if (action.equals(ACTION_STOP_NEOPIXEL_ANIMATION)) {
                    Log.d(TAG, "received disable temp notify");
                    animatingNeopixels = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            turnOffNeopixels();
                        }
                    }, 300);


                } else if (action.equals(ACTION_DISCONNECT)) {
                    if(bluetoothGatt != null) {
                        bluetoothGatt.close();
                    }
                    Intent connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_DISCONNECTED);
                    sendBroadcast(connectionStatusIntent);
                    connectionState = STATE_DISCONNECTED;
                }
            }
        };

        registerReceiver(bluefruitReceiver, bluefruitReceiverFilter);

        if (!mScanning) {
            scanLeDevice(true);
        }

        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Intent connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_CONNECTED);
                    sendBroadcast(connectionStatusIntent);

                    connectionState = STATE_CONNECTED;

                    boolean startedDiscover = gatt.discoverServices();
                    Log.i(TAG, "Connected to GATT server.");
                    Log.i(TAG, "Attempting to start service discovery:" + startedDiscover);

                    connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_DISCOVERING);
                    sendBroadcast(connectionStatusIntent);

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Intent connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_DISCONNECTED);
                    sendBroadcast(connectionStatusIntent);

                    Log.i(TAG, "Disconnected from GATT server.");
                    //broadcastUpdate(intentAction);
                }
            }

            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                    BluetoothGattService neoPixelService = gatt.getService(UUID_NEOPIXEL_SERVICE);
                    BluetoothGattService toneService = gatt.getService(UUID_TONE_SERVICE);
                    BluetoothGattService tempService = gatt.getService(UUID_TEMP_SERVICE);
                    BluetoothGattService lightService = gatt.getService(UUID_LIGHT_SERVICE);
                    BluetoothGattService buttonsService = gatt.getService(UUID_BUTTONS_SERVICE);
                    BluetoothGattService accelerometerService = gatt.getService(UUID_ACCELEROMETER_SERVICE);

                    neopixelDataCharacteristic = neoPixelService.getCharacteristic(UUID_NEOPIXEL_DATA_CHARACTERISTIC);
                    toneCharacteristic = toneService.getCharacteristic(UUID_TONE_CHARACTERISTIC);
                    tempCharacteristic = tempService.getCharacteristic(UUID_TEMP_CHARACTERISTIC);
                    lightCharacteristic = lightService.getCharacteristic(UUID_LIGHT_CHARACTERISTIC);
                    buttonsCharacteristic = buttonsService.getCharacteristic(UUID_BUTTONS_CHARACTERISTIC);
                    accelerometerCharacteristic = accelerometerService.getCharacteristic(UUID_ACCELEROMETER_CHARACTERISTIC);
                    accelerometerPeriodCharacteristic = accelerometerService.getCharacteristic(UUID_PERIOD);

                    Intent connectionStatusIntent = new Intent(ACTION_CONNECTION_STATUS);
                    connectionStatusIntent.putExtra("status", STATE_SERVICES_DISCOVERED);
                    sendBroadcast(connectionStatusIntent);

                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                Log.d(TAG, "onCharacteristicRead status: " + status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG, "onCharacteristicWrite " + characteristic.getUuid() + " status: " + status);
                writingState = STATE_NOT_WRITING;
                if (characteristic.getUuid().equals(BluefruitService.UUID_TONE_CHARACTERISTIC)) {
                    if (toneActions.size() > 0) {
                        playTone(toneActions.get(0));
                        toneActions.remove(0);
                    }
                }
                if (characteristic.getUuid().equals(BluefruitService.UUID_NEOPIXEL_DATA_CHARACTERISTIC)) {
                    if (animatingNeopixels) {
                        if (animationDelay == 0) {
                            setNeopixels(curSequence.getCurrentFrame());
                        }else{
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setNeopixels(curSequence.getCurrentFrame());
                                }
                            }, animationDelay);
                        }
                    }

                    /*if (neopixelActions.size() > 0){
                        setNeopixels(neopixelActions.get(0));
                        neopixelActions.remove(0);
                    }*/
                    if(nextNeopixelAction != null){
                        setNeopixels(nextNeopixelAction);
                        nextNeopixelAction = null;
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.d(TAG, "characteristic " + characteristic.getUuid() + " changed");
                //Log.d(TAG, "char val: " + characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 0));

                if (characteristic.getUuid().equals(BluefruitService.UUID_TEMP_CHARACTERISTIC)) {
                    // https://stackoverflow.com/questions/42113986/android-bluetooth-le-read-float-characteristic
                    float curVal = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Intent tempDataIntent = new Intent(ACTION_TEMPERATURE_DATA_AVAILABLE);
                    tempDataIntent.putExtra("temperature", curVal);
                    sendBroadcast(tempDataIntent);
                    //Log.d(TAG, "char val: " + curVal);
                }
                if (characteristic.getUuid().equals(BluefruitService.UUID_LIGHT_CHARACTERISTIC)) {
                    // https://stackoverflow.com/questions/42113986/android-bluetooth-le-read-float-characteristic
                    float curVal = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Intent lightDataIntent = new Intent(ACTION_LIGHT_DATA_AVAILABLE);
                    lightDataIntent.putExtra("light", (int) curVal);
                    sendBroadcast(lightDataIntent);
                    //Log.d(TAG, "char val: " + curVal);
                }
                if (characteristic.getUuid().equals(BluefruitService.UUID_BUTTONS_CHARACTERISTIC)) {
                    // https://stackoverflow.com/questions/9854166/declaring-an-unsigned-int-in-java
                    int intValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                    long unsignedValue = intValue & 0xffffffffl;
                    String stringValue = Long.toBinaryString(unsignedValue);
                    boolean switchPosition;
                    if (stringValue.charAt(stringValue.length() - 1) == '0') {
                        switchPosition = SWITCH_POSITION_RIGHT;
                    } else {
                        switchPosition = SWITCH_POSITION_LEFT;
                    }
                    boolean btnA = BUTTON_POSITION_UNPRESSED;
                    boolean btnB = BUTTON_POSITION_UNPRESSED;
                    boolean switchOnly = false;

                    if (stringValue.length() == 1) {
                        btnA = BUTTON_POSITION_UNPRESSED;
                        btnB = BUTTON_POSITION_UNPRESSED;
                        switchOnly = true;
                    } else if (stringValue.length() == 2) {
                        if (stringValue.charAt(0) == '1') {
                            btnA = BUTTON_POSITION_PRESSED;
                        } else {
                            btnA = BUTTON_POSITION_UNPRESSED;
                        }
                        btnB = BUTTON_POSITION_UNPRESSED;
                    } else if (stringValue.length() == 3) {
                        if (stringValue.charAt(0) == '1') {
                            btnB = BUTTON_POSITION_PRESSED;
                        }

                        if (stringValue.charAt(1) == '1') {
                            btnA = BUTTON_POSITION_PRESSED;
                        }
                    }

                    Log.d(TAG, "buttons val: " + stringValue);

                    Intent buttonDataIntent = new Intent(ACTION_BUTTONS_DATA_AVAILABLE);
                    buttonDataIntent.putExtra("switch", switchPosition);
                    buttonDataIntent.putExtra("btnA", btnA);
                    buttonDataIntent.putExtra("btnB", btnB);
                    if (switchOnly){
                        buttonDataIntent.putExtra("switchOnly", true);
                    }
                    sendBroadcast(buttonDataIntent);
                    //Log.d(TAG, "char val: " + curVal);
                }
                if (characteristic.getUuid().equals(BluefruitService.UUID_ACCELEROMETER_CHARACTERISTIC)) {

                    //https://stackoverflow.com/questions/42113986/android-bluetooth-le-read-float-characteristic

                    float xVal = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat(0);
                    //Log.d(TAG, "x val: " + xVal);
                    float yVal = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat(4);
                    //Log.d(TAG, "y val: " + yVal);
                    float zVal = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat(8);
                    //Log.d(TAG, "z val: " + zVal);

                    Intent accelDataIntent = new Intent(ACTION_ACCELEROMETER_DATA_AVAILABLE);
                    accelDataIntent.putExtra("x", xVal);
                    accelDataIntent.putExtra("y", yVal);
                    accelDataIntent.putExtra("z", zVal);
                    sendBroadcast(accelDataIntent);
                    //Log.d(TAG, "char val: " + curVal);
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.d(TAG, "onDescriptorWrite " + descriptor.getUuid() + " status: " + status);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);

                Log.d(TAG, "onDescriptorRead " + descriptor.getUuid() + " status: " + status);
            }
        };
    }

    private void enableCharacteristicNotifications(BluetoothGattCharacteristic characteristic, boolean enabled) {
        BluetoothGattDescriptor cccdDescriptor = characteristic.getDescriptor(UUID_CCCD);

        Log.d(TAG, "descriptor: " + cccdDescriptor.getUuid());
        //Log.d(TAG, "descriptor perm: " + cccdDescriptor.getPermissions());

        boolean setValResult;
        if (enabled) {
            setValResult = cccdDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            setValResult = cccdDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        Log.d(TAG, "setValResult: " + setValResult);

        boolean descWriteResult = bluetoothGatt.writeDescriptor(cccdDescriptor);
        //Log.d(TAG, "descriptor val : " + desc.getValue());
        Log.d(TAG, "descriptor write result : " + descWriteResult);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean listenResult = bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
                Log.d(TAG, "listenresult: " + listenResult);
            }
        }, 2000);
    }

    private void showNotification() {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(ACTION_STOP_SERVICE);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "bluefruit_service");
        // build notification
        // the addAction re-use the same intent to keep the example short
        mBuilder
                .setContentTitle("Bluefruit Playground Service")
                .setSmallIcon(R.drawable.icon)
                .setChannelId("bluefruit_service")
                .setAutoCancel(true);


        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "bluefruit_service";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Bluefruit Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        notificationManager.notify(0, mBuilder.build());

    }

    private boolean playTone(Intent i) {
        int frequency = i.getIntExtra("frequency", -1);
        int duration = i.getIntExtra("duration", -1);

        if (frequency != -1 && duration != -1) {
            writingState = STATE_WRITING;
            return playTone(frequency, duration);
        }
        return false;
    }

    private boolean playTone(int frequency, int duration) {
        char note = (char) frequency;

        //Log.d(TAG, "tone bytes: " + tone.getValue());
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putChar(0, note);
        buffer.putInt(2, duration);

        Log.d(TAG, "note: " + Integer.valueOf(buffer.getChar(0)));
        Log.d(TAG, "duration: " + Integer.valueOf(buffer.getInt(2)));
        toneCharacteristic.setValue(buffer.array());

        boolean writeResult = bluetoothGatt.writeCharacteristic(toneCharacteristic);
        Log.d(TAG, "called writeCharacteristic: " + writeResult);

        return writeResult;
    }

    private void setNeopixels(Intent intent) {
        String pixels_str = intent.getStringExtra("pixels_json");
        Log.d(TAG, "pixels_str: " + pixels_str);
        if (pixels_str != null) {
            try {
                JSONArray pixels_json_arr = new JSONArray(pixels_str);
                writingState = STATE_WRITING;
                setNeopixels(pixels_json_arr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void turnOffNeopixels() {
        JSONArray pixelsOffArr = new JSONArray();
        for(int i = 0; i < 10; i++){
            try {
                pixelsOffArr.put(new JSONArray("[0,0,0]"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setNeopixels(pixelsOffArr);
    }

    private boolean setNeopixels(JSONArray pixels_arr) {
        char index = 0;
        byte flags = 1;

        if(animatingNeopixels){
            Intent pixelOutputIntent = new Intent(NeopixelActivity.ACTION_SET_NEOPIXEL_OUTPUT);
            pixelOutputIntent.putExtra("neopixel_frame", pixels_arr.toString());
            sendBroadcast(pixelOutputIntent);
        }

        Log.d(TAG, "inside setNeopixels: " + pixels_arr);

        // 2 bytes for index, 1 byte for flags, 3 bytes for each neopixel each represent R, G, or B
        // 10 neopixels on the board
        ByteBuffer buffer = ByteBuffer.allocate(3 + 3 * 10);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putChar(0, index);
        buffer.put(2, flags);

        if (pixels_arr.length() != 10) {
            return false;
        }

        int buffer_pos = 3;

        for (int i = 0; i < pixels_arr.length(); i++) {
            try {
                JSONArray rgb_arr = pixels_arr.getJSONArray(i);
                byte red = (byte) rgb_arr.getInt(0);
                byte green = (byte) rgb_arr.getInt(1);
                byte blue = (byte) rgb_arr.getInt(2);

                buffer.put(buffer_pos, green);
                buffer_pos++;
                buffer.put(buffer_pos, red);
                buffer_pos++;
                buffer.put(buffer_pos, blue);
                buffer_pos++;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        neopixelDataCharacteristic.setValue(buffer.array());

        boolean writeResult = bluetoothGatt.writeCharacteristic(neopixelDataCharacteristic);
        Log.d(TAG, "called writeCharacteristic: " + writeResult);

        return writeResult;
    }


    private void scanLeDevice(final boolean enable) {
        Log.d(TAG, "got scanner");
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.d(TAG, "stopping scan");

                    try {
                        bluetoothLeScanner.stopScan(mLeScanCallback);
                        Intent scanStopIntent = new Intent(ACTION_SCANNING_STOPPED);
                        sendBroadcast(scanStopIntent);
                    } catch (IllegalStateException e) {
                        //e.printStackTrace();
                        // BT Adapter got turned off before scan period was complete.
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);
            Log.d(TAG, "starting scan");
        } else {
            mScanning = false;
            Log.d(TAG, "stopping scan");
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != bluetoothGatt) {

            bluetoothGatt.close();
        }
        unregisterReceiver(bluefruitReceiver);
    }
}
