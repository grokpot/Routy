package org.routy.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;
import android.util.Log;

public class RouteProviderTest extends AndroidTestCase {

	private final String TAG = "RouteProviderTest";
	private Geocoder geocoder;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		geocoder = new Geocoder(getContext(), Locale.getDefault());
	}
	
	
	public void testDistanceMatrix() {
		// Create an origin address to work with
		Address origin = null;
		try {
            List<Address> origins = geocoder.getFromLocationName("600 West Martin Luther King Junior Boulevard, Austin, TX", 1);
            if (origins != null && origins.size() > 0) {
            	origin = origins.get(0);
            }
    	} catch (Exception e) {
    		System.err.println("Error geocoding origin address.");
    	}
		
		if (origin == null) {
			fail("No origin.");
		}
		
		// Get some destinations to work with
		List<Address> destinations = null;
		try {
			destinations = getDestinationAddresses();
		} catch (IOException e) {
			System.err.println("Couldn't get destination addresses\n" + e.getMessage());
		}
		
		if (destinations == null) {
			fail("No destinations.");
		}
		
		RouteProvider routeProvider = null;
		try {
			long start = System.currentTimeMillis();
			routeProvider = new RouteProvider(origin, destinations, RouteOptimizePreference.PREFER_DISTANCE, false);
			Log.i(TAG, "Got a RouteProvider (with distance matrix) in " + (System.currentTimeMillis() - start) + "ms");
		} catch (Exception e) {
			System.err.println("Couldn't get a RouteProvider\n" + e.getMessage());
		}
		
		if (routeProvider == null) {
			fail("No RouteProvider.");
		}
		
		int[] expectedFromOrigin = {369, 2490, 17602, 1953, 3667};
		for (int i = 0; i < destinations.size(); i++) {
//			failNotEquals("Distance mismatch", expectedFromOrigin[i], routeProvider.distances[0][i]);
			if (expectedFromOrigin[i] != routeProvider.distances[0][i]) {
				Log.e(TAG, "Distance mismatch: i=" + i + ", expected=" + expectedFromOrigin[i] + ", actual=" + routeProvider.distances[0][i]);
			}
		}
		
	}


	private List<Address> getDestinationAddresses() throws IOException {
		List<Address> destinations = new ArrayList<Address>();
        
        Address dest1 = null;
        List<Address> destBuffer = geocoder.getFromLocationName("300 West Martin Luther King Junior Boulevard, Austin, TX", 1);
        if (destBuffer != null && destBuffer.size() > 0) {
        	dest1 = destBuffer.get(0);
        	destinations.add(dest1);
        	Log.d(TAG, "dest1: " + dest1.getAddressLine(0) + " - " + dest1.getLatitude() + ", " + dest1.getLongitude());
        } else {
        	Log.e(TAG, "Couldn't reverse geocode dest1");
        }
        
        Address dest2 = null;
        destBuffer = geocoder.getFromLocationName("2902 Medical Arts Street, Austin, TX", 1);
        if (destBuffer != null && destBuffer.size() > 0) {
        	dest2 = destBuffer.get(0);
        	destinations.add(dest2);
        	Log.d(TAG, "dest2: " + dest2.getAddressLine(0) + " - " + dest2.getLatitude() + ", " + dest2.getLongitude());
        } else {
        	Log.e(TAG, "Couldn't reverse geocode dest2");
        }
        
        Address dest3 = null;
        destBuffer = geocoder.getFromLocationName("500 North IH 35, Austin, TX", 1);
        if (destBuffer != null && destBuffer.size() > 0) {
        	dest3 = destBuffer.get(0);
        	destinations.add(dest3);
        	Log.d(TAG, "dest3: " + dest3.getAddressLine(0) + " - " + dest3.getLatitude() + ", " + dest3.getLongitude());
        } else {
        	Log.e(TAG, "Couldn't reverse geocode dest3");
        }
        
        Address dest4 = null;
        destBuffer = geocoder.getFromLocationName("125 East 11th Street, Austin, TX", 1);
        if (destBuffer != null && destBuffer.size() > 0) {
        	dest4 = destBuffer.get(0);
        	destinations.add(dest4);
        	Log.d(TAG, "dest4: " + dest4.getAddressLine(0) + " - " + dest4.getLatitude() + ", " + dest4.getLongitude());
        } else {
        	Log.e(TAG, "Couldn't reverse geocode dest4");
        }
        
        Address dest5 = null;
        destBuffer = geocoder.getFromLocationName("1308 East 4th Street, Austin, TX", 1);
        if (destBuffer != null && destBuffer.size() > 0) {
        	dest5 = destBuffer.get(0);
        	destinations.add(dest5);
        	Log.d(TAG, "dest5: " + dest5.getAddressLine(0) + " - " + dest5.getLatitude() + ", " + dest5.getLongitude());
        } else {
        	Log.e(TAG, "Couldn't reverse geocode dest5");
        }
        
        return destinations;
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
