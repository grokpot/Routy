package org.routy.provider;

import java.util.Locale;

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
		addressService = new AddressService(geocoder);
	}
	
	
	public void testGetAddressFromCoordinates() {
		Address result = addressService.getAddressForCoordinates(30.390895097516477, -97.69777324050665);
		
		if (result == null) {
			Log.e(TAG, "resulting address was null");
		} else {
			for (int i = 0; i < result.getMaxAddressLineIndex(); i++) {
				Log.v(TAG, result.getAddressLine(i));
			}
		}
	}
	
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
