package Bluetooth;

import android.bluetooth.BluetoothDevice;

public interface ScanResultsConsumer {
    void candidateDevice(BluetoothDevice device,
                                byte [] scan_record,
                                int rssi);
    void scanningStarted();
    void scanningStopped();
}
