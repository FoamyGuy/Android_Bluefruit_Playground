package com.foamyguy.bluefruit_playground;

import java.io.InputStream;

public class InputStreamWrapper {
    public InputStream is;
    public int length;

    public InputStreamWrapper(InputStream is, int length){
        this.is = is;
        this.length = length;
    }
}
