package com.example.applicationprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ActionConnectBTLEFragment extends Fragment {
    private CheckBox bt_enable;
    private Button bt_search;
    private TextView bt_device_name;
    private ListView bt_device_list;
    private BluetoothAdapter BA;
    /*private final BluetoothManager BM =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);*/
    private Set<BluetoothDevice> pairedDevices;
    private String tag = "DEBUG - CONNECT BTLE FRAGMENT -";
    private Button connect_btn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.action_connect_bt_device_fragment,container,false);
//        bt_enable = view.findViewById(R.id.bt_enabled_cb);
        bt_search = view.findViewById(R.id.bt_search);
        bt_device_name = view.findViewById(R.id.bt_device_name);
        bt_device_list = view.findViewById(R.id.bt_device_list);
//        connect_btn = view.findViewById(R.id.connect_btn);

        //get the host device name and display it
        String device_name = getLocalBTDeviceName();
        bt_device_name.setText("Device Name : " + device_name);

        //Set the bluetooth adapter. This adapter represents the local device's bt adapter
        //Allows fundamental bluetooth tasks :
        //1. instantiates a BluetoothDevice using a known MAC address
        //2. Create a BluetoothServerSocket to listen for connection requests from other devices
        //3. Start a scan for Bluetooth LE devices
        BA = BluetoothAdapter.getDefaultAdapter();

        //If BA is null then Bluetooth is not supported on this device
        if (BA == null) {
            Toast.makeText(getContext(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            Log.d(tag, "bluetooth not support");
            return view;
        }
//        //If BA is enabled then we can continue with searching for devices.
//        if (BA.isEnabled()) {
//            //set the enable BT
//            bt_enable.setChecked(true);
//            bt_enable.setText("Bluetooth Enabled");
//            Toast.makeText(getContext(), "Bluetooth Is Enabled", Toast.LENGTH_SHORT).show();
//            Log.d(tag, "bluetooth is enabled");
//        }

//        //set a listener for the check box that enables/disables BT
//        bt_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                //If check box is deselected
//                if (!isChecked) {
//                    BA.disable();
//                    bt_enable.setText("Enable Bluetooth");
//                    Toast.makeText(getContext(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
//                }
//                //If check box is selected
//                else {
//                    //request permission from user to turn on bluetooth
//                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(intentOn, 0);
//                    bt_enable.setText("Bluetooth Enabled");
//                    Toast.makeText(getContext(), "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDevices();
            }
        });


        return view;
    }
    private void listDevices() {
        pairedDevices = BA.getBondedDevices();

//        ArrayList list = new ArrayList();
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> device_info;
        for (BluetoothDevice btd : pairedDevices) {
            device_info = new HashMap<String, String>();
            device_info.put("Name", btd.getName());
            device_info.put("Address", btd.getAddress());
            list.add(device_info);
        }

        Toast.makeText(getContext(), "Showing Available Devices", Toast.LENGTH_SHORT).show();
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.bt_device_data_layout,
                new String[]{"Name", "Address"}, new int[]{R.id.line_name, R.id.line_address});

        bt_device_list.setAdapter(adapter);
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
}
