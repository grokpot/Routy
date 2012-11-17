package org.routy.service;

import java.io.IOException;
import java.util.Locale;

import org.routy.exception.RoutyException;

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
	
	
	/*public void testGetAddressForCoordinates() {
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
		} catch (RoutyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex() + 1; i++) {
				Log.v(TAG, result.getAddressLine(i));
			}
		}
	}*/
	
	
	public void testGetAddressForLocationNameWeb() {
		Log.v(TAG, "Get address for location name via WEB API");
		
		String locationName = "1400 South Congress Avenue, Austin, TX 78704";
		
		Address result = null;
		
		try {
			result = addressService.getAddressViaWeb(locationName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RoutyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			Log.v(TAG, result.getAddressLine(0));
			Log.v(TAG, result.getLatitude() + ", " + result.getLongitude());
		}
	}
	
	
	/*public void testGetAddressForCoordinatesWeb() {
		Log.v(TAG, "Get address for coordinates via WEB API");
		
		Address result = null;
		
		try {
			result = addressService.getAddressViaWeb(30.390895097516477, -97.69777324050665);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RoutyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex() + 1; i++) {
				Log.v(TAG, result.getAddressLine(i));
			}
		}
	}*/
	
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
