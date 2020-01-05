package com.foamyguy.bluefruit_playground.colorutils;

public class ColorConverter {


    public static String intToHex(int color){
        String hexConvertedColor = String.format("#%06X", (0xFFFFFF & color));
        return hexConvertedColor;
    }

    /**
     * @param colorStr e.g. "#FFFFFF"
     * @return
     */
    public static RGBColor hex2Rgb(String colorStr) {
        return new RGBColor(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
