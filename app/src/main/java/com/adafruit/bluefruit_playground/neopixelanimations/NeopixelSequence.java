package com.adafruit.bluefruit_playground.neopixelanimations;

import org.json.JSONArray;

public interface NeopixelSequence {

    JSONArray getFrame(int frame);
    JSONArray getCurrentFrame();
    int getNumFrames();
    void setNumFrames(int numFrames);
}
