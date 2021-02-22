package com.example.applicationprototype.Actions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationprototype.R;

import java.util.ArrayList;

import com.example.applicationprototype.Bluetooth.BleScanner;
import com.example.applicationprototype.Bluetooth.PeripheralControlActivity;
import com.example.applicationprototype.Bluetooth.ScanResultsConsumer;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ActionConnectBTLEDeviceActivity extends AppCompatActivity implements ScanResultsConsumer {
    private boolean isBleScanning = false;
    private ListAdapter bleDeviceListAdapter;
    private BleScanner bleScanner;
    private static final long SCAN_TIMEOUT = 5000;
    private static final int REQUEST_LOCATION = 0;
    private boolean permissions_granted = false;
    private int deviceCount = 0;
    private Toast toast;
    private ListView bt_device_list;
    private TextView bt_device_name;
    private Button bt_scan;
    private static final String TAG = "PHP Partner App Debug:";
    static class ViewHolder{
        public TextView deviceName;
        public TextView deviceAddress;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_connect_btle_device_activity);

        bt_device_name = findViewById(R.id.bt_device_name);
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        bt_device_name.setText("Current Device : " + name);

        bt_scan = findViewById(R.id.bt_scan);

        bleDeviceListAdapter = new ListAdapter();

        bt_device_list = this.findViewById(R.id.bt_device_list);
        bt_device_list.setAdapter(bleDeviceListAdapter);

        bleScanner = new BleScanner(this.getApplicationContext());

        bt_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isBleScanning){
                    bleScanner.stopScanning();
                }

                BluetoothDevice device = bleDeviceListAdapter.getDevice(position);
                if(toast != null){
                    toast.cancel();
                }
                Intent intent = new Intent(ActionConnectBTLEDeviceActivity.this, PeripheralControlActivity.class);
                intent.putExtra(PeripheralControlActivity.EXTRA_NAME, device.getName());
                intent.putExtra(PeripheralControlActivity.EXTRA_ID, device.getAddress());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleDeviceListAdapter.addDevice(device);
                bleDeviceListAdapter.notifyDataSetChanged();
                deviceCount++;
            }
        });
    }

    @Override
    public void scanningStarted() {
        setBleScanState(true);
    }

    @Override
    public void scanningStopped() {
        if(toast != null){
            toast.cancel();
        }
        setBleScanState(false);
    }

    private void setBleScanState(boolean val){
        this.isBleScanning = val;
        Log.d(TAG, "Setting Scan state to " + val);
        bt_scan.setText(val ? "Stop Scan" : "Scan For Devices");
    }

    private class ListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> bleDevices;

        public ListAdapter(){
            super();
            bleDevices = new ArrayList<>();
        }

        public void addDevice(BluetoothDevice device){
            if(!bleDevices.contains(device)){
                bleDevices.add(device);
            }
        }

        public boolean contains(BluetoothDevice device){
            if(bleDevices.contains(device)){
                return true;
            }
            else return false;
        }

        public BluetoothDevice getDevice(int position){
            return bleDevices.get(position);
        }

        public void clear(){
            bleDevices.clear();
        }

        @Override
        public int getCount(){
            return bleDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return bleDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                view = ActionConnectBTLEDeviceActivity.this.getLayoutInflater().inflate(R.layout.ble_device_row_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = view.findViewById(R.id.device_name_tv);
                viewHolder.deviceAddress = view.findViewById(R.id.device_address_tv);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = bleDevices.get(position);
            String deviceName = device.getName();
            if(deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);
            }else{
                viewHolder.deviceName.setText("Device Name Unknown");
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }
    public void onScan(View view) {
        if (!bleScanner.isScanning()) {
            Log.d(TAG, "Not currently scanning");
            deviceCount=0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions_granted = false;
                    requestLocationPermission();
                } else {
                    Log.i(TAG, "Location permission has already been granted. Starting scanning.");
                    permissions_granted = true;
                }
            } else {
                // the ACCESS_COARSE_LOCATION permission did not exist before M so....
                permissions_granted = true;
            }
            startScanning();
        } else {
            Log.d(TAG, "Already scanning");
            bleScanner.stopScanning();
        }
    }

    private void startScanning() {
        if (permissions_granted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bleDeviceListAdapter.clear();
                    bleDeviceListAdapter.notifyDataSetChanged();
                }
            });
            simpleToast("Scanning for Devices",2000);
            bleScanner.startScanning(this, SCAN_TIMEOUT);
        } else {
            Log.i(TAG, "Permission to perform Bluetooth scanning was not yet granted");
        }
    }

    private void requestLocationPermission() {
        Log.i(TAG, "Location permission has NOT yet been granted. Requesting permission.");
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.i(TAG, "Displaying location permission rationale to provide additional context.");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Required");
            builder.setMessage("Please grant Location access so this application can perform Bluetooth scanning");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    Log.d(TAG, "Requesting permissions after explanation");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for location permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                Log.i(TAG, "Location permission has now been granted. Scanning.....");
                permissions_granted = true;
                if (bleScanner.isScanning()) {
                    startScanning();
                }
            }else{
                Log.i(TAG, "Location permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void simpleToast(String message, int duration) {
        toast = Toast.makeText(this, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
