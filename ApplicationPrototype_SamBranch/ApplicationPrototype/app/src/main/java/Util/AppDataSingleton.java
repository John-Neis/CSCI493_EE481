package Util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import androidx.lifecycle.ViewModel;

import Bluetooth.BTStateBroadcastReceiver;

//Singleton Class that is used to share certain data throughout the entire application.
public class AppDataSingleton extends ViewModel {
    private static AppDataSingleton instance;
    private ScanSettings scanSettings;
    private ScanFilter scanFilter;
    private BluetoothAdapter bluetooth_adapter;
    private BluetoothManager bluetooth_manager;
    private boolean device_bluetooth_enabled;
    private BTStateBroadcastReceiver btStateUpdateReceiver;
    private String deviceName;

    //constructor is private since this is a singleton class
    private AppDataSingleton() {

    }
    public static void initAppDataSingleton(){
        instance = new AppDataSingleton();
    }
    public static AppDataSingleton getInstance(){
        return instance;
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

    public BluetoothManager getBluetooth_manager() {
        return bluetooth_manager;
    }

    public void setBluetoothManager(BluetoothManager bluetooth_manager) {
        this.bluetooth_manager = bluetooth_manager;
    }
}
