package com.foamyguy.bluefruit_playground.neopixelanimations;

import android.util.Log;

import com.foamyguy.bluefruit_playground.NeopixelActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class PulseLightSequence implements NeopixelSequence{
    private JSONArray pixels;
    private int curFrame = 0;
    private int numFrames;

    private final int[] PULSE_LEVELS = {
            0,1,2,4,8,16,32,64,128,255,
            255,128,64,32,16,8,4,2,1,0
    };

    public PulseLightSequence(){
        try {
            pixels = new JSONArray("[[0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0]]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        numFrames = 10 * 2;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public void setNumFrames(int numFrames) {
        this.numFrames = numFrames;
    }

    private void incrementCurFrame(){
        curFrame++;
        if(curFrame >= numFrames){
            curFrame = 0;
        }
    }

    public JSONArray getCurrentFrame(){
        JSONArray curPixelsFrame = getFrame(curFrame);
        incrementCurFrame();
        return curPixelsFrame;
    }

    public JSONArray getFrame(int frame){
        int adjustedFrame = frame % numFrames;
        JSONArray framePixels = null;
        framePixels = new JSONArray();

        for (int i = 0; i < 10; i++){
            JSONArray colorArr = new JSONArray();
            colorArr.put(0);
            colorArr.put(PULSE_LEVELS[adjustedFrame]);
            colorArr.put(0);
            framePixels.put(colorArr);
        }
        return framePixels;
    }
}
