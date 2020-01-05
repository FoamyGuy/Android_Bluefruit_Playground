package com.foamyguy.bluefruit_playground;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ButtonsActivity extends ModuleActivity {
    private final String TAG = ButtonsActivity.class.getSimpleName();
    BroadcastReceiver buttonsDataReceiver;

    boolean oldSwitchVal = BluefruitService.SWITCH_POSITION_LEFT;
    boolean oldBtnAVal = BluefruitService.BUTTON_POSITION_UNPRESSED;
    boolean oldBtnBVal = BluefruitService.BUTTON_POSITION_UNPRESSED;

    Animation scaleUpAnimationA;
    Animation scaleDownAnimationA;
    Animation scaleUpAnimationB;
    Animation scaleDownAnimationB;

    Animation scaleUpReboundAnimation;

    ImageView btnAstatus;
    ImageView btnBstatus;
    ImageView switchStatus;

    int changeSwitchImageTo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);

        scaleUpAnimationA = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDownAnimationA = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        scaleUpAnimationB = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDownAnimationB = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        scaleUpReboundAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up_rebound);

        scaleUpReboundAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switchStatus.setImageResource(changeSwitchImageTo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        btnAstatus = (ImageView)findViewById(R.id.statusImgBtnA);
        btnBstatus = (ImageView)findViewById(R.id.statusImgBtnB);
        switchStatus = (ImageView)findViewById(R.id.statusImgSwitch);

        IntentFilter buttonsDataFilter = new IntentFilter(BluefruitService.ACTION_BUTTONS_DATA_AVAILABLE);
        buttonsDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //int light = intent.getIntExtra("light", 0);
                //Log.i(TAG, "onReceive temperature data: " + light);

                boolean switchPos = intent.getBooleanExtra("switch", BluefruitService.SWITCH_POSITION_LEFT);
                boolean btnA = intent.getBooleanExtra("btnA", BluefruitService.BUTTON_POSITION_UNPRESSED);
                boolean btnB = intent.getBooleanExtra("btnB", BluefruitService.BUTTON_POSITION_UNPRESSED);

                if (switchPos == BluefruitService.SWITCH_POSITION_LEFT) {
                    Log.d(TAG, "switch: left");
                }else{
                    Log.d(TAG, "switch: right");
                }

                if (btnA == BluefruitService.BUTTON_POSITION_PRESSED) {
                    Log.d(TAG, "btnA: pressed");
                    if (oldBtnAVal == BluefruitService.BUTTON_POSITION_UNPRESSED){
                        Log.d(TAG, "starting animation for status A");
                        btnAstatus.setImageResource(R.drawable.status_a_pressed);
                        btnAstatus.startAnimation(scaleUpAnimationA);

                    }
                }else{
                    if (oldBtnAVal == BluefruitService.BUTTON_POSITION_PRESSED){
                        //btnAstatus.setAnimation(scaleUpAnimation);
                        //scaleUpAnimation.start();
                        btnAstatus.setImageResource(R.drawable.status_a);
                        btnAstatus.startAnimation(scaleDownAnimationA);
                    }
                }
                if (btnB == BluefruitService.BUTTON_POSITION_PRESSED) {
                    Log.d(TAG, "btnB: pressed");
                    if (oldBtnBVal == BluefruitService.BUTTON_POSITION_UNPRESSED){
                        Log.d(TAG, "starting animation for status A");
                        btnBstatus.setImageResource(R.drawable.status_b_pressed);
                        btnBstatus.startAnimation(scaleUpAnimationB);

                    }
                }else{
                    if (oldBtnBVal == BluefruitService.BUTTON_POSITION_PRESSED){
                        //btnAstatus.setAnimation(scaleUpAnimation);
                        //scaleUpAnimation.start();
                        btnBstatus.setImageResource(R.drawable.status_b);
                        btnBstatus.startAnimation(scaleDownAnimationB);
                    }
                }

                if (switchPos == BluefruitService.SWITCH_POSITION_LEFT){
                    if (oldSwitchVal == BluefruitService.SWITCH_POSITION_RIGHT){
                        Log.d(TAG, "starting animation for switch to left");
                        switchStatus.setImageResource(R.drawable.status_left_pressed);
                        switchStatus.startAnimation(scaleUpReboundAnimation);
                        changeSwitchImageTo = R.drawable.status_left;
                    }
                }else { // SWITCH_POSITION_RIGHT
                    if (oldSwitchVal == BluefruitService.SWITCH_POSITION_LEFT){
                        Log.d(TAG, "starting animation for switch to left");
                        switchStatus.setImageResource(R.drawable.status_right_pressed);
                        switchStatus.startAnimation(scaleUpReboundAnimation);
                        changeSwitchImageTo = R.drawable.status_right;
                    }
                }


                oldBtnAVal = btnA;
                oldBtnBVal = btnB;
                oldSwitchVal = switchPos;

            }
        };
        registerReceiver(buttonsDataReceiver, buttonsDataFilter);

        Intent enableButtonsNotify = new Intent(BluefruitService.ACTION_ENABLE_BUTTONS_NOTIFY);
        sendBroadcast(enableButtonsNotify);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Intent i = new Intent(BluefruitService.ACTION_DISABLE_BUTTONS_NOTIFY);
        sendBroadcast(i);
        Log.d(TAG, "sent disable buttons notify");

        try {
            unregisterReceiver(buttonsDataReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered
            e.printStackTrace();
        }
    }

}
