package com.bluetooth.php.ui.actions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;
import com.bluetooth.php.util.AppDataSingleton;


public class ActionDeviceStatusFragment extends Fragment {
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private View view;
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    AppDataSingleton shared_data;
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
    }
}
