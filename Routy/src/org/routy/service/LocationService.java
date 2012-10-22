package org.routy.service;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public abstract class LocationService {

	private final String TAG = "LocationProvider";
	private final double accuracy;
	private final LocationManager manager;
	
	public LocationService(LocationManager locManager, double accuracy) {
		this.manager = locManager;
		this.accuracy = accuracy;
	}
	
	
	/**
	 * Callback method that is called whenever a location update has been 
	 * received.
	 * @param location
	 */
	public abstract void onLocationResult(Location location);
	
	
	/**
	 * Gets the BEST fix on the user's current location based on 
	 * coarse and fine locations provided by Android.
	 * 
	 * @throws	Exception if there are no location providers (network or GPS) enabled
	 */
	public void getCurrentLocation() throws Exception {
		Log.v(TAG, "getting current location");
		
		// Keep requesting location updates until the error is below the threshold
		boolean networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (networkEnabled || gpsEnabled) {
			if (gpsEnabled) {
	        	manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	        }
	        
	        if (networkEnabled) {
	        	manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	        }
		} else {
			Log.e(TAG, "No network providers.");
			throw new Exception("No location providers enabled.");
		}
		
        
	}
	
	
	private LocationListener listener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			manager.removeUpdates(listener);
			Log.v(TAG, "Removed location listeners");
			onLocationResult(location);
		}
	};
}
