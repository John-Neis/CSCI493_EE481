package com.bluetooth.php.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.bluetooth.php.ui.ConnectBTLEDeviceActivity;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class BleScanner {
    private AppDataSingleton shared_data;
    private BluetoothLeScanner scanner = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private Handler handler = new Handler();
    private ScanResultsConsumer scan_results_consumer;
    private Context context;
    private boolean scanning = false;
    private String device_name_start = "";
    private int scan_duration = 0;

    public BleScanner(Context context) {
        this.context = context;
        shared_data = AppDataSingleton.getInstance();
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter.isEnabled() && shared_data.is_device_bluetooth_enabled()){
            shared_data.set_device_bluetooth_enabled(true);
        }
        if(!bluetoothAdapter.isEnabled() && shared_data.is_device_bluetooth_enabled()){
            shared_data.set_device_bluetooth_enabled(false);
        }
        // check bluetooth is available and on
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d(Constants.TAG, "Bluetooth is NOT switched on");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        }
        Log.d(Constants.TAG, "Bluetooth is switched on");
    }


    public void startScanning(final ScanResultsConsumer scan_results_consumer) {
        int stop_after_ms = shared_data.getScanDuration();
        if (scanning) {
            Log.d(Constants.TAG, "Already scanning so ignoring startScanning request");
            return;
        }
        Log.d(Constants.TAG, "Scanning...");
        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(Constants.TAG, "Created BluetoothScanner object");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scanning) {
                    Log.d(Constants.TAG, "Stopping scanning");
                    scanner.stopScan(scan_callback);
                    setScanning(false);
                }
            }
        }, stop_after_ms);
        this.scan_results_consumer = scan_results_consumer;
        ScanSettings settings = new ScanSettings.Builder()
                                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                    .build();
        List<ScanFilter> filters;
        filters = new ArrayList<ScanFilter>();
        if(shared_data.areFiltersEnabled()) {
            Log.d(Constants.TAG, "Starting Scan with Filters");
            ScanFilter filter = new ScanFilter.Builder().setDeviceName("PHP Controller").build();
            filters.add(filter);
        }
        else{
            Log.d(Constants.TAG, "Starting Scan without filters");
        }

        setScanning(true);
        scanner.startScan(filters, settings, scan_callback);
    }

    public void stopScanning() {
        setScanning(false);
        Log.d(Constants.TAG, "Stopping scanning");
        scanner.stopScan(scan_callback);
    }

    private final ScanCallback scan_callback = new ScanCallback() {
        public void onScanResult(int callbackType, final ScanResult result) {
            if (!scanning) {
                return;
            }
            scan_results_consumer.candidateBleDevice(result.getDevice(),
                    result.getScanRecord().getBytes(), result.getRssi());
        }
    };

    public boolean isScanning() {
        return scanning;
    }

    void setScanning(boolean scanning) {
        this.scanning = scanning;
        if (!scanning) {
            scan_results_consumer.scanningStopped();
        } else {
            scan_results_consumer.scanningStarted();        }
    }

}