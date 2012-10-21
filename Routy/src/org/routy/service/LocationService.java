package org.routy.service;

import android.location.Address;
import android.location.GpsStatus;
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
	 * @return	the user's current location as an {@link Address}
	 */
	public void getCurrentLocation() throws Exception {
		Log.v(TAG, "getting current location");
		
		// Keep requesting location updates until the error is below the threshold
//		Criteria locCriteria = new Criteria();
//        locCriteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
//        String locProvider = manager.getBestProvider(locCriteria, true);
//        
//        Log.v(TAG, "using location provider: " + locProvider + " (accuracy = " + locCriteria.getAccuracy() + " meters)");
        
//        manager.requestLocationUpdates(locProvider, 0, 0, listener);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        	manager.addGpsStatusListener(new GpsStatus.Listener() {
				
				@Override
				public void onGpsStatusChanged(int event) {
					// TODO Auto-generated method stub
					switch (event) {
					case GpsStatus.GPS_EVENT_STARTED:
						Log.v(TAG, "GPS - Event Started");
						break;
					case GpsStatus.GPS_EVENT_STOPPED:
						Log.v(TAG, "GPS - Event Stopped");
						break;
					case GpsStatus.GPS_EVENT_FIRST_FIX:
						Log.v(TAG, "GPS - Event First Fix");
						break;
					case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
						Log.v(TAG, "GPS - Event Satellite Status");
						break;
					}
				}
			});
        } else {
        	throw new Exception("GPS not enabled on device.");
        }
//        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
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
			onLocationResult(location);
			
			/*if (location.getAccuracy() <= accuracy) {
//				manager.removeUpdates(this);
				onLocationResult(location);
			} else {
				Log.v(TAG, "location changed but accuracy is not good enough - " + location.getAccuracy());
			}*/
		}
	};
}
