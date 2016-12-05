package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

public class TeleLauncher extends AppCompatActivity {

    public static final String TAG= TeleLauncher.class.getSimpleName();

    //References for bluetooth management
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;  //stores all the available devices
    private BluetoothGatt mConnectedGatt;           //represents the connected device

    private Handler mHandler = new Handler();       //Handles updates to UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiating bluetooth references
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mDevices = new SparseArray<BluetoothDevice>();
    }


    /**
     * Actions to be done while resuming activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        /***
         * Ensure that bluetooth permission is available and
         * bluetooth is turned on otherwise, prompt user to do so
         */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        /*
         * Checks whether the device supports bluetooth low energy
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, getString(R.string.no_ble_support), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
}
