package com.adafruit.bluefruit_playground.neopixelanimations;

import android.content.Context;
import android.util.Log;

import com.adafruit.bluefruit_playground.activities.modules.NeopixelActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class JSONLightSequence implements NeopixelSequence{
    private JSONArray pixels;
    private int curFrame = 0;


    private JSONArray allAnimationFrames;


    public JSONLightSequence(JSONArray allAnimationFrames){
        init(allAnimationFrames, false);
    }
    public JSONLightSequence(JSONArray allAnimationFrames, boolean addReversed){
        init(allAnimationFrames, addReversed);
    }

    public JSONLightSequence(Context ctx, String assetsJsonFile, boolean addReversed){
        try {
            String sequence_json = readAssetFile(ctx, "sequence_json/" + assetsJsonFile);
            JSONArray sequenceFrames = new JSONArray(sequence_json);
            init(sequenceFrames, addReversed);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(JSONArray allAnimationFrames, boolean addReversed){
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

    private String readAssetFile(Context ctx, String assetFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = ctx.getAssets().open(assetFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8 ));
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        br.close();

        return sb.toString();
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
