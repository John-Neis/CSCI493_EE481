package com.example.applicationprototype.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import com.example.applicationprototype.Util.AppDataSingleton;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleScanner {
    private BluetoothLeScanner bleScanner = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private Handler handler = new Handler();
    private ScanResultsConsumer scanResultsConsumer;
    private Context context;
    private boolean isScanning = false;
    private String TAG = "Ble Scanner Debug";
    AppDataSingleton shared_data;

    public BleScanner(Context context){
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //Ask to enable bluetooth if disabled
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth is OFF");
            Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        Log.d(TAG, "Bluetooth is ON");
    }

    public void startScanning(final ScanResultsConsumer scanResultsConsumer, long duration){
            if(isScanning){
                Log.d(TAG, "Scan Already In Progress");
                return;
            }
            Log.d(TAG, "Scanning...");
            if(bleScanner == null){
                bleScanner = bluetoothAdapter.getBluetoothLeScanner();
                Log.d(TAG, "Created BLuetoothScanner Object");
            }

            handler.postDelayed(() -> {
                if(isScanning) {
                    Log.d(TAG, "Stopping scan after delay...");
                    bleScanner.stopScan(scanCallback);
                    setIsScanning(false);
                }
            },duration);

            this.scanResultsConsumer = scanResultsConsumer;

            List<ScanFilter> filters = new ArrayList<>();
            ScanSettings sSettings = new ScanSettings
                                .Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build();
            setIsScanning(true);
            bleScanner.startScan(filters, sSettings, scanCallback);

    }

    public void stopScanning(){
            setIsScanning(false);
            Log.d(TAG, "Stopping scan on command");
            bleScanner.stopScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "Scan Result Received");
              if(!isScanning){
                  return;
              }
              scanResultsConsumer.candidateDevice(result.getDevice(),
                      result.getScanRecord().getBytes(),
                      result.getRssi());
        }
    };

    public boolean isScanning(){
        return isScanning;
    }

    public void setIsScanning(boolean scanning){
        this.isScanning = scanning;

        if(!isScanning){
            scanResultsConsumer.scanningStopped();
        }else{
            scanResultsConsumer.scanningStarted();
        }
    }


}

