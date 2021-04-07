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
import com.bluetooth.php.ui.device_control.DeviceActionsActivity;
import com.bluetooth.php.util.AslSignAdapter;
import com.bluetooth.php.util.Constants;
import com.bluetooth.php.util.Datasource;

import java.util.List;

public class ActionSelectASLSignFragment extends Fragment {
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


        if(DeviceActionsActivity.isGattConnected() && DeviceActionsActivity.isControlServicePresent()){
            AslRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
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
}
