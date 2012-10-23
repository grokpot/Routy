package org.routy;

import java.util.Date;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoNetworkConnectionException;
import org.routy.service.AddressService;
import org.routy.service.LocationService;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OriginActivity extends Activity {

	private final String TAG = "OriginActivity";

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
        
        originAddressField = (EditText) findViewById(R.id.origin_address_field);
        findUserButton = (Button) findViewById(R.id.find_user_button);
        resetLocateButton();
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        addressService = new AddressService(new Geocoder(this, Locale.getDefault()));
        locationService = new LocationService(locationManager, 10) {
			
			@Override
			public void onLocationResult(Location location) {
				// TODO Reverse geocode the location into an address and populate the TextEdit
				
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
        	} catch (Exception e) {
        		Log.e(TAG, e.getMessage());
        		resetLocateButton();
        	}
    	} else {
    		locationService.stop();
    		resetLocateButton();
    	}
    }
    
    
    public void goToDestinationsScreen(View view) {
    	// TODO validate the origin address, store it, and move on to the destinations screen
    	Log.v(TAG, "Origin: " + originAddressField.getText());
    	
    	// Validate the given address string
    	try {
    		addressService.getAddressForLocationName(originAddressField.getText().toString());
    		Toast.makeText(this, "Origin address is good!", Toast.LENGTH_LONG).show();	// XXX temp
    	} catch (AmbiguousAddressException e) {
    		// TODO display a message to the user asking them to be more specific??
    		Toast.makeText(this, e.getMessage() + " is ambiguous", Toast.LENGTH_LONG).show();	// XXX temp
    	} catch (NoNetworkConnectionException e) {
    		// TODO can we fire an intent for them to turn on their data connection in settings (like you can with GPS)?
    		Toast.makeText(this, "No data connection...can't verify address.", Toast.LENGTH_LONG).show();	// XXX temp
    	}
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	locationService.stop();
    }
}
