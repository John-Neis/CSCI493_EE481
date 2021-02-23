package com.bluetooth.php.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{
    private SwitchMaterial toggle_bt_enable_disable;
    private BluetoothAdapter BA;
    private String tag = "Settings Fragment DEBUG : ";
    private AppDataSingleton shared_data;
    private TextInputLayout scanDurationLayout;
    private TextView currentScanDuration;
    private Button saveData;
    private int duration= 0;
    private int tempDuration = 0;
    private View view1;
    @Nullable
    @Override
    public View getView() {
        return view1;
    }

    public void setView(View view) {
        this.view1 = view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment,container,false);
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
        init_scan_duration(view);
        saveData = view.findViewById(R.id.saveDataBtn);
        scanDurationLayout = view.findViewById(R.id.scanDurationInput);
        saveData.setOnClickListener(new View.OnClickListener()
        {@Override
            public void onClick(View v) {saveData(v);}
        });
    }
    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

    }
    private boolean validateInput(){
        if(!scanDurationLayout.getEditText().getText().toString().isEmpty() && scanDurationLayout != null) {
            try {
                tempDuration = Integer.parseInt(scanDurationLayout.getEditText().getText().toString().trim());
            } catch (NumberFormatException nfe) {
                scanDurationLayout.setError("Invalid Input");
                nfe.printStackTrace();
                return false;
            }
            if (tempDuration > 10000 || tempDuration < 1000) {
                scanDurationLayout.setError("Duration must be between 1000 and 10000 ms");
                Log.d(Constants.TAG, "Duration outside valid range. Must be between 1000 and 10000ms");
                return false;
            }
            return true;
        }
        else{
            closeKeyboard();
            Toast.makeText(getContext(), "Nothing to Save", Toast.LENGTH_SHORT).show();
            Log.d(Constants.TAG, "No Settings Have Changed");
            return false;
        }
    }
    public void saveData(View v){
        if(validateInput()){
            scanDurationLayout.setError(null);
            shared_data.setScanDuration(tempDuration);
            duration = shared_data.getScanDuration();
            Log.d(Constants.TAG, "duration : " + duration);

            currentScanDuration.setText(duration + " ms");
            scanDurationLayout.getEditText().setText("");
            closeKeyboard();
            Toast.makeText(getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d(Constants.TAG, "Errors in settings");
        }


    }
    public void init_scan_duration(View view){
        duration = shared_data.getScanDuration();
        currentScanDuration = view.findViewById(R.id.currentScanDuration);
        currentScanDuration.setText(duration + " ms");
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
                toggle_bt_enable_disable.setChecked(false);
                toggle_bt_enable_disable.setText("Enable Bluetooth");
                Log.d(tag, "bluetooth request denied");

            } else if (shared_data.is_device_bluetooth_enabled()) {
                toggle_bt_enable_disable.setChecked(true);
                toggle_bt_enable_disable.setText("Bluetooth Enabled");
                Log.d(tag, "bluetooth is enabled");
            }

        }
    }
}