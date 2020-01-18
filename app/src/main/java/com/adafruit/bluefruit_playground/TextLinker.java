package com.adafruit.bluefruit_playground;

import android.text.util.Linkify;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLinker {

    /**
     * Add a link to the TextView which is given.
     *
     * @param textView       the field containing the text
     * @param patternToMatch a regex pattern to put a link around
     * @param link           the link to add
     */
    public static void addLink(TextView textView, String patternToMatch,
                               final String link) {
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return link;
            }
        };
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }
}
