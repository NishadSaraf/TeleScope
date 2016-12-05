package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.pdx.telescope.finalproject.ece558.ece558telescope.dummy.DummyContent;

public class TelescopeActivity extends AppCompatActivity
        implements TagsFragment.OnListFragmentInteractionListener,AddTagFragment.onAddTagListener {

    public static final String TAG= TelescopeActivity.class.getSimpleName();

    //References for bluetooth management
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;  //stores all the available devices
    private BluetoothGatt mConnectedGatt;           //represents the connected device

    private Handler mHandler = new Handler();       //Handles updates to UI

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telescope);

        //Instantiating bluetooth references
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mDevices = new SparseArray<BluetoothDevice>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //TODO: make use of snackbar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_telescope, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //TODO: Stop any active scanning
        //TODO: disconnect from device
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

    /*
    * Begin a scan for new BLE devices
    */
    public void startScan() {

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
    public void stopScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }

    private void toggleBuzzer(BluetoothGatt locatingGatt, final boolean isEnable) {
        BluetoothGattCharacteristic characteristic = locatingGatt
                .getService(DeviceProfiles.TELESCOPE_SERVICE_UUID)
                .getCharacteristic(DeviceProfiles.TELESCOPE_CHARACTERISTIC_BUZZER_CONTROL);

        int buzzerValue;
        if (isEnable)
            buzzerValue=0x00;
        else
            buzzerValue= 0xFF;

        byte[] value = {(byte)buzzerValue};
        Log.d(TAG, "Writing value of size "+value.length);
        characteristic.setValue(value);
        locatingGatt.writeCharacteristic(characteristic);
    }

    /***
     * Connects to a particular device
     * @param device the device to be connected
     * @return
     */
    private boolean connect(final BluetoothDevice device) {
        if (mBluetoothAdapter == null ) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the
        // autoConnect parameter to false.
        mConnectedGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");

        return true;
    }

    /*
     * Callback handles GATT client events, such as results from
     * reading or writing a characteristic value on the server.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /***
         * Whenever there there is a change in connection status,
         * i.e when a device gets connected/disconnected
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange "
                    +DeviceProfiles.getStatusDescription(status)+" "
                    +DeviceProfiles.getStateDescription(newState));

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered:");

            for (BluetoothGattService service : gatt.getServices()) {
                Log.d(TAG, "Service: "+service.getUuid());

                if (DeviceProfiles.TELESCOPE_SERVICE_UUID.equals(service.getUuid())) {

                    List<BluetoothGattCharacteristic> charas = service.getCharacteristics();
                    for (BluetoothGattCharacteristic chara: charas)
                    {
                        Log.d(TAG, "Chara: "+chara.getUuid());
                    }
                }
            }
        }
    };

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

        /***
         * Processes a scan result
         * @param result
         */
        private void processResult(ScanResult result) {

            //Getting device from the result
            BluetoothDevice device = result.getDevice();
            Log.i(TAG, "New LE Device: " + device.getName() + " @ " + result.getRssi());

            //Add it to the collection
            mDevices.put(device.hashCode(), device);

            //Update the overflow menu
            invalidateOptionsMenu();

            stopScan();
        }
    };

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this,item.content,Toast.LENGTH_SHORT).show();
    }

    //TODO: Make onTagAdded() meaningful
    //TODO: change the parameter of onTagAdded()
    @Override
    public void onTagAdded(Uri uri) {

        //TODO: implement what should be done after tag addition
        Toast.makeText(this,"A tag has been added",Toast.LENGTH_SHORT).show();

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position)
            {
                //TODO: instantiate fragments accordingly
                case 0: return AddTagFragment.newInstance("blah","blah");
                default: return TagsFragment.newInstance(1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.add_tag_header);
                case 1:
                    return getString(R.string.tag_list_header);
                case 2:
                    return getString(R.string.group_list_header);
            }
            return null;
        }
    }


}
