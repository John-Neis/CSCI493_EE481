package com.bluetooth.php.ui.actions;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.ui.device_control.DeviceActionsActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ActionSelectGripFragment extends Fragment {
    private View view;
    private BleAdapterService bluetooth_le_adapter;
    private static RelativeLayout select_grip_option_container;
    private ImageView cmd_img_1;
    private ImageView cmd_img_2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_action_select_grip,container,false);
        init_ui_elements();
        return view;
    }

    private void init_ui_elements(){
        cmd_img_1 = view.findViewById(R.id.cmd_open_hand);
        cmd_img_1.setOnClickListener(this::onClick);

        cmd_img_2 = view.findViewById(R.id.cmd_closed_hand);
        cmd_img_2.setOnClickListener(this::onClick);

        select_grip_option_container = view.findViewById(R.id.select_grip_option_container);

        if(DeviceActionsActivity.isGattConnected() && DeviceActionsActivity.isControlServicePresent()){
            select_grip_option_container.setVisibility(View.VISIBLE);
        }
        else{
            select_grip_option_container.setVisibility(View.INVISIBLE);
        }

        bluetooth_le_adapter = DeviceActionsActivity.getBluetoothLeAdapter();
    }
    public static void updateViewOnConnect(){
        if(select_grip_option_container != null) {
            select_grip_option_container.setVisibility(View.VISIBLE);
        }
    }
    public static void updateViewOnDisconnect(){
        if(select_grip_option_container != null) {
            select_grip_option_container.setVisibility(View.INVISIBLE);
        }
    }
    private void onClick(View view){
        String command_data;
        switch(view.getId()){

            case R.id.cmd_open_hand:

                image_click_effect(cmd_img_1);
                Toast.makeText(getContext(), "Sending CMD 1", Toast.LENGTH_SHORT).show();
                command_data = "CMD 1";
                bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, command_data.getBytes());
                break;

            case R.id.cmd_closed_hand:

                image_click_effect(cmd_img_2);
                Toast.makeText(getContext(), "Sending CMD 2", Toast.LENGTH_SHORT).show();
                command_data = "CMD 2";
                bluetooth_le_adapter.writeCharacteristic(BleAdapterService.PHP_CONTROL_SERVICE, BleAdapterService.PHP_WRITE_CHARACTERISTIC, command_data.getBytes());
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    private void image_click_effect(@NonNull ImageView v){
        v.setEnabled(false);
        v.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
        Timer img_click_timer = new Timer();
        img_click_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.clearColorFilter();
                        v.setEnabled(true);
                    }
                });
            }
        }, 400);
    }
}
