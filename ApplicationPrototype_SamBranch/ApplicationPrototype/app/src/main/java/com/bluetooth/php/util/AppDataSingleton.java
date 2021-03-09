package com.bluetooth.php.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.bluetooth.php.bluetooth.BTStateBroadcastReceiver;
import com.bluetooth.php.bluetooth.BleAdapterService;

//Singleton Class that is used to share certain data throughout the entire application.
public class AppDataSingleton extends ViewModel {
    private static final AppDataSingleton instance = new AppDataSingleton();
    private ScanSettings scanSettings;
    private ScanFilter scanFilter;
    private BluetoothAdapter bluetooth_adapter;
    private boolean device_bluetooth_enabled;
    private BTStateBroadcastReceiver btStateUpdateReceiver;
    private String deviceName;
    private int scanDuration = Constants.DEFAULT_SCAN_DURATION;
    private String prospectiveDeviceName;
    private boolean filtersEnabled = Constants.DEFAULT_FILTER_SETTING;
    private boolean gattConnected= false;
    private Intent bleAdapterServiceIntent;
    @SuppressLint("StaticFieldLeak")

    public Intent getBleAdapterServiceIntent(){
        return bleAdapterServiceIntent;
    }
    public void setBleAdapterServiceIntent(Intent intent){
        this.bleAdapterServiceIntent = intent;
    }
    public boolean isGattConnected(){
        return gattConnected;
    }
    public void setGattConnected(boolean connected){
        this.gattConnected = connected;
    }
    public boolean areFiltersEnabled(){
        return filtersEnabled;
    }
    public void setFiltersEnabled(boolean enabled){
        this.filtersEnabled = enabled;
    }

    public String getProspectiveDeviceName() {
        return prospectiveDeviceName;
    }

    public void setProspectiveDeviceName(String prospectiveDeviceName) {
        this.prospectiveDeviceName = prospectiveDeviceName;
    }

    public String getProspectiveDeviceAddress() {
        return prospectiveDeviceAddress;
    }

    public void setProspectiveDeviceAddress(String prospectiveDeviceAddress) {
        this.prospectiveDeviceAddress = prospectiveDeviceAddress;
    }

    private String prospectiveDeviceAddress;

    //constructor is private since this is a singleton class
    private AppDataSingleton() {}

    public static AppDataSingleton getInstance(){
        return instance;
    }

    public void setScanDuration(int duration){
        this.scanDuration = duration;
    }

    public int getScanDuration(){
        return scanDuration;
    }
    public BluetoothAdapter getBluetooth_adapter() {
        return bluetooth_adapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetooth_adapter) {
        this.bluetooth_adapter = bluetooth_adapter;
    }
    public ScanFilter getScanFilter() {
        return scanFilter;
    }

    public void setScanFilter(ScanFilter scanFilter) {
        this.scanFilter = scanFilter;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BTStateBroadcastReceiver get_bt_state_update_receiver() {
        return btStateUpdateReceiver;
    }

    public void setBtStateUpdateReceiver(BTStateBroadcastReceiver bt_state_update_receiver) {
        this.btStateUpdateReceiver = bt_state_update_receiver;
    }

    public boolean is_device_bluetooth_enabled() {
        return device_bluetooth_enabled;
    }

    public void set_device_bluetooth_enabled(boolean device_bluetooth_enabled) {
        this.device_bluetooth_enabled = device_bluetooth_enabled;
    }
}
