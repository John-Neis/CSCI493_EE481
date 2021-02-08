package com.example.applicationprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

import androidx.lifecycle.ViewModel;

//Singleton Class that is used to share certain data throughout the entire application.
public class AppDataSingleton extends ViewModel {
    private static AppDataSingleton instance;
    private BTLE_Device btle_device;
    private BluetoothAdapter bluetooth_adapter;
    private BluetoothManager bluetooth_manager;
    private boolean device_bluetooth_enabled;

    public boolean is_device_bluetooth_enabled() {
        return device_bluetooth_enabled;
    }

    public void set_device_bluetooth_enabled(boolean device_bluetooth_enabled) {
        this.device_bluetooth_enabled = device_bluetooth_enabled;
    }

    public boolean is_btle_device_connected() {
        return btle_device_connected;
    }

    public void set_btle_device_connected(boolean btle_device_connected) {
        this.btle_device_connected = btle_device_connected;
    }

    private boolean btle_device_connected;

    public BluetoothManager getBluetooth_manager() {
        return bluetooth_manager;
    }

    public void setBluetooth_manager(BluetoothManager bluetooth_manager) {
        this.bluetooth_manager = bluetooth_manager;
    }

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

    public void setBluetooth_adapter(BluetoothAdapter bluetooth_adapter) {
        this.bluetooth_adapter = bluetooth_adapter;
    }

    public BTLE_Device getBtle_device() {
        return btle_device;
    }

    public void setBtle_device(BTLE_Device btle_device) {
        this.btle_device = btle_device;
    }
}
