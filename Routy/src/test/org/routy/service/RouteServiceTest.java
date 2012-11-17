package org.routy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;

import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;
import android.util.Log;

public class RouteServiceTest extends AndroidTestCase {

	private final String TAG = "RouteProviderTest";
	private Geocoder geocoder;
	private List<Address> destinations;
	private RouteService routeProvider;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		geocoder = new Geocoder(getContext(), Locale.getDefault());
		
		// Get some destinations to work with
		/*try {
			destinations = getDestinationAddresses();
		} catch (IOException e) {
			System.err.println("Couldn't get destination addresses\n" + e.getMessage());
		}
		
		if (destinations == null) {
			fail("No destinations.");
		}*/
	}
	
	
	/*public void testDistanceMatrix() {
		int[] expectedFromOrigin = {369, 2490, 17602, 1953, 3667};
		for (int i = 0; i < destinations.size(); i++) {
			if (expectedFromOrigin[i] != routeProvider.distances[0][i]) {
				Log.e(TAG, "Distance mismatch: i=" + i + ", expected=" + expectedFromOrigin[i] + ", actual=" + routeProvider.distances[0][i]);
			}
		}
		
		System.out.println("Distance matrix: ");
		for (int i = 0; i < destinations.size() + 1; i++) {
			printDistances(routeProvider.distances[i]);
		}
	}*/
	
	
	/* (non-Javadoc)
	 * Timed test -- 
	 * 1. Geocode destinations
	 * 2. Geocode origin
	 * 3. Create a RouteProvider (that initializes a Distance Matrix)
	 * 4. Compute the shortest route
	 */
	public void testGetShortestRoute() {
		long start = System.currentTimeMillis();
		try {
			destinations = getDestinationAddresses();
		} catch (IOException e) {
			System.err.println("Couldn't get destination addresses\n" + e.getMessage());
		}
		
		if (destinations == null) {
			fail("No destinations.");
		}
		
		routeProvider = getRouteProvider(destinations);
		
		if (routeProvider == null) {
			fail("No RouteProvider.");
		}
		
		Route shortestRoute = routeProvider.getBestRoute();
		System.out.println("Computed shortest route with " + destinations.size() + " destinations in " + (System.currentTimeMillis() - start) + "ms");
		
		System.out.println("Shortest route: ");
		for (int i = 0; i < shortestRoute.getAddresses().size(); i++) {
			System.out.println(shortestRoute.getAddresses().get(i).getAddressLine(0) + " - " + shortestRoute.getAddresses().get(i).getLatitude() + ", " + shortestRoute.getAddresses().get(i).getLongitude());
		}
		System.out.println("Total distance: " + shortestRoute.getTotalDistance() + " km");
	}
	
	
	private RouteService getRouteProvider(List<Address> destinations) {
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
		
		RouteService routeProvider = null;
		try {
			long start = System.currentTimeMillis();
			routeProvider = new RouteService(origin, destinations, RouteOptimizePreference.PREFER_DISTANCE, false);
			Log.i(TAG, "Got a RouteProvider (with distance matrix) in " + (System.currentTimeMillis() - start) + "ms");
		} catch (Exception e) {
			System.err.println("Couldn't get a RouteProvider\n" + e.getMessage());
		}
		
		if (routeProvider == null) {
			fail("No RouteProvider.");
		}
		
		return routeProvider;
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
	
	
	private void printDistances(int[] distances) {
		StringBuilder sb = new StringBuilder("[");
		
		for (int i = 0; i < distances.length - 1; i++) {
			sb.append(distances[i]);
			sb.append(", ");
		}
		
		sb.append(distances[distances.length - 1]);
		sb.append("]");
		
		System.out.println(sb.toString());
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
