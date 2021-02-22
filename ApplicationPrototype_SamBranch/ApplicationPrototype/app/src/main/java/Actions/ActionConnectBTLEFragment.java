package Actions;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import Bluetooth.BTStateBroadcastReceiver;
import Bluetooth.BleScanner;
import Bluetooth.PeripheralControlActivity;
import Bluetooth.ScanResultsConsumer;
import Util.AppDataSingleton;
import com.example.applicationprototype.R;

import java.util.ArrayList;

@RequiresApi(api = 23)
public class ActionConnectBTLEFragment extends Fragment implements ScanResultsConsumer {
    private AppDataSingleton shared_data;
    private Button bt_search;
    private TextView bt_device_name;
    private ListView bt_device_list;
    private BluetoothAdapter BA;
    private BTStateBroadcastReceiver bt_state_update_receiver;
    private String TAG = "DEBUG - CONNECT BTLE FRAGMENT -";
    private boolean bleIsScanning = false;
    private Handler handler = new Handler();
    private ListAdapter bleDeviceListAdapter;
    private BleScanner bleScanner;
    private static final long SCAN_DURATION = 5000;
    private static final int REQUEST_LOCATION = 0;
    private static String [] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean permissionsGranted = false;
    private int deviceCount = 0;
    private Toast toast;
        static class ViewHolder {
        public TextView deviceName;
        public TextView deviceAddress;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.action_scan_for_btle_devices,container,false);
        //set up the views for this fragment
        initViews(view);

        return view;
    }
    private void initViews(View view){
        bleScanner = new BleScanner(view.getContext());
        //Get the shared data instance
        shared_data= AppDataSingleton.getInstance();
        shared_data.setDeviceName(getLocalBTDeviceName());

        //Set the device name
        bt_device_name = view.findViewById(R.id.bt_device_name);
        bt_device_name.setText("Device Name : " + shared_data.getDeviceName());

        //Set Button Text
        setButtonText(view);

        //Create the list adapter for devices
        bleDeviceListAdapter = new ListAdapter();

        //Initialize the list and set its adapter and on click listener.
        bt_device_list = view.findViewById(R.id.bt_device_list);
        bt_device_list.setAdapter(bleDeviceListAdapter);
        bt_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(bleIsScanning){
                    bleScanner.stopScanning();
                }

                BluetoothDevice device = bleDeviceListAdapter.getDevice(position);
                if(toast != null){
                    toast.cancel();
                }
                Intent intent = new Intent(getContext(), PeripheralControlActivity.class);
                intent.putExtra(PeripheralControlActivity.EXTRA_NAME, device.getName());
                intent.putExtra(PeripheralControlActivity.EXTRA_ID, device.getAddress());
                startActivity(intent);
            }
        });
        //Initialize the button and set it's on click listener
        bt_search = view.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "BLE Scan Button CLicked");
                onScan(v);
            }
        });
    }
    private void setButtonText(View view){
            String text = "Scan For Devices";
            final String button_text = text;
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    Button btn = (Button)view.findViewById(R.id.bt_search);
                    btn.setText(button_text);
                }
            });
    }

    private void setBleScanState(boolean isScanning){
            Button btn = (Button) getView().findViewById(R.id.bt_search);
            Log.d(TAG, "Setting Scan state to " +isScanning);
            if(isScanning){
                btn.setText("Stop Scan");
            }else{
                btn.setText("Scan for Devices");
            }
    }
    public void onScan(View view){
            if(!bleScanner.isScanning()){
                Log.d(TAG, "Not currently scanning");
                deviceCount = 0;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                        permissionsGranted = false;
                        requestLocationPermission();
                    } else{
                        Log.i(TAG, "Location Permission Granted. Beginning Scan");
                        permissionsGranted = true;
                    }
                } else{
                    permissionsGranted = true;
                }
                startScanning();
            } else{
                Log.d(TAG, "Already Scanning");
                bleScanner.stopScanning();
            }
    }
    private void requestLocationPermission(){
            Log.i(TAG, "Location Permission NOT Granted. Requesting Permission");
            if(ActivityCompat.shouldShowRequestPermissionRationale
                    (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)){
            Log.i(TAG, "Displaying location permission rationale");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Permission Required");
            builder.setMessage("Please grant Location access. Location access is required for " +
                    "Bluetooth Scanning");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener(){
                public void onDismiss(DialogInterface dialog){
                    Log.d(TAG, "Requesting Permissions after rationale displayed");
                    ActivityCompat.requestPermissions(getActivity(), new
                            String [] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                }
            });
            builder.show();
            } else{
                ActivityCompat.requestPermissions(getActivity(), new
                        String [] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION){
            Log.i(TAG, "Response received for location permission request.");
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //location permission has been granted
                Log.i(TAG, "Location permission has now been granted. Scanning...");
                permissionsGranted = true;
                if(bleScanner.isScanning()){
                    startScanning();
                }
            }else{
                Log.i(TAG, "Location permission was NOT Granted.");
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    private void simpleToast(String message, int duration){
            toast = Toast.makeText(getContext(), message, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
    }
    private void startScanning(){
            if(permissionsGranted){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bleDeviceListAdapter.clear();
                        bleDeviceListAdapter.notifyDataSetChanged();
                    }
                });
                simpleToast("Scanning...", 5000);
                bleScanner.startScanning(this, SCAN_DURATION);
            } else{
                Log.i(TAG, "Permission to perform bluetooth scanning denied");
            }
    }
    @Override
    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi) {
            getActivity().runOnUiThread(new Runnable() {
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

    public String getLocalBTDeviceName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        String name = BA.getName();
        if (name == null) {
            name = BA.getAddress();
        }
        return name;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    private class ListAdapter extends BaseAdapter{
        private ArrayList<BluetoothDevice> bleDevices;
        public ListAdapter(){
            super();
            bleDevices = new ArrayList<BluetoothDevice>();
        }
        public void clear(){
            bleDevices.clear();
            this.notifyDataSetChanged();
        }
        public void addDevice(BluetoothDevice device){
            if(!bleDevices.contains(device)){
                bleDevices.add(device);
            }
        }
        public boolean contains(BluetoothDevice device){
            return bleDevices.contains(device);
        }
        public BluetoothDevice getDevice(int position){
            return bleDevices.get(position);
        }
        @Override
        public int getCount(){
            return bleDevices.size();
        }
        @Override
        public Object getItem(int pos){
            return bleDevices.get(pos);
        }
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.ble_device_row_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name_tv);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address_tv);
                view.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = bleDevices.get(position);
            String deviceName = device.getName();
            if(deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);
            }else{
                viewHolder.deviceName.setText("N/A");
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
