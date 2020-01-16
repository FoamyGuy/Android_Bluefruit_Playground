package com.foamyguy.bluefruit_playground;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.foamyguy.bluefruit_playground.colorutils.ColorConverter;
import com.foamyguy.bluefruit_playground.colorutils.RGBColor;
import com.foamyguy.bluefruit_playground.neopixelanimations.JSONLightSequence;
import com.foamyguy.bluefruit_playground.neopixelanimations.NeopixelSequence;
import com.foamyguy.bluefruit_playground.neopixelanimations.PulseLightSequence;
import com.foamyguy.bluefruit_playground.neopixelanimations.RotateLightSequence;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;



public class NeopixelActivity extends ModuleActivity implements CompoundButton.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    public static final String TAG = NeopixelActivity.class.getSimpleName();
    ViewPager colorPickerPager;

    ColorPickerAdapter colorPickerAdapter;
    SeekBar brightnessSeek;
    SeekBar speedSeek;

    JSONArray previousPixels;

    ArrayList<CheckBox> pixelChecks;
    ArrayList<ImageView> pixelsOutputs;

    boolean animating = false;

    NeopixelSequence curSequence;

    Runnable pixelAnimationRunnable;

    ImageView selectAllImg;
    ImageView unselectAllImg;

    boolean haveAutoSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neopixel);

        colorPickerPager = findViewById(R.id.colorPickerPager);

        //Bind the title indicator to the adapter
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.colorPickerPagerIndicator);



        colorPickerAdapter = new ColorPickerAdapter(this);
        colorPickerPager.setAdapter(colorPickerAdapter);
        colorPickerPager.addOnPageChangeListener(this);
        indicator.setViewPager(colorPickerPager);

        selectAllImg = findViewById(R.id.selectAllBtn);
        unselectAllImg = findViewById(R.id.clearBtn);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.d(TAG, "width: " + width);
        if (width > 800){
            RelativeLayout cpb = findViewById(R.id.cpbLayout);
            cpb.setScaleX(1.2f);
            cpb.setScaleY(1.2f);
        }

        // setup checkboxes
        //for loop
        //  int drawableResourceId = this.getResources().getIdentifier("nameOfDrawable", "drawable", this.getPackageName());

        pixelChecks = new ArrayList<>();
        pixelsOutputs = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int checkResourceId = this.getResources().getIdentifier("pixel" + i + "Check", "id", this.getPackageName());
            pixelChecks.add(findViewById(checkResourceId));
            // pixel8ColorOutput
            int colorOutputResourceId = this.getResources().getIdentifier("pixel" + i + "ColorOutput", "id", this.getPackageName());
            pixelsOutputs.add(findViewById(colorOutputResourceId));

        }

        JSONArray pixelsOffArr = new JSONArray();
        for(int i = 0; i < 10; i++){
            try {
                pixelsOffArr.put(new JSONArray("[0,0,0]"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setPixelColorOutputs(pixelsOffArr);

        pixelAnimationRunnable = new Runnable() {
            @Override
            public void run() {

                if(animating) {
                    Log.d(TAG, "inside animation frame run");
                    Intent setPixelsIntent = new Intent(BluefruitService.ACTION_SET_NEOPIXELS);

                    JSONArray currentFrame = curSequence.getCurrentFrame();

                    setPixelsIntent.putExtra("pixels_json", currentFrame.toString());
                    sendBroadcast(setPixelsIntent);
                    setPixelColorOutputs(currentFrame);
                    colorPickerPager.postDelayed(this, 1000 - speedSeek.getProgress());
                }
            }
        };

    }

    private void setPixelColorOutputs(JSONArray colors) {
        for (int i = 0; i < 10; i++){
            try {
                JSONArray rgb = colors.getJSONArray(i);

                int intColor = Color.rgb(rgb.getInt(0), rgb.getInt(1), rgb.getInt(2));
                if (intColor == Color.BLACK){
                    ((GradientDrawable)pixelsOutputs.get(i).getDrawable()).setColor(Color.argb(0,0,0,0));
                }else{
                    ((GradientDrawable)pixelsOutputs.get(i).getDrawable()).setColor(intColor);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public void turnOffNeopixels(View view) {
        Intent stopAnimatingIntent = new Intent(BluefruitService.ACTION_STOP_NEOPIXEL_ANIMATION);
        sendBroadcast(stopAnimatingIntent);
        animating = false;
        colorPickerPager.removeCallbacks(pixelAnimationRunnable);


        JSONArray pixelsOffArr = new JSONArray();
        for(int i = 0; i < 10; i++){
            try {
                pixelsOffArr.put(new JSONArray("[0,0,0]"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setPixelColorOutputs(pixelsOffArr);
        Intent setPixelsIntent = new Intent(BluefruitService.ACTION_SET_NEOPIXELS);
        setPixelsIntent.putExtra("pixels_json", pixelsOffArr.toString());
        previousPixels = pixelsOffArr;
        sendBroadcast(setPixelsIntent);


    }

    public void clearPixelSelection(View view) {
        for (CheckBox pixelCheck : pixelChecks){
            pixelCheck.setChecked(false);
        }
    }

    public void selectAllPixels(View view) {
        for (CheckBox pixelCheck : pixelChecks){
            pixelCheck.setChecked(true);
        }
    }



    private void unselectAllPixels(){
        for(CheckBox pixelChk : pixelChecks){
            pixelChk.setChecked(false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1 ){
            selectAllImg.setVisibility(View.VISIBLE);
            unselectAllImg.setVisibility(View.VISIBLE);
            if(!haveAutoSelected){
                haveAutoSelected = true;
                selectAllPixels(null);
            }
        }else if (position == 0 ){
            selectAllImg.setVisibility(View.INVISIBLE);
            unselectAllImg.setVisibility(View.INVISIBLE);
            clearPixelSelection(null);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ColorPickerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        public ColorPickerAdapter(Context ctx) {
            inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d(TAG, "instantiate " + position);
            RelativeLayout page;
            if (position == 0) {
                page = (RelativeLayout) inflater.inflate(R.layout.page_color_sequence, container, false);
                speedSeek = page.findViewById(R.id.speedSeek);
                speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Intent setDelayIntent = new Intent(BluefruitService.ACTION_SET_NEOPIXEL_ANIMATION_DELAY);
                        setDelayIntent.putExtra("delay", seekBar.getMax() - progress);
                        sendBroadcast(setDelayIntent);
                        Log.d(TAG, "sent set delay intent");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                ImageView sequence3Btn = page.findViewById(R.id.sequence3Btn);
                ImageView sequence2Btn = page.findViewById(R.id.sequence2Btn);
                ImageView sequence4Btn = page.findViewById(R.id.sequence4Btn);
                ImageView sequence1Btn = page.findViewById(R.id.sequence1Btn);

                AnimationDrawable frameAnimation = (AnimationDrawable) sequence3Btn.getDrawable();
                frameAnimation.start();

                frameAnimation = (AnimationDrawable) sequence2Btn.getDrawable();
                frameAnimation.start();

                frameAnimation = (AnimationDrawable) sequence4Btn.getDrawable();
                frameAnimation.start();

                frameAnimation = (AnimationDrawable) sequence1Btn.getDrawable();
                frameAnimation.start();

                container.addView(page);
                return page;
            } else if (position == 1) {
                // color grid
                page = (RelativeLayout) inflater.inflate(R.layout.page_color_grid, container, false);
                brightnessSeek = page.findViewById(R.id.brightnessSeek);
                container.addView(page);
                return page;
            } else if (position == 2) {
                // color picker
                page = (RelativeLayout) inflater.inflate(R.layout.page_color_picker, container, false);

                Button colorSelectBtn = page.findViewById(R.id.colorSelectBtn);
                //ColorPicker picker = (ColorPicker) page.findViewById(R.id.picker);
                TextView colorTxt = page.findViewById(R.id.colorTxt);
                //SaturationBar sBar = page.findViewById(R.id.saturationbar);
                //ValueBar vBar = page.findViewById(R.id.valuebar);
                //picker.addSaturationBar(sBar);
                //picker.addValueBar(vBar);
                //picker.setShowOldCenterColor(false);

                /*
                colorSelectBtn.setBackgroundColor(picker.getColor());
                colorTxt.setText(ColorConverter.intToHex(picker.getColor()));

                picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        colorSelectBtn.setBackgroundColor(color);
                        colorTxt.setText(ColorConverter.intToHex(picker.getColor()));
                    }
                });*/


                BrightnessSlideBar brightnessSlideBar = page.findViewById(R.id.brightnessSlide);
                ColorPickerView colorPickerView = page.findViewById(R.id.colorPickerView);
                colorPickerView.attachBrightnessSlider(brightnessSlideBar);

                colorPickerView.setColorListener(new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {

                        /*float[] hsv = new float[3];
                        Color.RGBToHSV(envelope.getArgb()[1],envelope.getArgb()[2],envelope.getArgb()[3], hsv);
                        hsv[2] = 1.0f;
                        int convertedColor = Color.HSVToColor(hsv);*/

                        colorSelectBtn.setBackgroundColor(envelope.getColor());
                        colorTxt.setText("#" + envelope.getHexCode().substring(2));

                    }
                });

                container.addView(page);
                return page;
            }

            return null;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public void startNeopixelSequence(View v){

        animating = true;
        Log.d(TAG, "starting rotate sequence");
        curSequence = new PulseLightSequence();
        colorPickerPager.post(pixelAnimationRunnable);

        /*Intent startAnimatingIntent = new Intent(BluefruitService.ACTION_PLAY_NEOPIXEL_ANIMATION);
        sendBroadcast(startAnimatingIntent);*/

    }

    public void startRotationSequence(View v){
        /*animating = true;
        Log.d(TAG, "starting rotate sequence");
        curSequence = new RotateLightSequence();
        colorPickerPager.post(pixelAnimationRunnable);*/
        Intent animationIntent = new Intent(BluefruitService.ACTION_PLAY_NEOPIXEL_ANIMATION);
        animationIntent.putExtra("animationType", "rotate");
        sendBroadcast(animationIntent);
    }

    public void startPulseSequence(View v){
        /*animating = true;
        Log.d(TAG, "starting pulse sequence");
        curSequence = new PulseLightSequence();
        colorPickerPager.post(pixelAnimationRunnable);*/
        Intent animationIntent = new Intent(BluefruitService.ACTION_PLAY_NEOPIXEL_ANIMATION);
        animationIntent.putExtra("animationType", "pulse");
        sendBroadcast(animationIntent);
    }

    public void startSweepSequence(View v){
        /*Log.d(TAG, "starting sweep sequence");
        try {
            String sizzle_json = readAssetFile("sequence_json/sweep.json");
            JSONArray sequenceFrames = new JSONArray(sizzle_json);
            curSequence = new JSONLightSequence(sequenceFrames);
            colorPickerPager.post(pixelAnimationRunnable);
            animating = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Intent animationIntent = new Intent(BluefruitService.ACTION_PLAY_NEOPIXEL_ANIMATION);
        animationIntent.putExtra("animationType", "sweep");
        sendBroadcast(animationIntent);
    }

    public void startSizzleSequence(View v){
       /* Log.d(TAG, "starting sizzle sequence");
        try {
            String sizzle_json = readAssetFile("sequence_json/alternating_pixels.json");
            JSONArray sequenceFrames = new JSONArray(sizzle_json);
            curSequence = new JSONLightSequence(sequenceFrames, true);
            colorPickerPager.post(pixelAnimationRunnable);
            animating = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Intent animationIntent = new Intent(BluefruitService.ACTION_PLAY_NEOPIXEL_ANIMATION);
        animationIntent.putExtra("animationType", "sizzle");
        sendBroadcast(animationIntent);
    }

    public void setNeopixelColorsAdjusted(View v) {
        Log.d(TAG, "setNeopixelColor()");
        int intColor = ((ColorDrawable) v.getBackground()).getColor();
        setNeopixelColors(true, intColor);

    }

    public void setNeopixelColors(View v) {
        Log.d(TAG, "setNeopixelColor()");
        int intColor = ((ColorDrawable) v.getBackground()).getColor();
        setNeopixelColors(false, intColor);

    }

    private void setNeopixelColors(boolean adjusted, int color) {

        //Log.d(TAG, "setNeopixelColors()");
        int intColor = color;
        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
        RGBColor rgb = ColorConverter.hex2Rgb(hexColor);

        Log.d(TAG, "color: " + hexColor);
        Log.d(TAG, "color: " + rgb.r + ", " + rgb.g + ", " + rgb.b);

        JSONArray pixelsArr = new JSONArray();

        for (int i = 0; i < 10; i++) {
            JSONArray colorArr = new JSONArray();
            Log.d(TAG, i + ": " + pixelChecks.get(i).isChecked());

            if (pixelChecks.get(i).isChecked()) {
                if (adjusted) {
                    float[] hsv = new float[3];
                    Color.RGBToHSV(rgb.r, rgb.g, rgb.b, hsv);

                    Log.d(TAG, "hsv: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);


                    // adjust value based on seekbar
                    hsv[2] = (brightnessSeek.getProgress() + 1) / 100.0f;
                    Log.d(TAG, "hsv: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);

                    int convertedColor = Color.HSVToColor(hsv);
                    String hexConvertedColor = String.format("#%06X", (0xFFFFFF & convertedColor));
                    Log.d(TAG, "converted color: " + hexConvertedColor);

                    RGBColor rgbConverted = ColorConverter.hex2Rgb(hexConvertedColor);
                    Log.d(TAG, "converted color: " + rgbConverted.r + ", " + rgbConverted.g + ", " + rgbConverted.b);

                    colorArr.put(rgbConverted.r);
                    colorArr.put(rgbConverted.g);
                    colorArr.put(rgbConverted.b);

                    ((GradientDrawable)pixelsOutputs.get(i).getDrawable()).setColor(intColor);

                    /*Drawable background = imageView.getBackground();
                    if (background instanceof ShapeDrawable) {
                        ((ShapeDrawable)background).getPaint().setColor(ContextCompat.getColor(mContext,R.color.colorToSet));
                    }*/
                } else {
                    float[] hsv = new float[3];
                    Color.RGBToHSV(rgb.r, rgb.g, rgb.b, hsv);
                    hsv[2] = 1.0f;
                    int convertedColor = Color.HSVToColor(hsv);

                    ((GradientDrawable)pixelsOutputs.get(i).getDrawable()).setColor(convertedColor);
                    colorArr.put(rgb.r);
                    colorArr.put(rgb.g);
                    colorArr.put(rgb.b);
                }

                pixelsArr.put(colorArr);
            } else {
                try {
                    if (previousPixels != null) {
                        pixelsArr.put(previousPixels.getJSONArray(i));
                    } else {
                        pixelsArr.put(new JSONArray("[0,0,0]"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Intent setPixelsIntent = new Intent(BluefruitService.ACTION_SET_NEOPIXELS);
        setPixelsIntent.putExtra("pixels_json", pixelsArr.toString());
        previousPixels = pixelsArr;
        sendBroadcast(setPixelsIntent);


    }


}
