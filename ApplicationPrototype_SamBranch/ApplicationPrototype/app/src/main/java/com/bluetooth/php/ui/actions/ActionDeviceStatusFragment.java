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
import com.bluetooth.php.ui.device_control.DeviceActionsActivity;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;
import com.google.android.material.textfield.TextInputLayout;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class ActionDeviceStatusFragment extends Fragment {
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private static TextView device_status_tv;

    private View view;
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    private Button btn_send_text_command;
    private TextInputLayout command_text_layout;
    private static RelativeLayout gatt_profile_relative_layout;
    public static BleAdapterService bluetooth_le_adapter;
    private AppDataSingleton shared_data;


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

        gatt_profile_relative_layout = view.findViewById(R.id.RL_device_gatt_profile);

        btn_send_text_command = view.findViewById(R.id.send_command_btn);
        btn_send_text_command.setOnClickListener(this::onClick);

        command_text_layout = view.findViewById(R.id.command_input);

        bluetooth_le_adapter = DeviceActionsActivity.getBluetoothLeAdapter();

        if(DeviceActionsActivity.isGattConnected()){
            if(DeviceActionsActivity.isControlServicePresent()){
                updateView(true, true);
            }
            else{
                updateView(true, false);
            }
        }
        else{
            updateView(false, false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void onClick(View view){
        switch(view.getId()){
            case R.id.send_command_btn:
                Log.d(Constants.TAG, "Send Command Button Pressed");
                sendCommand();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    public static void updateView(boolean connected, boolean has_expected_services){

        if(connected){
            device_status_tv.setText(R.string.connected);
            device_status_tv.setTextColor(0xFF44FF44);
            if(has_expected_services) {
                gatt_profile_relative_layout.setVisibility(VISIBLE);
            }
        }
        else{
            device_status_tv.setText(R.string.not_connected_label);
            device_status_tv.setTextColor(0xFFFF0000);
            gatt_profile_relative_layout.setVisibility(INVISIBLE);
        }
    }
    private void sendCommand(){
        String command_text = command_text_layout.getEditText().getText().toString();

        if(!command_text.isEmpty()){
            if(command_text.length() > 256){
                command_text_layout.setError("Error: Command is too long.");
                closeKeyboard();
            }
            else {
                command_text_layout.setError(null);
                bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, command_text.getBytes());
                Log.d(Constants.TAG, "MESSAGE SENT : " + command_text);
                Log.d(Constants.TAG, "MESSAGE Bytes SENT : " + command_text.getBytes());
                command_text_layout.getEditText().setText("");
                closeKeyboard();
                Toast.makeText(getContext(), "Command: \"" + command_text + "\" Has been sent!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.d(Constants.TAG, "command is empty or invalid");
            command_text_layout.setError("Error: Command cannot be blank.");
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
