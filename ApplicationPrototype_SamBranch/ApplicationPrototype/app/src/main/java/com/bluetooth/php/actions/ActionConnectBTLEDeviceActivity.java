package com.bluetooth.php.actions;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bluetooth.php.util.Constants;
import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleScanner;
import com.bluetooth.php.bluetooth.ScanResultsConsumer;
import com.bluetooth.php.ui.PeripheralControlActivity;
import com.bluetooth.php.util.AppDataSingleton;

import java.util.ArrayList;


public class ActionConnectBTLEDeviceActivity extends AppCompatActivity implements ScanResultsConsumer {
    private AppDataSingleton shared_data;
    private boolean isBleScanning = false;
    private Handler handler = new Handler();
    private ListAdapter bleDeviceListAdapter;
    private BleScanner bleScanner;
    private TextView deviceName;
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissions_granted=false;
    private int deviceCount =0;
    private Toast toast;

    static class ViewHolder {
        public TextView deviceName;
        public TextView deviceAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared_data = AppDataSingleton.getInstance();
        setContentView(R.layout.action_connect_btle_device_activity);

        setButtonText();
        deviceName = findViewById(R.id.bt_device_name);
        deviceName.setText("Device Name: " +BluetoothAdapter.getDefaultAdapter().getName());

        bleDeviceListAdapter = new ListAdapter();

        ListView listView = (ListView) this.findViewById(R.id.bt_device_list);
        listView.setAdapter(bleDeviceListAdapter);

        bleScanner = new BleScanner(this.getApplicationContext());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (isBleScanning) {
                    bleScanner.stopScanning();
                }

                BluetoothDevice device = bleDeviceListAdapter.getDevice(position);
                if (toast != null) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void candidateBleDevice(final BluetoothDevice device, byte[] scan_record, int rssi) {
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
        setScanState(true);
    }

    @Override
    public void scanningStopped() {
        if (toast != null) {
            toast.cancel();
        }
        setScanState(false);
    }

    private void setButtonText() {
        String text="";
        text = Constants.FIND;
        final String button_text = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) ActionConnectBTLEDeviceActivity.this.findViewById(R.id.bt_scan)).setText(button_text);
            }
        });
    }

    private void setScanState(boolean value) {
        isBleScanning = value;
        Log.d(Constants.TAG,"Setting scan state to "+value);
        ((Button) this.findViewById(R.id.bt_scan)).setText(value ? Constants.STOP_SCANNING : Constants.FIND);
    }

    private class ListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> ble_devices;

        public ListAdapter() {
            super();
            ble_devices = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            if (!ble_devices.contains(device)) {
                ble_devices.add(device);
            }
        }

        public boolean contains(BluetoothDevice device) {
            return ble_devices.contains(device);
        }

        public BluetoothDevice getDevice(int position) {
            return ble_devices.get(position);
        }

        public void clear() {
            ble_devices.clear();
        }

        @Override
        public int getCount() {
            return ble_devices.size();
        }

        @Override
        public Object getItem(int i) {
            return ble_devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = ActionConnectBTLEDeviceActivity.this.getLayoutInflater().inflate(R.layout.ble_device_row_entry, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.deviceName);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = ble_devices.get(i);
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("unknown device");
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    public void onScan(View view) {
        if (!bleScanner.isScanning()) {
            Log.d(Constants.TAG, "Not currently scanning");
            deviceCount =0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions_granted = false;
                    requestLocationPermission();
                } else {
                    Log.i(Constants.TAG, "Location permission has already been granted. Starting scanning.");
                    permissions_granted = true;
                }
            } else {
                // the ACCESS_COARSE_LOCATION permission did not exist before M so....
                permissions_granted = true;
            }
            startScanning();
        } else {
            Log.d(Constants.TAG, "Already scanning");
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
            simpleToast(Constants.SCANNING,2000);
            bleScanner.startScanning(this);
        } else {
            Log.i(Constants.TAG, "Permission to perform Bluetooth scanning was not yet granted");
        }
    }

    private void requestLocationPermission() {
        Log.i(Constants.TAG, "Location permission has NOT yet been granted. Requesting permission.");
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.i(Constants.TAG, "Displaying location permission rationale to provide additional context.");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Required");
            builder.setMessage("Please grant Location access so this application can perform Bluetooth scanning");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    Log.d(Constants.TAG, "Requesting permissions after explanation");
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
            Log.i(Constants.TAG, "Received response for location permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                Log.i(Constants.TAG, "Location permission has now been granted. Scanning.....");
                permissions_granted = true;
                if (bleScanner.isScanning()) {
                    startScanning();
                }
            }else{
                Log.i(Constants.TAG, "Location permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void simpleToast(String message, int duration) {
        toast = Toast.makeText(this, message, duration);
        toast.show();
    }

}
