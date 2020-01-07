package com.foamyguy.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ModuleActivity extends Activity {

    public void backToModulesList(View v){
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }
}
