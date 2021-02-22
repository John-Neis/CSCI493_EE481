package com.bluetooth.php.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.bluetooth.php.Constants;
import com.bluetooth.php.about.AboutMissionFragment;
import com.bluetooth.php.about.AboutTeamFragment;
import com.bluetooth.php.actions.ActionConnectBTLEDeviceActivity;
import com.bluetooth.php.actions.ActionDeviceStatusFragment;
import com.bluetooth.php.actions.ActionSelectASLSignFragment;
import com.bluetooth.php.actions.ActionSelectGripFragment;
import com.bluetooth.php.bluetooth.BTStateBroadcastReceiver;
import com.bluetooth.php.R;
import com.bluetooth.php.util.AppDataSingleton;
import com.bluetooth.php.util.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppDataSingleton shared_data;
    private BluetoothAdapter BA;
    private BTStateBroadcastReceiver bt_state_update_receiver;
    private int REQUEST_ENABLE_BT = 1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private androidx.appcompat.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Starting content view? */
        setContentView(R.layout.activity_main);
        //Initialize the shared data object
        AppDataSingleton.initAppDataSingleton();
        shared_data = AppDataSingleton.getInstance();

        //Initialize certain bluetooth classes
        initBleSharedData();

        //prepare the navigation menu for use
        initNavDrawerMenu(savedInstanceState);

    }
    public void initBleSharedData(){
        BA = BluetoothAdapter.getDefaultAdapter();
        shared_data.setBluetoothAdapter(BA);

        bt_state_update_receiver = new BTStateBroadcastReceiver(this);
        shared_data.setBtStateUpdateReceiver(bt_state_update_receiver);
        shared_data.set_device_bluetooth_enabled(BA.isEnabled());
    }
    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(bt_state_update_receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(bt_state_update_receiver);
    }

    private void initNavDrawerMenu(Bundle savedInstanceState) {

        //Create custom toolbar to open drawer nav menu
        toolbar = findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        //Configure drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout == null){
            Log.d(Constants.TAG, "drawer Layout is null");
        }
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.openNavDrawer, R.string.closeNavDrawer
        );
        //add drawer open listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_nav_menu_container, new ActionDeviceStatusFragment()).commit();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_nav_menu_container, new ActionDeviceStatusFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_menu_device_status);
            actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.device_status_label);
        }
        // Handle Bluetooth
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        //  displays a dialog requesting user permission to enable Bluetooth.
        if (BA == null || !BA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override//function that allows the back button to close the navigation drawer
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //This event listener changes the context to the desired fragment depending what the user
    //has selected from the nav menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentActivity fragmentActivity = this;
        switch (item.getItemId()) {
            case R.id.nav_menu_device_status:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new ActionDeviceStatusFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.device_status_label);
                break;
            case R.id.nav_menu_connect_bt_device:
                Intent intent = new Intent(this, ActionConnectBTLEDeviceActivity.class);
                startActivity(intent);
                break;
//            case R.id.nav_menu_bt_connection:
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_nav_menu_container, new ActionConnectBTLEFragment()).commit();
//                actionBar = getSupportActionBar();
////                Intent intent = new Intent(this, BTLE_Device_List_Activity);
//                actionBar.setTitle(R.string.bt_connect_a_device_label);
//                break;
            case R.id.nav_menu_grips:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new ActionSelectGripFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_grip_label);
                break;
            case R.id.nav_menu_ASL:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new ActionSelectASLSignFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_asl_sign_label);
                break;
            case R.id.nav_menu_about_team:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new AboutTeamFragment()).commit();
                actionBar = getSupportActionBar();
                actionBar.setTitle(R.string.about_team_label);
                break;
            case R.id.nav_menu_about_mission:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new AboutMissionFragment()).commit();
                actionBar = getSupportActionBar();
                actionBar.setTitle(R.string.about_mission_label);
                break;
            case R.id.nav_menu_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_nav_menu_container, new SettingsFragment()).commit();
                actionBar = getSupportActionBar();
                actionBar.setTitle(R.string.settings_label);
//                Intent intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}