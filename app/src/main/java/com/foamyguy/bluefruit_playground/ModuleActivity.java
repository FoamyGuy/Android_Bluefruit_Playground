package com.foamyguy.bluefruit_playground;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ModuleActivity extends Activity {

    boolean stopServiceOnStop = true;

    public void backToModulesList(View v){
        stopServiceOnStop = false;

        Intent moduleListIntent = new Intent(this, ModulesListActivity.class);
        startActivity(moduleListIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (stopServiceOnStop){
            Intent disconnectIntent = new Intent(BluefruitService.ACTION_DISCONNECT);
            sendBroadcast(disconnectIntent);

            Intent exitServiceIntent = new Intent(BluefruitService.ACTION_STOP_SERVICE);
            sendBroadcast(exitServiceIntent);
        }
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopServiceOnStop = false;
        backToModulesList(null);
    }
}
