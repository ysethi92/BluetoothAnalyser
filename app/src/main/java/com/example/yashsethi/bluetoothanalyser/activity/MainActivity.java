package com.example.yashsethi.bluetoothanalyser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yashsethi.bluetoothanalyser.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "B=B MainActivity";

    Button turnon, turnoff, devices, discover;
    TextView text;
    ListView listView;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ProgressBar progressBar;
    ImageView refresh;
    ArrayList<BluetoothDevice> deviceList;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        turnon = findViewById(R.id.turnon);
        turnoff = findViewById(R.id.turnoff);
        devices = findViewById(R.id.devices);
        discover = findViewById(R.id.discover);
        listView = findViewById(R.id.listview);
        text = findViewById(R.id.text);
        progressBar = findViewById(R.id.progressBar);
        refresh = findViewById(R.id.refresh);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "Adapter = " + bluetoothAdapter);
        Log.d(TAG ,"Device name = " +bluetoothAdapter.getName());

        turnon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                checkBTPermissions();
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 0);

                    Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();
                } else if (bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
                }
            }
        });

        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.disable();
                text.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(getVisible, 0);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //checkBTPermissions();
                Log.d(TAG , "1");
                bluetoothAdapter.startDiscovery();
                refresh.setRotation(refresh.getRotation() + 90);

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(broadcastReceiver, filter);
                Log.d(TAG, "2");



/*
                if(bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "btnDiscover: Canceling discovery.");

                    //check BT permissions in manifest
                    checkBTPermissions();


                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(broadcastReceiver, filter);
                    bluetoothAdapter.startDiscovery();
                }
                if(!bluetoothAdapter.isDiscovering()){

                    //check BT permissions in manifest
                    checkBTPermissions();


                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(broadcastReceiver, filter);
                    bluetoothAdapter.startDiscovery();
                }*/
            }
        });

        devices.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {

                text.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);

                pairedDevices = bluetoothAdapter.getBondedDevices();
                ArrayList list = new ArrayList();
                for (BluetoothDevice bt : pairedDevices)
                    list.add(bt.getName() + " : " + bt.getAddress());
                Log.d(TAG, "Devices = " + list);

                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.devices, list);
                listView.setAdapter(adapter);
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG ,"Receiver");

            String action = intent.getAction();
            Log.d(TAG, "IntentAction = " + intent.getAction());

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
                }
            }
            if (bluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                progressBar.setVisibility(View.VISIBLE);
                 deviceList = new ArrayList<BluetoothDevice>();
            }
            if (bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.INVISIBLE);
                Intent i = new Intent();
                i.putParcelableArrayListExtra("devices" , deviceList);
                i.setClass(MainActivity.this , PairedDeviceActivity.class);
                //startActivity(i);
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Device = " + device.getName());
                Toast.makeText(context, "Found Device : " + device.getName(), Toast.LENGTH_SHORT).show();
                deviceList.add(device);
            }
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        Log.d(TAG , "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        bluetoothAdapter.cancelDiscovery();
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}



//package com.example.yashsethi.bluetoothanalyser.activity;
//
//        import android.Manifest;
//        import android.annotation.SuppressLint;
//        import android.bluetooth.BluetoothAdapter;
//        import android.bluetooth.BluetoothDevice;
//        import android.content.BroadcastReceiver;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.content.IntentFilter;
//        import android.os.Build;
//        import android.support.annotation.RequiresApi;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.ArrayAdapter;
//        import android.widget.Button;
//        import android.widget.ImageView;
//        import android.widget.ListView;
//        import android.widget.ProgressBar;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.example.yashsethi.bluetoothanalyser.R;
//
//        import java.util.ArrayList;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.Set;
//
//public class MainActivity extends AppCompatActivity {
//
//    private final static String TAG = "B=B MainActivity";
//
//    Button turnon, turnoff, devices, discover;
//    TextView text;
//    ListView listView;
//    BluetoothAdapter bluetoothAdapter;
//    Set<BluetoothDevice> pairedDevices;
//    ProgressBar progressBar;
//    ImageView refresh;
//    ArrayList <BluetoothDevice>arrayList;
//
//    String[] device = {};
//    int i = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//        turnon = findViewById(R.id.turnon);
//        turnoff = findViewById(R.id.turnoff);
//        devices = findViewById(R.id.devices);
//        discover = findViewById(R.id.discover);
//        listView = findViewById(R.id.listview);
//        text = findViewById(R.id.text);
//        progressBar = findViewById(R.id.progressBar);
//        refresh = findViewById(R.id.refresh);
//
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        Log.d(TAG, "Adapter = " + bluetoothAdapter);
//        Log.d(TAG ,"Device name = " +bluetoothAdapter.getName());
//
//        turnon.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("WrongConstant")
//            @Override
//            public void onClick(View view) {
//                if (!bluetoothAdapter.isEnabled()) {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(intent, 0);
//
//                    Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();
//                } else if (bluetoothAdapter.isEnabled()) {
//                    Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
//
//        turnoff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bluetoothAdapter.disable();
//                text.setVisibility(View.GONE);
//                listView.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        discover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                startActivityForResult(getVisible, 0);
//            }
//        });
//
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View view) {
//                refresh.setRotation(refresh.getRotation() + 90);
//
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(BluetoothDevice.ACTION_FOUND);
//                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//                registerReceiver(broadcastReceiver, filter);
//                Log.d(TAG, "2");
//                bluetoothAdapter.startDiscovery();
//            }
//        });
//
//        devices.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//            @Override
//            public void onClick(View view) {
//
//                text.setVisibility(View.VISIBLE);
//                listView.setVisibility(View.VISIBLE);
//
//                pairedDevices = bluetoothAdapter.getBondedDevices();
//                ArrayList list = new ArrayList();
//                for (BluetoothDevice bt : pairedDevices)
//                    list.add(bt.getName() + " : " + bt.getAddress());
//                Log.d(TAG, "Devices = " + list);
//
//                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.devices, list);
//                listView.setAdapter(adapter);
//            }
//        });
//    }
//
//    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG ,"Receiver");
//            arrayList = new ArrayList<BluetoothDevice>();
//            String action = intent.getAction();
//            Log.d(TAG, "IntentAction = " + intent.getAction());
//
//            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//
//                if (state == BluetoothAdapter.STATE_ON) {
//                    Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
//                }
//            }
//            else if (bluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
//            {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//            else if (bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                progressBar.setVisibility(View.INVISIBLE);
//                Intent i = new Intent();
//                i.putParcelableArrayListExtra("devices" , arrayList);
//                i.setClass(MainActivity.this , PairedDeviceActivity.class);
//            }
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Log.d(TAG, "Device = " + device.getName());
//                Toast.makeText(context, "Found Device : " + device.getName(), Toast.LENGTH_SHORT).show();
//                arrayList.add(device);
//            }
//        }
//    };
//
//
//    @Override
//    protected void onDestroy() {
//        unregisterReceiver(broadcastReceiver);
//        Log.d(TAG , "onDestroy");
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        bluetoothAdapter.cancelDiscovery();
//        super.onPause();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void checkBTPermissions() {
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//            if (permissionCheck != 0) {
//
//                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
//            }
//        }else{
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//        }
//    }
//}
//
//
