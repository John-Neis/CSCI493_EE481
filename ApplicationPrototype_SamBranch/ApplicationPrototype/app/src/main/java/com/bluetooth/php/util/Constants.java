package com.bluetooth.php.util;

public class Constants {
    public static final String TAG = "PHP Prototype";
    public static final String FIND = "Find BLE Devices";
    public static final String STOP_SCANNING = "Stop Scanning";
    public static final int DEFAULT_SCAN_DURATION = 3000; //Scan duration set to 5 seconds
    public static final boolean DEFAULT_FILTER_SETTING = true; // scan filters enabled, app only looks for PHP Controller devices.

    //Delete these once the peripheral control example is deleted
    public static final byte [] ALERT_LEVEL_LOW = { (byte) 0x00};
    public static final byte [] ALERT_LEVEL_MID = { (byte) 0x01};
    public static final byte [] ALERT_LEVEL_HIGH = { (byte) 0x02};
    public static final String [] TEMPERATURE_UNITS = {"Celsius", "Fahrenheit"};
    private static String test_command = "0123";
    public static final byte [] TEST_COMMAND = test_command.getBytes();
}
