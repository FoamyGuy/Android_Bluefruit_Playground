package com.adafruit.bluefruit_playground.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.R;
import com.adafruit.bluefruit_playground.ui.TextLinker;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.prefs.Preferences;

public class WelcomeActivity extends Activity {

    WelcomePagerAdapter welcomeAdapter;
    ViewPager welcomePager;

    Animation moveUpAnim;
    Animation rotateUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        moveUpAnim = AnimationUtils.loadAnimation(this, R.anim.move_up);
        rotateUp = AnimationUtils.loadAnimation(this, R.anim.rotate_up);


        welcomePager = findViewById(R.id.welcomePager);
        CirclePageIndicator indicator = findViewById(R.id.welcomePagerIndicator);


        welcomeAdapter = new WelcomePagerAdapter(this);
        welcomePager.setAdapter(welcomeAdapter);
        indicator.setViewPager(welcomePager);

    }

    private class WelcomePagerAdapter extends PagerAdapter{
        LayoutInflater inflater;

        public WelcomePagerAdapter(Context ctx){
            inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            RelativeLayout page;
            if (position == 0){
                page = (RelativeLayout)inflater.inflate(R.layout.page_welcome_0, container, false);
                TextView detailTxt = page.findViewById(R.id.pageDetail);
                TextLinker.addLink(detailTxt,getString(R.string.tip0_link_text), getString(R.string.tip0_link_url));
            }else if(position == 1){
                page = (RelativeLayout)inflater.inflate(R.layout.page_welcome_1, container, false);
                TextView detailTxt = page.findViewById(R.id.pageDetail);
                TextLinker.addLink(detailTxt,getString(R.string.tip1_link_text), getString(R.string.tip1_link_url));

                ImageView powerImg = page.findViewById(R.id.powerImg);
                powerImg.setAnimation(moveUpAnim);
                moveUpAnim.start();
            }else{
                page = (RelativeLayout)inflater.inflate(R.layout.page_welcome_2, container, false);

                ImageView armImg = page.findViewById(R.id.armImg);
                armImg.setAnimation(rotateUp);
                rotateUp.start();
            }

            container.addView(page);
            return page;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

    public void gotoPage2(View v){
        welcomePager.setCurrentItem(1);
    }
    public void gotoPage3(View v){
        welcomePager.setCurrentItem(2);
    }
    public void gotoPairing(View v){
        Intent pairingIntent = new Intent(this, PairingActivity.class);
        startActivity(pairingIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoPairing(null);
    }
}
