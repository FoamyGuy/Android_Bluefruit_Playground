package com.foamyguy.bluefruit_playground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ModulesListActivity extends Activity {

    ListView modulesList;

    // Color.argb(0,0,0,0)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_list);

        modulesList = findViewById(R.id.modulesList);

        ModulesAdapter modulesAdapter = new ModulesAdapter(this, -1);

        modulesAdapter.add(new CPBModule(Color.rgb(0,0,250), "Neopixels","Control LED color & animation"));
        modulesAdapter.add(new CPBModule(Color.rgb(236,228,0), "Light Sensor","View continuous light sensor readings."));
        modulesAdapter.add(new CPBModule(Color.rgb(165,0,219), "Button Status","View state of built-in buttons & switch"));
        modulesAdapter.add(new CPBModule(Color.rgb(18,186,0), "Tone Generator","Turn CPB into musical instrument"));
        modulesAdapter.add(new CPBModule(Color.rgb(0,100,237), "Accelerometer","View orientation based on accelerometer data"));
        modulesAdapter.add(new CPBModule(Color.rgb(220,13,0), "Temperature","View current temperature readings"));

        modulesList.setAdapter(modulesAdapter);

        modulesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    startNeopixelsActivity(view);
                }else if (position == 1){
                    startLightActivity(view);
                }else if (position == 2){
                    startButtonsActivity(view);
                }else if (position == 3){
                    startToneGeneratorActivity(view);
                }else if (position == 4){
                    startAccelerometerActivity(view);
                }else if (position == 5){
                    startTemperatureActivity(view);
                }
            }
        });
    }

    public void disconnectBluefruit(View view) {
        Intent disconnectIntent = new Intent(BluefruitService.ACTION_DISCONNECT);
        sendBroadcast(disconnectIntent);

        Intent pairingIntent = new Intent(getApplicationContext(), PairingActivity.class);
        startActivity(pairingIntent);
        finish();

    }

    private class CPBModule {
        String title;
        String info;
        int color;

        public CPBModule(int color, String title, String info){
            this.title = title;
            this.info = info;
            this.color = color;
        }
    }

    private class ModulesAdapter extends ArrayAdapter<CPBModule>{
        LayoutInflater inflater;
        public ModulesAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RelativeLayout rowLyt = (RelativeLayout)convertView;
            if(rowLyt == null){
                rowLyt = (RelativeLayout) inflater.inflate(R.layout.row_module, parent, false);
            }
            TextView titleTxt = rowLyt.findViewById(R.id.moduleTitle);
            TextView infoTxt = rowLyt.findViewById(R.id.moduleInfo);
            ImageView colorDot = rowLyt.findViewById(R.id.colorDot);

            titleTxt.setText(getItem(position).title);
            infoTxt.setText(getItem(position).info);
            ((GradientDrawable)colorDot.getDrawable()).setColor(getItem(position).color);

            return rowLyt;
        }
    }

    public void startToneGeneratorActivity(View view) {
        Intent toneGenIntent = new Intent(this, ToneActivity.class);
        startActivity(toneGenIntent);

    }

    public void startTemperatureActivity(View view) {
        Intent temperatureIntent = new Intent(this, TemperatureActivity.class);
        startActivity(temperatureIntent);
    }

    public void startLightActivity(View view) {
        Intent lightIntent = new Intent(this, LightActivity.class);
        startActivity(lightIntent);

    }

    public void startButtonsActivity(View view) {
        Intent buttonsIntent = new Intent(this, ButtonsActivity.class);
        startActivity(buttonsIntent);
    }

    public void startAccelerometerActivity(View view) {
        Intent accelerometerIntent = new Intent(this, AccelerometerActivity.class);
        startActivity(accelerometerIntent);
    }

    public void startNeopixelsActivity(View view) {
        Intent neopixelIntent = new Intent(this, NeopixelActivity.class);
        startActivity(neopixelIntent);
    }
}
