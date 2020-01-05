package com.foamyguy.bluefruit_playground.neopixelanimations;

import android.util.Log;

import com.foamyguy.bluefruit_playground.NeopixelActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;


public class JSONLightSequence implements NeopixelSequence{
    private JSONArray pixels;
    private int curFrame = 0;


    private JSONArray allAnimationFrames;


    public JSONLightSequence(JSONArray allAnimationFrames){
        this(allAnimationFrames, false);
    }
    public JSONLightSequence(JSONArray allAnimationFrames, boolean addReversed){
        try {
            this.allAnimationFrames = new JSONArray(allAnimationFrames.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (addReversed){
            for(int i = allAnimationFrames.length()-1; i >= 0; i--){
                try {
                    Log.d(NeopixelActivity.TAG, "adding " + i);
                    this.allAnimationFrames.put(allAnimationFrames.getJSONArray(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d(NeopixelActivity.TAG, "after addReverse: ");
            Log.d(NeopixelActivity.TAG, this.allAnimationFrames.toString());
        }

    }

    public JSONLightSequence(String assetsJsonFile){

    }


    public int getNumFrames() {
        return allAnimationFrames.length();
    }

    @Override
    public void setNumFrames(int numFrames) {

    }


    private void incrementCurFrame(){
        curFrame++;
        if(curFrame >= getNumFrames()){
            curFrame = 0;
        }
    }

    public JSONArray getCurrentFrame(){
        JSONArray curPixelsFrame = getFrame(curFrame);
        incrementCurFrame();
        return curPixelsFrame;
    }

    public JSONArray getFrame(int frame){
        int adjustedFrame = frame % getNumFrames();
        JSONArray framePixels = null;
        try {
            framePixels = allAnimationFrames.getJSONArray(adjustedFrame);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return framePixels;
    }
}
