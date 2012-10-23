package org.routy.service;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public abstract class LocationService {

	private final String TAG = "LocationService";
	private final double accuracy;
	private final LocationManager manager;
	
	private long start;	// XXX temporary timer for getting location fix
	
	public LocationService(LocationManager locManager, double accuracy) {
		this.manager = locManager;
		this.accuracy = accuracy;
	}
	
	
	/**
	 * Called whenever a location update has been received.
	 * 
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
		
		// XXX temporary
		start = System.currentTimeMillis();
		
		// Keep requesting location updates until the error is below the threshold
		boolean networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if (listener == null) {
			Log.v(TAG, "Listener is null");
		}
		
		if (networkEnabled || gpsEnabled) {
			if (gpsEnabled) {
				Log.v(TAG, "gps enabled");
	        	manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	        	
//	        	manager.addGpsStatusListener(gpsStatusListener);
	        } else {
	        	Log.v(TAG, "gps disabled");
	        }
	        
	        if (networkEnabled) {
	        	Log.v(TAG, "network enabled");
	        	manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	        } else {
	        	Log.v(TAG, "network disabled");
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
			Log.v(TAG, provider + " provider status changed to " + status);
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.v(TAG, provider + " just enabled");
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.v(TAG, provider + " just disabled");
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.v(TAG, "location changed");
			if (location.hasAccuracy()) {
				Log.v(TAG, "location has accuracy: " + location.getAccuracy());
			} else {
				Log.v(TAG, "location has no accuracy.");
			}
			
			if (/*location.hasAccuracy() && */location.getAccuracy() <= accuracy) {
				Log.v(TAG, "Got a good fix");
				// XXX temporary
				Log.v(TAG, "Time to fix = " + (System.currentTimeMillis() - start) + "ms");
				stop();
				onLocationResult(location);
			} else {
				Log.v(TAG, "Location has a bad accuracy: " + location.getAccuracy());
			}
		}
	};
	
	
	private GpsStatus.Listener gpsStatusListener = new Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			Log.v(TAG, "GPS Status Changed: " + event);
		}
	};

	
	/**
	 * Releases all of this location service's holds on any system location resources.<br/>
	 * (ie. un-registers {@link LocationListener} objects from the {@link LocationManager})
	 */
	public void stop() {
		Log.v(TAG, "Stopping Location Service");
		manager.removeUpdates(listener);
	}
}
