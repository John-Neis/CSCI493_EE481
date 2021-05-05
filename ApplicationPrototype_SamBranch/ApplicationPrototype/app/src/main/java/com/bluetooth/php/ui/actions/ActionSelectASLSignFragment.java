package com.bluetooth.php.ui.actions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.ui.device_control.DeviceActionsActivity;
import com.bluetooth.php.util.AslSignAdapter;
import com.bluetooth.php.util.Constants;
import com.bluetooth.php.util.Datasource;

import java.util.List;

public class ActionSelectASLSignFragment extends Fragment {
    private static BleAdapterService bluetooth_le_adapter;
    View view;
    List<Sign> signList;
    TextView listSize;
    static RecyclerView AslRecyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_action_select_asl_sign,container,false);
        init_ui_elements();
        return view;
    }
    public void init_ui_elements(){
        signList = Datasource.loadSigns();
        Log.d(Constants.TAG, "ASL Sign List Size: " + signList.size());
        AslRecyclerView = view.findViewById(R.id.sign_recycler_view);
        AslSignAdapter adapter = new AslSignAdapter(signList, getContext());
        AslRecyclerView.setAdapter(adapter);
        AslRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bluetooth_le_adapter = DeviceActionsActivity.getBluetoothLeAdapter();;

        if(DeviceActionsActivity.isGattConnected() && DeviceActionsActivity.isControlServicePresent()){
            Log.d(Constants.TAG, "ASL Recycler View Visible");
            AslRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
            Log.d(Constants.TAG, "ASL Recycler View NOT Visible");
            AslRecyclerView.setVisibility(View.INVISIBLE);
        }

    }

    public static void updateViewOnConnect(){
        if(AslRecyclerView != null){
            AslRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public static void updateViewOnDisconnect(){
        if(AslRecyclerView != null){
            AslRecyclerView.setVisibility(View.INVISIBLE);
        }
    }
    public static void sendAslCommand(String letter){
        String commandString = Datasource.getAslCommand(letter);
        bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, commandString.getBytes());

    }
}
