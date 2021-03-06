package com.adafruit.bluefruit_playground.ui;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adafruit.bluefruit_playground.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScanResultAdapter extends ArrayAdapter<ScanResult> {
    private final String TAG = ScanResultAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    public void insertOrUpdate(ScanResult incResult){
        Log.d(TAG, "checking if " + incResult.getDevice().getAddress() + " already in list");
        for(int i = 0; i < this.getCount(); i++){
            if (getItem(i).getDevice().getAddress().equals(incResult.getDevice().getAddress())){
                Log.d(TAG, "found " + incResult.getDevice().getAddress());
                this.remove(getItem(i));
                this.add(incResult);
                this.notifyDataSetChanged();
                return;
            }
        }

        this.add(incResult);
        this.notifyDataSetChanged();
        return;

    }
    public ScanResultAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RelativeLayout row = (RelativeLayout) convertView;
        if(row == null){
            row = (RelativeLayout) inflater.inflate(R.layout.row_scan_result, parent, false);
        }

        ImageView strengthImg = row.findViewById(R.id.strengthImg);

        TextView nameTxt = row.findViewById(R.id.nameTxt);
        TextView macTxt = row.findViewById(R.id.macTxt);

        strengthImg.setImageResource(RssiUI.signalImage(getItem(position).getRssi()));
        nameTxt.setText(getItem(position).getDevice().getName());
        macTxt.setText(getItem(position).getDevice().getAddress());


        return row;
    }
}
