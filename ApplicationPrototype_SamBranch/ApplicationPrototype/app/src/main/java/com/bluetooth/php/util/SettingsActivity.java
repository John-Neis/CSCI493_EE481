package com.bluetooth.php.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluetooth.php.R;
import com.bluetooth.php.ui.MainActivity;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private SwitchMaterial toggle_bt_enable_disable;
    private SwitchMaterial toggle_bt_filters_enable_disable;
    private BluetoothAdapter BA;
    private String tag = "Settings Fragment DEBUG : ";
    private AppDataSingleton shared_data = AppDataSingleton.getInstance();
    private TextInputLayout scanDurationLayout;
    private TextView currentScanDuration;
    private Button saveData;
    private boolean filtersEnabled = false;
    Toolbar toolbar;
    private int duration= 0;
    private int tempDuration = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        /* Get the shared data singleton that will be used for any data needed in different activities */
        toolbar = this.findViewById(R.id.app_main_toolbar);
        toolbar.setTitle(R.string.settings_label);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /* Function that initializes the settings */
        init_settings();
    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init_settings() {
        Log.d(tag, "In Init Settings Method");
        /* set the default check state and activate the check box listener */
        init_bt_toggle_enable_disable();
        init_scan_filters_toggle();
        init_scan_duration();
        saveData = this.findViewById(R.id.saveDataBtn);
        scanDurationLayout = this.findViewById(R.id.scanDurationInput);
        saveData.setOnClickListener(new View.OnClickListener()
        {@Override
            public void onClick(View v) {saveData(v);}
        });
    }
    private void init_scan_filters_toggle(){
        filtersEnabled = shared_data.areFiltersEnabled();
        Log.d(Constants.TAG, "Filters Enabled: " + filtersEnabled);
        toggle_bt_filters_enable_disable = findViewById(R.id.toggle_bt_filters);
        if(filtersEnabled == true){
            toggle_bt_filters_enable_disable.setChecked(true);
            toggle_bt_filters_enable_disable.setText("Filters Enabled");
            shared_data.setFiltersEnabled(true);
            Log.d(tag, "Filters are enabled");
        }
        else{
            toggle_bt_filters_enable_disable.setChecked(false);
            toggle_bt_filters_enable_disable.setText("Enable Filters");
            shared_data.setFiltersEnabled(false);
            Log.d(tag, "Filters are disabled");
        }
        toggle_bt_filters_enable_disable.setOnCheckedChangeListener(this::onCheckedChanged);
    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                           this.getSystemService(Context.INPUT_METHOD_SERVICE);

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
                Log.d(Constants.TAG, "Duration outside valid range. Must be between 1000 and 10000 ms");
                return false;
            }
            return true;
        }
        else{
            closeKeyboard();
            if(scanDurationLayout.isErrorEnabled()){
                scanDurationLayout.setError("Nothing to Save. Enter a value between 1000 and 10000 ms");
            }
            Toast.makeText(this, "Nothing to Save", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d(Constants.TAG, "Errors in settings");
        }


    }
    public void init_scan_duration(){
        duration = shared_data.getScanDuration();
        currentScanDuration = this.findViewById(R.id.currentScanDuration);
        currentScanDuration.setText(duration + " ms");
    }
    public void init_bt_toggle_enable_disable(){
        BA = shared_data.getBluetooth_adapter();
        BA = BluetoothAdapter.getDefaultAdapter();
//        //If BA is enabled then we can continue with searching for devices.
        toggle_bt_enable_disable = this.findViewById(R.id.toggle_bt_enable_disable);

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
            case R.id.toggle_bt_filters:
                Log.d(tag, "In on checked changed listener for scan filter enable disable");
                toggleBtFiltersEnableDisable(buttonView, isChecked);
                break;
            default:

                break;
        }
    }
    private void toggleBtFiltersEnableDisable(CompoundButton buttonView, boolean isChecked){
        //If switch is deselected
        if(!isChecked){
            shared_data.setFiltersEnabled(false);
            Log.d(Constants.TAG, "1.Filters enable disable set: " + shared_data.areFiltersEnabled());
            toggle_bt_filters_enable_disable.setText("Enable Filters");
            toggle_bt_filters_enable_disable.setChecked(false);
            Toast.makeText(this, "Device Filters Disabled", Toast.LENGTH_SHORT).show();
        }
        //If switch is selected
        else{
            shared_data.setFiltersEnabled(true);
            Log.d(Constants.TAG, "2.Filters enable disable set: " + shared_data.areFiltersEnabled());
            toggle_bt_filters_enable_disable.setText("Filters Enabled");
            toggle_bt_filters_enable_disable.setChecked(true);
            Toast.makeText(this, "Device Filters Enabled", Toast.LENGTH_SHORT).show();
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