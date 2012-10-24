package org.routy.service;

import java.util.Date;
import java.util.List;

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
		
		// Keep requesting location updates until the error is below the threshold
		boolean networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		Location lastKnownLoc = getLastKnownLocation();
		if (lastKnownLoc != null) {
			Log.v(TAG, "Last known location was good...using it.");
			onLocationResult(lastKnownLoc);
		}
		
		if (listener == null) {
			Log.v(TAG, "Listener is null");
		}
		
		if (networkEnabled || gpsEnabled) {
			if (gpsEnabled) {
				Log.v(TAG, "gps enabled");
	        	manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
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
	
	
	private Location getLastKnownLocation() {
		long minTimeMs = 300000;
		long bestTimeMs = 0;
		
		Location lastKnownLocation = null;
		
		List<String> providers = manager.getProviders(true);
		for (String provider : providers) {
			lastKnownLocation = manager.getLastKnownLocation(provider);
			
			// Last loc update is within our expiration time and is newer than any we've found before
			if (((new Date()).getTime() - lastKnownLocation.getTime()) <= minTimeMs && lastKnownLocation.getTime() > bestTimeMs) {
				
				// Last loc has an accuracy within our threshold
				if (lastKnownLocation.hasAccuracy() && lastKnownLocation.getAccuracy() <= accuracy) {
					bestTimeMs = lastKnownLocation.getTime();
				}
			}
		}
		
		return lastKnownLocation;
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
			Log.v(TAG, "location changed via " + location.getProvider());
			if (location.hasAccuracy()) {
				Log.v(TAG, "location has accuracy: " + location.getAccuracy());
			} else {
				Log.v(TAG, "location has no accuracy.");
			}
			
			if (/*location.hasAccuracy() && */location.getAccuracy() <= accuracy) {
				Log.v(TAG, "Got a good fix");
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
