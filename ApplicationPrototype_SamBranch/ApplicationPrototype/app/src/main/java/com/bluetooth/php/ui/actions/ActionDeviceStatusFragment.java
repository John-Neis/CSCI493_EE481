package com.bluetooth.php.ui.actions;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.ui.PeripheralControlActivity;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;


public class ActionDeviceStatusFragment extends Fragment {
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private TextView device_status_tv;
    private View view;
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    private Button btnConnect;
    private BleAdapterService bluetooth_le_adapter;

    private final ServiceConnection service_connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CONNECTED:
                    Log.d(Constants.TAG, "GATT Connected");

                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    Log.d(Constants.TAG, "GATT Disconnected");

                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "GATT Services Discovered");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    Log.d(Constants.TAG, "GATT Characteristic read");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "GATT Characteristic written");

                    break;
            }
        }
    };

    AppDataSingleton shared_data;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_action_device_status,container,false);
        shared_data = AppDataSingleton.getInstance();
        init_ui_elements();


        return view;
    }
    private void init_ui_elements(){
        prospective_device_name_data = shared_data.getProspectiveDeviceName();
        prospective_device_address_data = shared_data.getProspectiveDeviceAddress();

        selected_device_name_tv = view.findViewById(R.id.prospective_device_name);
        selected_device_name_tv.setText(prospective_device_name_data);

        selected_device_address_tv = view.findViewById(R.id.prospective_device_address);
        selected_device_address_tv.setText(prospective_device_address_data);

        device_status_tv = view.findViewById(R.id.device_status_tv);

        btnConnect = (Button)view.findViewById(R.id.device_connect_button);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(Constants.TAG, "onConnect. Device is: " + prospective_device_address_data);
                //showMsg("onConnect");

                if (bluetooth_le_adapter != null) {
                    if(bluetooth_le_adapter.isConnected() && btnConnect.getText().equals("Disconnect")) {
                        bluetooth_le_adapter.disconnect();
                        btnConnect.setText("Connect");
                        device_status_tv.setText("Not Connected");
                        return;
                    } else if (bluetooth_le_adapter.connect(prospective_device_address_data) && btnConnect.getText().equals("Connect")) {
                        //((Button) PeripheralControlActivity.this.findViewById(R.id.connectButton)).setEnabled(false);
                        btnConnect.setText("Disconnect");
                        device_status_tv.setText("Connected");
                    } else {
                        //showMsg("onConnect: failed to connect");
                        Log.d(Constants.TAG, "onConnect: failed to connect");
                    }
                } else {
                    //showMsg("onConnect: bluetooth_le_adapter=null");
                    Log.d(Constants.TAG, "onConnect: bluetooth_le_adapter=null");
                }
            }
        });

        Intent gattServiceIntent = new Intent(getContext(), BleAdapterService.class);
        getContext().bindService(gattServiceIntent, service_connection, getContext().BIND_AUTO_CREATE);
    }

//    public void onStatusConnect(View view) {
//        Log.d(Constants.TAG, "Pressed Connect Button to Connect to PHP Controller");
//    }
}
