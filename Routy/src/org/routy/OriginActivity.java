package org.routy;

import java.util.Date;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoLocationProviderException;
import org.routy.exception.NoNetworkConnectionException;
import org.routy.fragment.ErrorDialog;
import org.routy.model.AppProperties;
import org.routy.service.AddressService;
import org.routy.service.LocationService;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OriginActivity extends FragmentActivity {

	private final String TAG = "OriginActivity";

	private FragmentActivity context;
	
	private LocationService locationService;
	private AddressService addressService;
	
	private LocationManager locationManager;
	private EditText originAddressField;
	private Button findUserButton;
	private boolean locating;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        
        context = this;
        
        originAddressField = (EditText) findViewById(R.id.origin_address_field);
        findUserButton = (Button) findViewById(R.id.find_user_button);
        resetLocateButton();
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        addressService = new AddressService(new Geocoder(this, Locale.getDefault()), false);		// TODO make getting sensor true/false dynamic
        locationService = initLocationService();
    }
    
    
    LocationService initLocationService() {
    	return new LocationService(locationManager, AppProperties.LOCATION_ACCURACY_THRESHOLD_M) {
			
			@Override
			public void onLocationResult(Location location) {
				// Reverse geocode the location into an address and populate the TextEdit
				Date locationUpdated = new Date();
				locationUpdated.setTime(location.getTime());
				Log.v(TAG, "Location: " + 
						   "\nLat: " + location.getLatitude() + 
						   "\nLong: " + location.getLongitude() + 
						   "\nProvider: " + location.getProvider() + 
						   "\nAccuracy: " + location.getAccuracy() +
						   "\nTime: " + locationUpdated);
				
				Address address = null;
				
				try {
					address = addressService.getAddressForLocation(location);
				} catch (NoNetworkConnectionException e) { 
					AppError.showErrorDialog(context, "Routy needs some sort of internet connection.  Please try again when you've got one.");
				} catch (AmbiguousAddressException e) {
					if (e.getAddresses().size() > 0) {
						address = e.getFirstAddress();
					}
				}
				
				if (address != null) {
					StringBuilder addressStr = new StringBuilder();
					
					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressStr.append(address.getAddressLine(i));
						addressStr.append(", ");
					}
					addressStr.append(address.getAddressLine(address.getMaxAddressLineIndex()));
					
					Log.v(TAG, "Address: " + addressStr.toString());
					originAddressField.setText(addressStr.toString());
				} else {
					Log.e(TAG, "Couldn't reverse geocode the address.");
					AppError.showErrorDialog(context, "Routy's embarrassed he couldn't find an address for your location.  Would you mind typing it in?");
					
				}
				
				resetLocateButton();
			}
			
			
			@Override
			public void onLocationSearchTimeout() {
				Log.e(TAG, "Getting user location timed out.");
				AppError.showErrorDialog(context, "Routy's embarrassed he couldn't your location.  Would you mind typing it in?");
				resetLocateButton();
			}
		};
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_origin, menu);
        return true;
    }
    
    
    private void resetLocateButton() {
		findUserButton.setText(R.string.find_user_prompt);
		locating = false;
	}
    
    
    public void findUserLocation(View view) {
    	if (!locating) {
        	findUserButton.setText(R.string.stop_locating);
        	
        	try {
        		locating = true;
        		locationService.getCurrentLocation();		// TODO implement the timeout around this
        	} catch (NoLocationProviderException e) {
        		Log.e(TAG, e.getMessage());
        		resetLocateButton();
        	}
    	} else {
    		locationService.stop();
    		resetLocateButton();
    	}
    }
    
    
    public void goToDestinationsScreen(View view) {
    	// validate the origin address, store it, and move on to the destinations screen
    	Log.v(TAG, "Origin: " + originAddressField.getText());
    	
    	if (originAddressField.getText() == null || originAddressField.getText().length() == 0) {
    		AppError.showErrorDialog(this, "Please enter an origin address or click Find Me and Routy will locate you.");
    	}
    	
    	// Validate the given address string
    	Address originAddress = null;
    	try {
    		originAddress = addressService.getAddressForLocationString(originAddressField.getText().toString());
    	} catch (AmbiguousAddressException e) {
    		Log.d(TAG, "Got more than one result for the given origin address.  I'm using the first one.");
    		originAddress = e.getFirstAddress();
    	} catch (NoNetworkConnectionException e) {
    		// TODO can we fire an intent for them to turn on their data connection in settings (like you can with GPS)?
    		Toast.makeText(this, "No data connection...can't verify address.", Toast.LENGTH_LONG).show();	// XXX temp
    		
    		/*WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    		wifiMgr.setWifiEnabled(true);*/
    	}
    	
    	if (originAddress != null) {
			Toast.makeText(this, "Origin address is good!", Toast.LENGTH_LONG).show();	// XXX temp
			Intent destinationIntent = new Intent(getBaseContext(), DestinationActivity.class);
			destinationIntent.putExtra("origin", originAddress);	// Android Address is Parcelable, so no need for Bundle
			startActivity(destinationIntent);
		} else {
			Toast.makeText(this, "Bad origin address.", Toast.LENGTH_LONG).show();	// XXX temp
		}
    }
    
    
    /*void showErrorDialog(String message) {
    	FragmentManager fm = getSupportFragmentManager();
    	ErrorDialog errorDialog = new ErrorDialog(message);
    	errorDialog.show(fm, "fragment_error_message");
    }*/
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	locationService.stop();
    }
}
