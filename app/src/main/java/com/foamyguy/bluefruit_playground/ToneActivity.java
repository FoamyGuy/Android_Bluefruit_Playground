package com.foamyguy.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ToneActivity extends ModuleActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone);

        Button c4Btn = (Button)findViewById(R.id.c4Btn);
        Button d4Btn = (Button)findViewById(R.id.d4Btn);
        Button e4Btn = (Button)findViewById(R.id.e4Btn);
        Button f4Btn = (Button)findViewById(R.id.f4Btn);
        Button g4Btn = (Button)findViewById(R.id.g4Btn);
        Button a4Btn = (Button)findViewById(R.id.a4Btn);

        Button cs4Btn = (Button)findViewById(R.id.cs4Btn);
        Button ds4Btn = (Button)findViewById(R.id.ds4Btn);
        Button fs4Btn = (Button)findViewById(R.id.fs4Btn);
        Button gs4Btn = (Button)findViewById(R.id.gs4Btn);
        Button as4Btn = (Button)findViewById(R.id.as4Btn);

        Button b4Btn = (Button)findViewById(R.id.b4Btn);
        Button c5Btn = (Button)findViewById(R.id.c5Btn);
        Button d5Btn = (Button)findViewById(R.id.d5Btn);
        Button e5Btn = (Button)findViewById(R.id.e5Btn);
        Button f5Btn = (Button)findViewById(R.id.f5Btn);
        Button g5Btn = (Button)findViewById(R.id.g5Btn);

        Button cs5Btn = (Button)findViewById(R.id.cs5Btn);
        Button ds5Btn = (Button)findViewById(R.id.ds5Btn);
        Button fs5Btn = (Button)findViewById(R.id.fs5Btn);

        View.OnTouchListener toneBtnListener= new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent startPlayingIntent = new Intent(BluefruitService.ACTION_PLAY_TONE);
                    startPlayingIntent.putExtra("frequency", Integer.valueOf(v.getTag().toString()));
                    startPlayingIntent.putExtra("duration", 0);
                    sendBroadcast(startPlayingIntent);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    Intent stopPlayingIntent = new Intent(BluefruitService.ACTION_PLAY_TONE);
                    stopPlayingIntent.putExtra("frequency", 0);
                    stopPlayingIntent.putExtra("duration", 0);
                    sendBroadcast(stopPlayingIntent);
                }
                return false;

            }
        };

        c4Btn.setOnTouchListener(toneBtnListener);
        d4Btn.setOnTouchListener(toneBtnListener);
        e4Btn.setOnTouchListener(toneBtnListener);
        f4Btn.setOnTouchListener(toneBtnListener);
        g4Btn.setOnTouchListener(toneBtnListener);
        a4Btn.setOnTouchListener(toneBtnListener);

        cs4Btn.setOnTouchListener(toneBtnListener);
        ds4Btn.setOnTouchListener(toneBtnListener);
        fs4Btn.setOnTouchListener(toneBtnListener);
        gs4Btn.setOnTouchListener(toneBtnListener);
        as4Btn.setOnTouchListener(toneBtnListener);

        b4Btn.setOnTouchListener(toneBtnListener);
        c5Btn.setOnTouchListener(toneBtnListener);
        d5Btn.setOnTouchListener(toneBtnListener);
        e5Btn.setOnTouchListener(toneBtnListener);
        f5Btn.setOnTouchListener(toneBtnListener);
        g5Btn.setOnTouchListener(toneBtnListener);

        cs5Btn.setOnTouchListener(toneBtnListener);
        ds5Btn.setOnTouchListener(toneBtnListener);
        fs5Btn.setOnTouchListener(toneBtnListener);
    }



}
