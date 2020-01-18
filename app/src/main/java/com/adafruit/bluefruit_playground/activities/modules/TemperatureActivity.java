package com.adafruit.bluefruit_playground.activities.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.BluefruitService;
import com.adafruit.bluefruit_playground.R;
import com.adafruit.bluefruit_playground.activities.HelpActivity;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;

public class TemperatureActivity extends ModuleActivity {
    private final String TAG = TemperatureActivity.class.getSimpleName();
    BroadcastReceiver temperatureDataReceiver;
    GraphView graph;
    int curTick = 0;
    TextView temperatureTxt;


    LineGraphSeries<DataPoint> celsiusSeries;
    LineGraphSeries<DataPoint> fahrenheitSeries;

    boolean showingCelsius = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        graph = (GraphView) findViewById(R.id.temperatureGraph);
        temperatureTxt = (TextView) findViewById(R.id.temperatureTxt);

        celsiusSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {});
        celsiusSeries.setDrawDataPoints(false);
        celsiusSeries.setDrawAsPath(true);

        fahrenheitSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {});
        fahrenheitSeries.setDrawDataPoints(false);
        fahrenheitSeries.setDrawAsPath(true);

        graph.addSeries(celsiusSeries);


        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(60);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(1);
        nf.setMinimumIntegerDigits(2);

        NumberFormat nfX = NumberFormat.getInstance();
        nfX.setMinimumIntegerDigits(1);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nfX, nf));

        IntentFilter temperatureDataFilter = new IntentFilter(BluefruitService.ACTION_TEMPERATURE_DATA_AVAILABLE);
        temperatureDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                float temperature = intent.getFloatExtra("temperature", 0f);
                Log.i(TAG, "onReceive temperature data: " + temperature);
                DataPoint tempData = new DataPoint(curTick, temperature);
                curTick++;
                Log.d(TAG, "appending data: " + curTick + " - " + temperature);
                celsiusSeries.appendData(tempData, true, 60, false);
                DataPoint fTempData = new DataPoint(curTick, celsiusToFahrenheit(temperature));
                fahrenheitSeries.appendData(fTempData, true, 60, false);
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);

                if(showingCelsius) {
                    temperatureTxt.setText(getString(R.string.temperature_celsius_output_template).replace("%s", nf.format(temperature)));
                }else{
                    temperatureTxt.setText(getString(R.string.temperature_farenheit_output_template).replace("%s", nf.format(celsiusToFahrenheit(temperature))));
                }
            }
        };
        registerReceiver(temperatureDataReceiver, temperatureDataFilter);

        Intent enableTempNotify = new Intent(BluefruitService.ACTION_ENABLE_TEMPERATURE_NOTIFY);
        sendBroadcast(enableTempNotify);





    }


    public void startHelpActivity(View view) {
        Intent returnToIntent = new Intent(this, TemperatureActivity.class);
        stopServiceOnStop = false;
        Intent helpIntent = new Intent(this, HelpActivity.class);
        helpIntent.putExtra("helpStr", getString(R.string.temperature_help));
        helpIntent.putExtra("returnTo", returnToIntent);
        startActivity(helpIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Intent i = new Intent(BluefruitService.ACTION_DISABLE_TEMPERATURE_NOTIFY);
        sendBroadcast(i);
        Log.d(TAG, "sent disable temp notify");

        try {
            unregisterReceiver(temperatureDataReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }
    }

    public void temperatureSwap(View view) {
        if (showingCelsius) {
            graph.removeSeries(celsiusSeries);
            graph.addSeries(fahrenheitSeries);
            showingCelsius = false;
            ((TextView)view).setText(R.string.temperature_units_celsius);
        }else{
            graph.removeSeries(fahrenheitSeries);
            graph.addSeries(celsiusSeries);
            showingCelsius = true;
            ((TextView)view).setText(R.string.temperature_units_farenheit);
        }

    }

    float celsiusToFahrenheit(float cDegrees){
        float fDegress = cDegrees * 9/5 + 32;
        return fDegress;
    }
}
