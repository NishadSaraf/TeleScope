package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;

import java.util.UUID;

/**
 * Created by Jaisil on 12/4/2016.
 */

public class DeviceProfiles {

    //Service UUID to expose the service from the BLE module
    public static UUID TELESCOPE_SERVICE_UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
    //Read-only characteristic providing number of elapsed seconds since offset
    public static UUID TELESCOPE_CHARACTERISTIC_BUZZER_CONTROL = UUID.fromString("19b10001-e8f2-537e-4f6c-d104768a1214");

    /***
     * Parses the connection status into string
     * @param state state to be parsed
     * @return parsed connection status string
     */
    public static String getStateDescription(int state) {
        switch (state) {
            case BluetoothProfile.STATE_CONNECTED:
                return "Connected";
            case BluetoothProfile.STATE_CONNECTING:
                return "Connecting";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "Disconnected";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "Disconnecting";
            default:
                return "Unknown State "+state;
        }
    }

    /***
     * Parses the status description into string
     * @param status status to be parsed
     * @return parsed status string
     */
    public static String getStatusDescription(int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                return "SUCCESS";
            default:
                return "Unknown Status "+status;
        }
    }
}
