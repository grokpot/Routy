package org.routy.service;

import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoNetworkConnectionException;
import org.routy.service.AddressService;

import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;
import android.util.Log;

public class AddressServiceTest extends AndroidTestCase {

	private final String TAG = "AddressServiceTest";
	
	private Geocoder geocoder;
	private AddressService addressService;
	
	public void setUp() throws Exception {
		super.setUp();
		
		geocoder = new Geocoder(getContext(), Locale.getDefault());
		addressService = new AddressService(geocoder, false);
	}
	
	
	public void testGetAddressForCoordinates() {
		Log.v(TAG, "Get address for coordinates via Geocoder backend");
		
		Address result = null;
		try {
			result = addressService.getAddressForCoordinates(30.390895097516477, -97.69777324050665);
		} catch (AmbiguousAddressException e) {
			Log.e(TAG, "ambiguous address -- " + e.getAddresses().size() + " found");
			for (Address a : e.getAddresses()) {
				for (int i = 0; i < a.getMaxAddressLineIndex() + 1; i++) {
					Log.v(TAG, a.getAddressLine(i));
				}
			}
			
			result = e.getAddresses().get(0);
		} catch (NoNetworkConnectionException e) {
			Log.e(TAG, e.getMessage());
		}
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex() + 1; i++) {
				Log.v(TAG, result.getAddressLine(i));
			}
		}
	}
	
	
	public void testGetAddressForLocationNameWeb() {
		Log.v(TAG, "Get address for location name via WEB API");
		
		String locationName = "1400 South Congress Avenue, Austin, TX 78704";
		
		Address result = addressService.getAddressViaWeb(locationName);
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex() + 1; i++) {
				Log.v(TAG, result.getAddressLine(0));
				Log.v(TAG, result.getLatitude() + ", " + result.getLongitude());
			}
		}
	}
	
	
	public void testGetAddressForCoordinatesWeb() {
		Log.v(TAG, "Get address for coordinates via WEB API");
		
		Address result = addressService.getAddressForCoordinatesWeb(30.390895097516477, -97.69777324050665);
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex() + 1; i++) {
				Log.v(TAG, result.getAddressLine(i));
			}
		}
	}
	
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
