package com.example.will.wifigetter;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private List<ScanResult> results;
    private Button scan, connect;
    private ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = findViewById(R.id.Scan);
        listView = findViewById(R.id.listView);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan();
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        Scan();

    }

    public  void Scan(){
        arrayList.clear();

        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning...", Toast.LENGTH_LONG).show();
    }

    BroadcastReceiver wifiReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            for (ScanResult scanResult: results){
                String mac1 = scanResult.BSSID.toUpperCase();
                String mac2 = scanResult.BSSID.toLowerCase();
                String name = scanResult.SSID;

                arrayList.add(name);
                arrayList.add(mac1);
                adapter.notifyDataSetChanged();
                final String macPass = mac1.replace(":", "");
                final String macPass1 = macPass.substring(4);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
                        final ClipboardManager clipboardManager = (ClipboardManager)clipboardService;
                        ClipData clipData = ClipData.newPlainText("Source Text", macPass1);
                        clipboardManager.setPrimaryClip(clipData);
                    }
                });
            }
        }
    };



}
