package edu.pdx.telescope.finalproject.ece558.ece558telescope;

/**
 * Created by Spoorthi Chandra.K on 12/5/2016.
 */

import android.bluetooth.BluetoothDevice;

/***
 * Class that handles one BLE tag
 */
public class BLETag {

    // Attributes of a BLE tag
    private String mMACAddress;
    private String mDeviceName;
    private BluetoothDevice mDevice;
    private boolean isSavedTag;

    /***
     * Default constructor
     */
    public BLETag() {
        this.setmDeviceName("Unknown tag");
    }

    /***
     * Parametrized constructor
     * @param mMACAddress MAC address of the tag device
     * @param mDeviceName Name of tag device
     * @param mDevice Bluetooth device reference
     * @param isSavedTag is the tag already saved
     */
    public BLETag(String mMACAddress, String mDeviceName, BluetoothDevice mDevice, boolean isSavedTag) {
        this.setmMACAddress( mMACAddress);
        this.setmDeviceName(mDeviceName);
        this.setmDevice(mDevice);
        this.setSavedTag(isSavedTag);
    }

    public boolean isSavedTag() {
        return isSavedTag;
    }

    public void setSavedTag(boolean savedTag) {
        isSavedTag = savedTag;
    }

    public String getmMACAddress() {
        return mMACAddress;
    }

    public void setmMACAddress(String mMACAddress) {
        this.mMACAddress = mMACAddress;
    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }



}
