package com.bluetooth.php.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluetooth.php.R;
import com.bluetooth.php.audio.AlarmManager;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.ui.actions.ActionDeviceStatusFragment;
import com.bluetooth.php.ui.actions.ActionSelectASLSignFragment;
import com.bluetooth.php.ui.actions.ActionSelectGripFragment;
import com.bluetooth.php.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class DeviceActionsActivity extends AppCompatActivity{
    private BottomNavigationView botNavView;
    private Bundle mSavedInstanceState;
    private Toolbar toolbar;
    private BleAdapterService bluetoothLeAdapter;
    private androidx.appcompat.app.ActionBar actionBar;
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ADDRESS = "id";

    private final ServiceConnection service_connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeAdapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetoothLeAdapter.setActivityHandler(message_handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeAdapter = null;
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CONNECTED:
                    Log.d(Constants.TAG, "GATT Connected");

                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    Log.d(Constants.TAG, "GATT Disconnected");

                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "GATT Services Discovered");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    Log.d(Constants.TAG, "GATT Characteristic read");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "GATT Characteristic written");

                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_device_actions);
        init_ui_elements();

    }

    private void init_ui_elements(){

        botNavView = findViewById(R.id.device_control_bottom_nav_bar);
        botNavView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        toolbar = findViewById(R.id.app_device_action_status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment()).commit();
        botNavView.getMenu().findItem(R.id.action_device_status).setChecked(true);

        if (mSavedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment()).commit();
            actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.device_status_title);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, ConnectBTLEDeviceActivity.class));
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        return super.onOptionsItemSelected(item);

    }

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int idx = item.getItemId();
        switch(item.getItemId()){
            case R.id.action_help:
                botNavView.getMenu().findItem(R.id.action_help).setChecked(true);
                Log.d(Constants.TAG, "ACTION INSTRUCTIONS on click");
                break;

            case R.id.action_device_status:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.device_status_title);
                botNavView.getMenu().findItem(R.id.action_device_status).setChecked(true);
                Log.d(Constants.TAG, "ACTION DEVICE STATUS on click");
                break;

            case R.id.action_grips:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionSelectGripFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_grip_label);
                botNavView.getMenu().findItem(R.id.action_grips).setChecked(true);
                Log.d(Constants.TAG, "ACTION GRIPS on click");
                break;

            case R.id.action_asl:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionSelectASLSignFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_asl_sign_label);
                botNavView.getMenu().findItem(R.id.action_asl).setChecked(true);
                Log.d(Constants.TAG, "ACTION ASL on click");
                break;
        }
        return false;
    }


}
