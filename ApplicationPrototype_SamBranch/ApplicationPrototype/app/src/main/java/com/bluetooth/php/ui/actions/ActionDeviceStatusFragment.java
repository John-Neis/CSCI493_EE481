package com.bluetooth.php.ui.actions;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattService;
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

import java.util.List;


public class ActionDeviceStatusFragment extends Fragment {
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private TextView device_status_tv;
    private TextView message_tv;
    private View view;
    private boolean connected = false;
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    private Button btn_connect;
    private Button btn_test;
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
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                    break;

                case BleAdapterService.GATT_CONNECTED:
                    Log.d(Constants.TAG, "GATT Connected");
                    showMsg("GATT CONNECTED");
                    bluetooth_le_adapter.discoverServices();
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    Log.d(Constants.TAG, "GATT Disconnected");
                    showMsg("GATT DISCONNECTED");
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "GATT Services Discovered");
                    List<BluetoothGattService> service_list = bluetooth_le_adapter.getSupportedGattServices();
                    boolean control_service_present = false;
                    for(BluetoothGattService svc : service_list){
                        Log.d(Constants.TAG,
                                "UUID=" + svc.getUuid().toString().toUpperCase()
                                        + " INSTANCE=" + svc.getInstanceId());
                        if(svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.PHP_CONTROL_SERVICE)) {
                            control_service_present = true;
                        }
                    }
                    if(control_service_present){
                        showMsg("Device Has Expected Services");
                    }else{
                        showMsg("Device Does Not Have Expected Services");
                    }

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    Log.d(Constants.TAG, "GATT Characteristic read");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "GATT Characteristic written");
                    bundle = msg.getData();
                    Log.d(Constants.TAG,
                            "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                    + " Characteristic="
                                    + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
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
        showMsg("READY");
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

        btn_connect = view.findViewById(R.id.btnConnect);
        btn_connect.setOnClickListener(this::onClick);

        btn_test = view.findViewById(R.id.text_write_char_btn);
        btn_test.setOnClickListener(this::onClick);

        message_tv = view.findViewById(R.id.msgTextView);
        message_tv.setText("");

        Intent gattServiceIntent = new Intent(getContext(), BleAdapterService.class);
        getContext().bindService(gattServiceIntent, service_connection, getContext().BIND_AUTO_CREATE);
    }

    private void onClick(View view){
        switch(view.getId()){
            case R.id.btnConnect:
                if(!connected) {
                    onConnect();
                }else{
                    onDisconnect();
                }
                    break;
            case R.id.text_write_char_btn:
                    Log.d(Constants.TAG, "Test Button Pressed");
                    bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, Constants.TEST_COMMAND);
                    Log.d(Constants.TAG, "MESSAGE SENT : " + Constants.TEST_COMMAND.toString());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    private void onConnect(){
        Log.d(Constants.TAG, "onConnect. Device is: " + prospective_device_address_data);
        showMsg("onConnect");
        if (bluetooth_le_adapter != null) {
            if (bluetooth_le_adapter.connect(prospective_device_address_data)) {
                btn_connect.setText(R.string.disconnect_device_label);
                btn_connect.setBackgroundColor(0xFFFF0000);
                device_status_tv.setText(R.string.connected);
                device_status_tv.setTextColor(0xFF44FF44);
                btn_test.setEnabled(true);
                connected = true;
            } else {
                showMsg("onConnect: failed to connect");
            }
        } else {
            showMsg("onConnect: bluetooth_le_adapter=null");
        }
    }
    private void onDisconnect(){
        showMsg("onDisconnect");
        if(connected){
            connected = false;
            bluetooth_le_adapter.disconnect();
            btn_connect.setBackgroundColor(0xFF018786);
            btn_connect.setText(R.string.connect_device_label);
            device_status_tv.setText(R.string.not_connected_label);
            device_status_tv.setTextColor(0xFFFF0000);
            btn_test.setEnabled(false);
        }
    }
    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message_tv.setText(msg);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(service_connection);
        bluetooth_le_adapter = null;
    }
}
