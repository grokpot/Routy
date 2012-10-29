package org.routy;

import java.util.Date;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoLocationProviderException;
import org.routy.exception.NoNetworkConnectionException;
import org.routy.fragment.ErrorDialog;
import org.routy.service.AddressService;
import org.routy.service.LocationService;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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

	private LocationService locationService;
	private AddressService addressService;
	
	private LocationManager locationManager;
	private EditText originAddressField;
	private Button findUserButton;
	private boolean locating;
	
	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        
        context = this;
        
        originAddressField = (EditText) findViewById(R.id.origin_address_field);
        findUserButton = (Button) findViewById(R.id.find_user_button);
        resetLocateButton();
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        addressService = new AddressService(new Geocoder(this, Locale.getDefault()));
        locationService = initLocationService();
    }
    
    
    LocationService initLocationService() {
    	return new LocationService(locationManager, 10) {
			
			@Override
			public void onLocationResult(Location location) {
				// Reverse geocode the location into an address and populate the TextEdit
				Log.v(TAG, "Location: " + 
						   "\nLat: " + location.getLatitude() + 
						   "\nLong: " + location.getLongitude() + 
						   "\nProvider: " + location.getProvider() + 
						   "\nAccuracy: " + location.getAccuracy() +
						   "\nTime: " + new Date());
				
				Address address = addressService.getAddressForLocation(location);
				if (address != null) {
					StringBuilder addressStr = new StringBuilder();
					
					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressStr.append(address.getAddressLine(i));
						addressStr.append(", ");
					}
					addressStr.append(address.getAddressLine(address.getMaxAddressLineIndex()));
					
					Log.v(TAG, "Address: " + addressStr.toString());
					originAddressField.setText(addressStr.toString());
					resetLocateButton();
				} else {
					Log.e(TAG, "Couldn't reverse geocode the address.");
					// TODO Make this an alert dialog
					Toast.makeText(context, "Routy couldn't find an address for your location.  Would you mind typing it in?", Toast.LENGTH_LONG).show();
				}
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
        		locationService.getCurrentLocation();
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
    		showErrorDialog("Please enter an origin address or click Find Me and Routy will locate you.");
    	}
    	
    	// Validate the given address string
    	try {
    		Address originAddress = addressService.getAddressForLocationName(originAddressField.getText().toString());
    		if (originAddress != null) {
    			Toast.makeText(this, "Origin address is good!", Toast.LENGTH_LONG).show();	// XXX temp
    			Intent destinationIntent = new Intent(getBaseContext(), DestinationActivity.class);
    			destinationIntent.putExtra("origin", originAddress);	// Android Address is Parcelable, so no need for Bundle
    			startActivity(destinationIntent);
    		} else {
    			Toast.makeText(this, "Bad origin address.", Toast.LENGTH_LONG).show();	// XXX temp
    		}
    	} catch (AmbiguousAddressException e) {
    		// TODO display a message to the user asking them to be more specific??
    		Toast.makeText(this, e.getMessage() + " is ambiguous", Toast.LENGTH_LONG).show();	// XXX temp
    	} catch (NoNetworkConnectionException e) {
    		// TODO can we fire an intent for them to turn on their data connection in settings (like you can with GPS)?
    		Toast.makeText(this, "No data connection...can't verify address.", Toast.LENGTH_LONG).show();	// XXX temp
    	}
    }
    
    
    void showErrorDialog(String message) {
    	FragmentManager fm = getSupportFragmentManager();
    	ErrorDialog errorDialog = new ErrorDialog(message);
    	errorDialog.show(fm, "fragment_error_message");
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	locationService.stop();
    }
}
