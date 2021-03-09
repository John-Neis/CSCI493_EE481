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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class ActionDeviceStatusFragment extends Fragment {
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private TextView device_status_tv;
    private TextView message_tv;
    private View view;
    private boolean gattConnected = false;
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    private Button btn_connect;
    private Button btn_send_text_command;
    private TextInputLayout command_text_layout;
    private RelativeLayout gatt_profile_relative_layout;
    private BleAdapterService bluetooth_le_adapter;
    private AppDataSingleton shared_data;

    private final ServiceConnection service_connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(Constants.TAG, "onServiceConnected");
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Constants.TAG, "onServiceDisconnected");
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
                    shared_data.setGattConnected(true);
                    bluetooth_le_adapter.discoverServices();
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    Log.d(Constants.TAG, "GATT Disconnected");
                    showMsg("GATT DISCONNECTED");
                    shared_data.setGattConnected(false);
                    gatt_profile_relative_layout.setVisibility(INVISIBLE);
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
                        gatt_profile_relative_layout.setVisibility(VISIBLE);
                    }else{
                        showMsg("Device Does Not Have Expected Services");
                        gatt_profile_relative_layout.setVisibility(INVISIBLE);
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_action_device_status,container,false);
        shared_data = AppDataSingleton.getInstance();
        gattConnected = shared_data.isGattConnected();
        init_ui_elements();
        Intent gattServiceIntent = new Intent(getContext(), BleAdapterService.class);
        requireContext().bindService(gattServiceIntent, service_connection, Context.BIND_AUTO_CREATE);
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

        message_tv = view.findViewById(R.id.msgTextView);
        message_tv.setText("");

        gatt_profile_relative_layout = view.findViewById(R.id.RL_device_gatt_profile);

        btn_send_text_command = view.findViewById(R.id.send_command_btn);
        btn_send_text_command.setOnClickListener(this::onClick);

        command_text_layout = view.findViewById(R.id.command_input);

    }

    private void onClick(View view){
        switch(view.getId()){
            case R.id.btnConnect:
                if(!gattConnected) {
                    onConnect();
                }else{
                    onDisconnect();
                }
                    break;
            case R.id.send_command_btn:
                Log.d(Constants.TAG, "Send Command Button Pressed");
                sendCommand();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    private void sendCommand(){
        String command_text = command_text_layout.getEditText().getText().toString();

        if(!command_text.isEmpty()){
            command_text_layout.setError(null);

            bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, command_text.getBytes());
            Log.d(Constants.TAG, "MESSAGE SENT : " + command_text);
            Log.d(Constants.TAG, "MESSAGE Bytes SENT : " + command_text.getBytes());
            command_text_layout.getEditText().setText("");
            closeKeyboard();
            Toast.makeText(getContext(), "Command: \"" + command_text + "\" Has been sent!", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d(Constants.TAG, "command is empty or invalid");
            command_text_layout.setError("Error: Command cannot be empty");
        }


    }
    private void onConnect(){
        Log.d(Constants.TAG, "onConnect. Device is: " + prospective_device_name_data);
        Log.d(Constants.TAG, "onConnect. Address is: " + prospective_device_address_data);
        showMsg("onConnect");
        if (bluetooth_le_adapter != null) {
            if (bluetooth_le_adapter.connect(prospective_device_address_data)) {
                btn_connect.setText(R.string.disconnect_device_label);
                btn_connect.setBackgroundColor(0xFFFF0000);
                device_status_tv.setText(R.string.connected);
                device_status_tv.setTextColor(0xFF44FF44);
                gattConnected = true;
            } else {
                showMsg("onConnect: failed to connect");
            }
        } else {
            showMsg("onConnect: bluetooth_le_adapter=null");
        }
    }
    private void onDisconnect(){
        showMsg("onDisconnect");
        if(gattConnected){
            gattConnected = false;
            bluetooth_le_adapter.disconnect();
            btn_connect.setBackgroundColor(0xFF018786);
            btn_connect.setText(R.string.connect_device_label);
            device_status_tv.setText(R.string.not_connected_label);
            device_status_tv.setTextColor(0xFFFF0000);
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
//    @Override
    public void onDestroy() {
        Log.d(Constants.TAG, "On DESTROY");
        super.onDestroy();
        requireContext().unbindService(service_connection);
        bluetooth_le_adapter = null;

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "On Resume");
        if(gattConnected){
            Log.d(Constants.TAG, "Gatt is connected");

        }
        else{
            Log.d(Constants.TAG, "Gatt not connected");
        }
    }
    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}
