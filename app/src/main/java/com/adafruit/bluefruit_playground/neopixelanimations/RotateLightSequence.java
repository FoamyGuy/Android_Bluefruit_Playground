package com.adafruit.bluefruit_playground.neopixelanimations;

import android.util.Log;

import com.adafruit.bluefruit_playground.NeopixelActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class RotateLightSequence implements NeopixelSequence{
    private JSONArray pixels;
    private int curFrame = 0;
    private int numFrames;

    public RotateLightSequence(){
        try {
            //pixels = new JSONArray("[[10,10,30], [2,2,30], [0,0,50], [0,0,16], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0]]");
            pixels = new JSONArray("[[102,102,255], [27,27,255], [0,0,255], [0,0,16], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0]]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        numFrames = 10;
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
        JSONArray tempPxels = null;
        try {
            tempPxels = new JSONArray(pixels.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<JSONArray> framePixelsList = new ArrayList<>();
        JSONArray framePixels = new JSONArray();

        Log.d(NeopixelActivity.TAG, "frame is: " + adjustedFrame);
        for (int i = 0; i < adjustedFrame; i++){
            JSONArray lastPixel = (JSONArray)tempPxels.remove(tempPxels.length()-1);
            //framePixels.put(lastPixel);
            framePixelsList.add(0,lastPixel);

            //Log.d(NeopixelActivity.TAG, "size of temp: "+ tempPxels.length());
            //Log.d(NeopixelActivity.TAG, "size of frame: "+ framePixels.length());
        }
        //Log.d(NeopixelActivity.TAG, "after adjustment: ");
        //Log.d(NeopixelActivity.TAG, framePixels.toString());
        //Log.d(NeopixelActivity.TAG, "size of temp: "+ tempPxels.length());
        for (int j = 0; j < 10 - adjustedFrame; j++){
            try {
                //framePixels.put(tempPxels.getJSONArray(j));
                framePixelsList.add(tempPxels.getJSONArray(j));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Log.d(NeopixelActivity.TAG, "after filling: ");
        //Log.d(NeopixelActivity.TAG, framePixels.toString());

        for(int k = 0; k < framePixelsList.size(); k++){
            framePixels.put(framePixelsList.get(k));
        }
        return framePixels;
    }
}
