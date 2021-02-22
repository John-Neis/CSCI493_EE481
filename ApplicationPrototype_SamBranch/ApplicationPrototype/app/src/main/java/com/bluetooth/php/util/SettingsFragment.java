package com.bluetooth.php.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{
    private CheckBox toggle_bt_enable_disable;
    private BluetoothAdapter BA;
    private String tag = "Settings Fragment DEBUG : ";
    private AppDataSingleton shared_data;
    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment,container,false);
        /* Get the shared data singleton that will be used for any data needed in different activities */
        shared_data = AppDataSingleton.getInstance();
        /* Function that initializes the settings */
        init_settings(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init_settings(View view) {
        Log.d(tag, "In Init Settings Method");
        /* set the default check state and activate the check box listener */
        init_bt_toggle_enable_disable(view);
    }
    public void init_bt_toggle_enable_disable(View view){
        BA = shared_data.getBluetooth_adapter();
        BA = BluetoothAdapter.getDefaultAdapter();
//        //If BA is enabled then we can continue with searching for devices.
        toggle_bt_enable_disable = view.findViewById(R.id.toggle_bt_enable_disable);

        if (shared_data.is_device_bluetooth_enabled() == true) {
            //set the enable BT
            toggle_bt_enable_disable.setChecked(true);
            toggle_bt_enable_disable.setText("Bluetooth Enabled");
//            Toast.makeText(getContext(), "Bluetooth Is Enabled", Toast.LENGTH_SHORT).show();
            Log.d(tag, "bluetooth is enabled");
        }else{
            toggle_bt_enable_disable.setChecked(false);
            shared_data.set_device_bluetooth_enabled(false);
            Log.d(tag, "bluetooth is disabled");
        }

        //set a listener for the check box that enables/disables BT
        toggle_bt_enable_disable.setOnCheckedChangeListener(this::onCheckedChanged);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_bt_enable_disable:
                Log.d(tag, "In on checked changed listener for bt enable disable");
                toggleBtEnableDisable(buttonView, isChecked);
                break;
            default:

                break;
        }
    }

    private void toggleBtEnableDisable(CompoundButton buttonView, boolean isChecked) {
        //If check box is deselected
        if (!isChecked) {
            BA.disable();
            toggle_bt_enable_disable.setText("Enable Bluetooth");
            shared_data.set_device_bluetooth_enabled(false);
        }
        //If check box is selected
        else {
            //request permission from user to turn on bluetooth
            Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentOn, 0);
                /*
                    After requesting user for permission to turn on bluetooth, check if
                    the bluetooth enable request was approved.
                */
            if (!shared_data.is_device_bluetooth_enabled()) {
                toggle_bt_enable_disable.setText("Enable Bluetooth");
                Log.d(tag, "bluetooth request denied");

            } else if (shared_data.is_device_bluetooth_enabled()) {
                toggle_bt_enable_disable.setText("Bluetooth Enabled");
                Log.d(tag, "bluetooth is enabled");
            }

        }
    }
}