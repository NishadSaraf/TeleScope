package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
            Log.i(TAG,"Bluetooth is disabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        /*
         * Halting the app if the device doesn't supports bluetooth low energy
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.i(TAG,getString(R.string.no_ble_support));
            Toast.makeText(this, getString(R.string.no_ble_support), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //TODO: Stop any active scanning
        //TODO: disconnect from device
    }

    /*
     * Begin a scan for new BLE devices
     */
    private void startScan() {

        //Setting scan filter - scan for what
        ScanFilter scanFilter = new ScanFilter.Builder()
                //.setServiceUuid(new ParcelUuid(DeviceProfile.TELESCOPE_SERVICE_UUID))
                .build();
        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(scanFilter);

        //Creating scan settings- the mode of scanning
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        /***
         * Starting the scan,
         * passing the mScanCallback that handles events of scan results
         */
        mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, mScanCallback);
    }

    /*
     * Terminate any active scans
     */
    private void stopScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }

    /*
     * Callback handles results from new devices that appear
     * during a scan. Batch results appear when scan delay
     * filters are enabled.
     */
    private ScanCallback mScanCallback = new ScanCallback() {

        /***
         * Callback when a BLE advertisement has been found.
         * @param callbackType
         * @param result
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult");

            processResult(result);
        }

        /***
         * Callback when batch results are delivered
         * @param results
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "onBatchScanResults: "+results.size()+" results");

            //Processing each result
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        /***
         * Callback when scan could not be started
         * @param errorCode
         */
        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "LE Scan Failed: "+errorCode);
        }

        private void processResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.i(TAG, "New LE Device: " + device.getName() + " @ " + result.getRssi());
            //Add it to the collection
            mDevices.put(device.hashCode(), device);
            //Update the overflow menu
            invalidateOptionsMenu();

            stopScan();
        }
    };
}
