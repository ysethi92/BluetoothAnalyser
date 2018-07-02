package com.example.yashsethi.bluetoothanalyser.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.yashsethi.bluetoothanalyser.R;

public class PairedDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_device);

        Intent i = new Intent();
        i.getStringArrayListExtra("devices");
    }
}
