package com.adafruit.bluefruit_playground.ui;

import com.adafruit.bluefruit_playground.R;

public class RssiUI {
    private static int[] signalImgs = {
        R.drawable.signalstrength0,
        R.drawable.signalstrength1,
        R.drawable.signalstrength2,
        R.drawable.signalstrength3,
        R.drawable.signalstrength4,
    };

    public static int signalImage(int rssiValue){
        int index = 0;
        if (rssiValue == 127) {     // value of 127 reserved for RSSI not available
            index = 0;
        } else if (rssiValue <= -84) {
            index = 0;
        } else if (rssiValue <= -72) {
            index = 1;
        } else if (rssiValue <= -60) {
            index = 2;
        } else if (rssiValue <= -48) {
            index = 3;
        } else {
            index = 4;
        }

        return signalImgs[index];
    }
}
