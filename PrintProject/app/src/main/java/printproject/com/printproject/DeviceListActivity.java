
package printproject.com.printproject;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
/**
 * @author shohrab.uddin, RONGTA
 * This Activity appears when user wants to connect with a printer. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    public static final String PERMISSIONS_REQUEST_LOCATION=100;
    public static final int CURRENT_ANDROID_VERSION = android.os.Build.VERSION.SDK_INT;
    

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bluetooth_device_list);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);
        
        alertDialogBuilder = new AlertDialog.Builder(
				getApplicationContext());

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            permissionChecking();
            }
        });
        
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_layout_bluetooth_device);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_layout_bluetooth_device);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }
    
    private void permissionChecking(){
	  //--------------------------------------LOCATION Requesting Permission at Run Time--------------------------------------
      	// Here, thisActivity is the current activity
      	
      	if (CURRENT_ANDROID_VERSION >= Build.VERSION_CODES.M) { //Marshmallow
    
      		if (ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
      				!= PackageManager.PERMISSION_GRANTED) {
    
      			// Should we show an explanation? This method returns true if the app has requested this
      			// permission previously and the user denied the request.
      			if (ActivityCompat.shouldShowRequestPermissionRationale(DeviceListActivity.this,
      					Manifest.permission.ACCESS_COARSE_LOCATION)) {
    
      				// Show an explanation to the user *asynchronously* -- don't block
      				// this thread waiting for the user's response! After the user
      				// sees the explanation, try again to request the permission.
      				
      	
    			alertDialogBuilder.setTitle("Location Service");
    			alertDialogBuilder
    				.setMessage("We need to access your device's location service in order to use bluetooth.")
    				.setCancelable(false)
    				.setPositiveButton("Allow",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    							ActivityCompat.requestPermissions(DeviceListActivity.this,
          								new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
          								PERMISSIONS_REQUEST_LOCATION);
          								dialog.cancel();
    					}
    				  })
    				.setNegativeButton("Not allow",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						dialog.cancel();
    					}
    				});
    
    				// create alert dialog
    				AlertDialog alertDialog = alertDialogBuilder.create();
    				alertDialog.show();
      			} else {// No explanation needed, we can request the permission.
    
      				ActivityCompat.requestPermissions(DeviceListActivity.this,
      						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
      						PERMISSIONS_REQUEST_LOCATION);
    
      				// MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE is an
      				// app-defined int constant. The callback method gets the
      				// result of the request.
      			}
      		}else{ //permission already granted
      			mNewDevicesArrayAdapter.clear();
                doDiscovery();
      		}
      	}else{//android version lower than 23
      		mNewDevicesArrayAdapter.clear();
            doDiscovery();
      	}
    }
    
    //When your app requests permissions, the system presents a dialog box to the user.
  	//When the user responds, the system invokes your app's onRequestPermissionsResult()
  	//method, passing it the user response.
  	@Override
  	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

  		switch (requestCode) {
  			case PERMISSIONS_REQUEST_LOCATION: {
  				// If request is cancelled, the result arrays are empty.
  				if (grantResults.length > 0
  						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
  					mNewDevicesArrayAdapter.clear();
  		            doDiscovery();

  				} else {
                      Toast.makeText(DeviceListActivity.this,"Permission is Denied!",Toast.LENGTH_LONG).show();
  				}
  				return;
  			}

  			// other 'case' lines to check for other
  			// permissions this app might request
  		}
  	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }


    /**
     * The on-click listener for all devices in the ListViews
     */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and
     * changes the title when discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };


}
