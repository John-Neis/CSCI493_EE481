package com.bluetooth.php.ui.device_control;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluetooth.php.R;
import com.bluetooth.php.bluetooth.BleAdapterService;
import com.bluetooth.php.ui.actions.ActionDeviceStatusFragment;
import com.bluetooth.php.ui.actions.ActionSelectASLSignFragment;
import com.bluetooth.php.ui.actions.ActionSelectGripFragment;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class DeviceActionsActivity extends AppCompatActivity{
    private String prospective_device_name_data;
    private String prospective_device_address_data;
    private TextView selected_device_name_tv;
    private TextView selected_device_address_tv;
    private AppDataSingleton shared_data = AppDataSingleton.getInstance();
    private BottomNavigationView botNavView;
    private Bundle mSavedInstanceState;
    private Toolbar toolbar;
    private androidx.appcompat.app.ActionBar actionBar;
    public static BleAdapterService bluetooth_le_adapter;
    private Button btn_connect;
    private TextView message_tv;
    private static boolean gattConnected = false;
    private static boolean control_service_present = false;

    public static BleAdapterService getBluetoothLeAdapter(){
        return bluetooth_le_adapter;
    }
    public static boolean isGattConnected(){
        return gattConnected;
    }
    public static boolean isControlServicePresent(){
        return control_service_present;
    }

    public final ServiceConnection service_connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(Constants.TAG, "onServiceConnected");
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Constants.TAG, "onServiceDisconnected");
            bluetooth_le_adapter = null;
        }
    };
    @SuppressLint("HandlerLeak")
    public Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                    break;

                case BleAdapterService.GATT_CONNECTED:
                    Log.d(Constants.TAG, "GATT Connected");
                    showMsg("GATT CONNECTED");
                    bluetooth_le_adapter.discoverServices();
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    Log.d(Constants.TAG, "GATT Disconnected");
                    showMsg("GATT DISCONNECTED");
                    ActionSelectGripFragment.updateViewOnDisconnect();
                    ActionSelectASLSignFragment.updateViewOnDisconnect();
                    ActionDeviceStatusFragment.updateView(false, false);
                    onDisconnect();
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "GATT Services Discovered");
                    List<BluetoothGattService> service_list = bluetooth_le_adapter.getSupportedGattServices();
                    for(BluetoothGattService svc : service_list){
                        Log.d(Constants.TAG,
                                "UUID=" + svc.getUuid().toString().toUpperCase()
                                        + " INSTANCE=" + svc.getInstanceId());
                        if(svc.getUuid().toString().equalsIgnoreCase(BleAdapterService.PHP_CONTROL_SERVICE)) {
                            control_service_present = true;
                        }
                    }
                    if(control_service_present){
                        showMsg("Device Has Expected Services");
                        ActionDeviceStatusFragment.updateView(gattConnected, true);
                        ActionSelectGripFragment.updateViewOnConnect();
                        ActionSelectASLSignFragment.updateViewOnConnect();
                    }else{
                        showMsg("Device Does Not Have Expected Services");
                    }

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_READ:
                    Log.d(Constants.TAG, "GATT Characteristic read");

                    break;
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "GATT Characteristic written");
                    bundle = msg.getData();
                    Log.d(Constants.TAG,
                            "Service=" + bundle.get(BleAdapterService.PARCEL_SERVICE_UUID).toString().toUpperCase()
                                    + " Characteristic="
                                    + bundle.get(BleAdapterService.PARCEL_CHARACTERISTIC_UUID).toString().toUpperCase());
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

        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, service_connection, Context.BIND_AUTO_CREATE);
        showMsg("Ready...");
    }

    private void init_ui_elements(){
        prospective_device_name_data = shared_data.getProspectiveDeviceName();
        prospective_device_address_data = shared_data.getProspectiveDeviceAddress();

        botNavView = findViewById(R.id.device_control_bottom_nav_bar);
        botNavView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        message_tv = findViewById(R.id.msgTextView);
        message_tv.setText("NonNUll");

        btn_connect = findViewById(R.id.btnConnect);
        btn_connect.setOnClickListener(this::onClick);


        toolbar = findViewById(R.id.app_device_action_status_toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment()).commit();
        botNavView.getMenu().findItem(R.id.action_device_status).setChecked(true);

        if (mSavedInstanceState == null) {
            Log.d(Constants.TAG, "device action activity -> init_ui_elements -> saved instance state null");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment()).commit();
            actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.device_status_title);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(Constants.TAG, "System Back Pressed");
        super.onBackPressed();
        if(gattConnected){
            onDisconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(Constants.TAG, "On Option Items Selected");
        if(gattConnected){
            onDisconnect();
        }
        startActivity(new Intent(this, ConnectBTLEDeviceActivity.class));
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_device_status:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionDeviceStatusFragment())
                        .commit();
                actionBar=getSupportActionBar();
                assert actionBar != null;
                actionBar.setTitle(R.string.device_status_title);
                botNavView.getMenu().findItem(R.id.action_device_status).setChecked(true);
                Log.d(Constants.TAG, "ACTION DEVICE STATUS on click");
                break;

            case R.id.action_grips:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionSelectGripFragment())
                        .commit();
                actionBar=getSupportActionBar();
                assert actionBar != null;
                actionBar.setTitle(R.string.select_grip_label);
                botNavView.getMenu().findItem(R.id.action_grips).setChecked(true);
                Log.d(Constants.TAG, "ACTION GRIPS on click");
                break;

            case R.id.action_asl:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_bot_nav_container, new ActionSelectASLSignFragment())
                        .commit();
                actionBar=getSupportActionBar();
                assert actionBar != null;
                actionBar.setTitle(R.string.select_asl_sign_label);
                botNavView.getMenu().findItem(R.id.action_asl).setChecked(true);
                Log.d(Constants.TAG, "ACTION ASL on click");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return false;
    }
    public void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message_tv.setText(msg);
            }
        });
    }
    @SuppressLint("NonConstantResourceId")
    private void onClick(View view){
        switch(view.getId()){
            case R.id.btnConnect:
                if(!gattConnected) {
                    onConnect();
                }else{
                    onDisconnect();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    private void onConnect(){
        Log.d(Constants.TAG, "onConnect. Device is: " + prospective_device_name_data);
        Log.d(Constants.TAG, "onConnect. Address is: " + prospective_device_address_data);
        showMsg("onConnect");
        if (bluetooth_le_adapter != null) {
            if (bluetooth_le_adapter.connect(prospective_device_address_data)) {
                btn_connect.setText(R.string.disconnect_device_label);
                btn_connect.setBackgroundColor(0xFFFF0000);
                gattConnected = true;
            } else {
                showMsg("onConnect: failed to connect");
            }
        } else {
            showMsg("onConnect: bluetooth_le_adapter=null");
        }
    }

    private void onDisconnect(){
        showMsg("onDisconnect");
        if(gattConnected){
            gattConnected = false;
            bluetooth_le_adapter.disconnect();
            btn_connect.setBackgroundColor(0xFF018786);
            btn_connect.setText(R.string.connect_device_label);
            ActionDeviceStatusFragment.updateView(false,false);
        }
    }

}
