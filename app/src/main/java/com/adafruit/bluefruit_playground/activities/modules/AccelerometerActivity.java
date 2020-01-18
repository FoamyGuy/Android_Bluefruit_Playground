package com.adafruit.bluefruit_playground.activities.modules;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.ble.BluefruitService;
import com.adafruit.bluefruit_playground.R;
import com.adafruit.bluefruit_playground.activities.HelpActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;

public class AccelerometerActivity extends ModuleActivity {
    private final String TAG = AccelerometerActivity.class.getSimpleName();
    BroadcastReceiver accelerometerDataReceiver;

    TextView xTxt;
    TextView yTxt;
    TextView zTxt;

    TextView xAngleTxt;
    TextView yAngleTxt;
    TextView zAngleTxt;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        xTxt = findViewById(R.id.xTxt);
        yTxt = findViewById(R.id.yTxt);
        zTxt = findViewById(R.id.zTxt);

        xAngleTxt = findViewById(R.id.xAngleTxt);
        yAngleTxt = findViewById(R.id.yAngleTxt);
        zAngleTxt = findViewById(R.id.zAngleTxt);

        WebView wv;
        WebView.setWebContentsDebuggingEnabled(true);
        wv = findViewById(R.id.modelWeb);
        wv.getSettings().setJavaScriptEnabled(true);

        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        Log.d(TAG, "loading html");


        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
               if (!request.isForMainFrame() && Objects.requireNonNull(request.getUrl().getPath()).endsWith(".js")) {
                    Log.d(TAG, " js file request need to set mime/type " + request.getUrl().getPath());
                   try {
                       return new WebResourceResponse("application/javascript", null, new BufferedInputStream(view.getContext().getAssets().open(request.getUrl().getPath().replace("/assets/",""))));
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                }
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //Log.d(TAG, "error: " + error.getDescription());
                Log.d(TAG, "error: " + request.getUrl());
            }
        });

        wv.loadUrl("https://appassets.androidplatform.net/assets/www/cpb_3d_model_wgt/index.html");



        IntentFilter accelerometerDataFilter = new IntentFilter(BluefruitService.ACTION_ACCELEROMETER_DATA_AVAILABLE);
        accelerometerDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                float xVal = intent.getFloatExtra("x", 0);
                float yVal = intent.getFloatExtra("y", 0);
                float zVal = intent.getFloatExtra("z", 0);

                Log.i(TAG, "onReceive accel data: " + xVal + ", " + yVal + ", " + zVal);

                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(2);
                nf.setMinimumIntegerDigits(1);

                xTxt.setText(getString(R.string.accelerometer_panel_x_template).replace("%s", nf.format(xVal)));
                yTxt.setText(getString(R.string.accelerometer_panel_y_template).replace("%s", nf.format(yVal)));
                zTxt.setText(getString(R.string.accelerometer_panel_z_template).replace("%s", nf.format(zVal)));


                String eulerAnglesCsv = eulerAngles(xVal, yVal, zVal);
                xAngleTxt.setText(
                        getString(R.string.accelerometer_panel_x_template).replace(
                                "%s",nf.format(Float.valueOf(eulerAnglesCsv.split(",")[0]))
                        ));

                yAngleTxt.setText(
                        getString(R.string.accelerometer_panel_y_template).replace(
                                "%s",nf.format(Float.valueOf(eulerAnglesCsv.split(",")[1]))
                        ));

                zAngleTxt.setText(
                        getString(R.string.accelerometer_panel_z_template).replace(
                                "%s",nf.format(Float.valueOf(eulerAnglesCsv.split(",")[2]))
                        ));

                wv.loadUrl("javascript:setRotation(" + eulerAngles(xVal, yVal, zVal) +")");

            }
        };
        registerReceiver(accelerometerDataReceiver, accelerometerDataFilter);

        Intent enableLightNotify = new Intent(BluefruitService.ACTION_ENABLE_ACCELEROMETER_NOTIFY);
        sendBroadcast(enableLightNotify);
    }


    private String  eulerAngles(float accelX, float accelY, float accelZ){
        float accelAngleX = (float)(Math.atan2(accelY, accelZ) * 180/Math.PI);
        float accelAngleY = (float)(Math.atan2(-accelX, Math.sqrt(accelY*accelY + accelZ*accelZ)) * 180/Math.PI);

        //float angleX = 0.98*angleX + 0.02*accelAngleX;
        //float angleY = 0.98*angleY + 0.02*accelAngleY;
        Log.d(TAG, "euler: " + accelAngleX + ", " + accelAngleY);
        return accelAngleX + "," + accelAngleY + ",0";
    }

    public void startHelpActivity(View view) {
        Intent returnToIntent = new Intent(this, AccelerometerActivity.class);
        stopServiceOnStop = false;
        Intent helpIntent = new Intent(this, HelpActivity.class);
        helpIntent.putExtra("helpStr", getString(R.string.accelerometer_help));
        helpIntent.putExtra("returnTo", returnToIntent);
        startActivity(helpIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Intent i = new Intent(BluefruitService.ACTION_DISABLE_ACCELEROMETER_NOTIFY);
        sendBroadcast(i);
        Log.d(TAG, "sent disable accel notify");

        try {
            unregisterReceiver(accelerometerDataReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }
    }
}
