package com.example.applicationprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppDataSingleton shared_data;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private boolean device_bt_enabled = false;
    private BTStateBroadcastReceiver bt_state_update_receiver;

    private String TAG = "MAIN ACTIVITY : DEBUG: ";
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

        init_bluetooth_shared_data();

        init_nav_drawer_menu(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(bt_state_update_receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(bt_state_update_receiver);
    }

    private void init_bluetooth_shared_data() {
        /*
            Initialize the shared data singleton in the main Activity on create method
            So that the singleton is set before any other activities/fragments run
        */
        AppDataSingleton.initAppDataSingleton();
        /* Set the shared data singleton to the currently created instance*/
        shared_data = AppDataSingleton.getInstance();
        /*create the bluetooth manager */
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        /*store the bluetooth manager in the shared data singleton*/
        shared_data.setBluetooth_manager(bluetoothManager);
        /* create the bluetooth adapter for the device */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*store the bluetooth manager in the shared data singleton*/
        shared_data.setBluetooth_adapter(bluetoothAdapter);

        /*
            store the default as false until we figure out how to check if a btle device is already
            connected.
         */
        shared_data.set_btle_device_connected(false);
        /*
            initially check if the devices bluetooth is enabled and initialize the data in the shared
            object
        */
        if(bluetoothAdapter.isEnabled()){
            device_bt_enabled = true;
            shared_data.set_device_bluetooth_enabled(true);
        }

        bt_state_update_receiver = new BTStateBroadcastReceiver(getApplicationContext());
    }

    private void init_nav_drawer_menu(Bundle savedInstanceState) {

        //Create custom toolbar to open drawer nav menu
        toolbar = findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        //Configure drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.openNavDrawer, R.string.closeNavDrawer
        );
        //add drawer open listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_nav_menu_container, new ActionConnectBTLEFragment());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_nav_menu_container, new ActionConnectBTLEFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_menu_bt_connection);
            actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.bt_connect_a_device_label);
        }
        // Handle Bluetooth
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        //  displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
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
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new ActionDeviceStatusFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.device_status_label);
                break;
            case R.id.nav_menu_bt_connection:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new ActionConnectBTLEFragment()).commit();
                actionBar = getSupportActionBar();
//                Intent intent = new Intent(this, BTLE_Device_List_Activity);
                actionBar.setTitle(R.string.bt_connect_a_device_label);
                break;
            case R.id.nav_menu_grips:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new ActionSelectGripFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_grip_label);
                break;
            case R.id.nav_menu_ASL:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new ActionSelectASLSignFragment()).commit();
                actionBar=getSupportActionBar();
                actionBar.setTitle(R.string.select_asl_sign_label);
                break;
            case R.id.nav_menu_about_team:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new AboutTeamFragment()).commit();
                actionBar = getSupportActionBar();
                actionBar.setTitle(R.string.about_team_label);
                break;
            case R.id.nav_menu_about_mission:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new AboutMissionFragment()).commit();
                actionBar = getSupportActionBar();
                actionBar.setTitle(R.string.about_mission_label);
                break;
            case R.id.nav_menu_settings:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_nav_menu_container, new SettingsFragment()).commit();
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