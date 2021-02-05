package com.example.applicationprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BluetoothAdapter bluetoothAdapter;

    boolean BTLE_device_connected = false;
    private String TAG = "MAIN ACTIVITY : DEBUG: ";
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter BA;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private androidx.appcompat.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


//        BTLE_Device connected_device = new BTLE_Device();
        //Creates a shared model that will be used to share data between
        //fragments
        SharedViewAppData appData = ViewModelProviders.of(this).
                get(SharedViewAppData.class);



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