package com.example.applicationprototype;

import androidx.lifecycle.ViewModel;

public class SharedViewAppData extends ViewModel {
    private BTLE_Device btle_device;

    public BTLE_Device getBtle_device() {
        return btle_device;
    }

    public void setBtle_device(BTLE_Device btle_device) {
        this.btle_device = btle_device;
    }
}
