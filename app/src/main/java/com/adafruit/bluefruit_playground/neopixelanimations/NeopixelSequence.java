package com.adafruit.bluefruit_playground.neopixelanimations;

import org.json.JSONArray;

public interface NeopixelSequence {

    public JSONArray getFrame(int frame);
    public JSONArray getCurrentFrame();
    public int getNumFrames();
    public void setNumFrames(int numFrames);
}
