package Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import Util.AppDataSingleton;

public class BTStateBroadcastReceiver extends BroadcastReceiver {
    private Context activity_context;
    private AppDataSingleton shared_data;

    public BTStateBroadcastReceiver(Context activity_context) {
        this.activity_context = activity_context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        shared_data= AppDataSingleton.getInstance();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show();
                    shared_data.set_device_bluetooth_enabled(false);
                    break;

                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(context, "Bluetooth is turning off...", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_SHORT).show();
                    shared_data.set_device_bluetooth_enabled(true);
                    break;

                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(context, "Bluetooth is turning on", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
