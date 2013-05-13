package org.routy.task;

import org.routy.exception.NoLocationProviderException;
import org.routy.listener.FindDeviceLocationListener;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.service.LocationService;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

public class FindDeviceLocationTask extends AsyncTask<Void, Void, LatLng> {

	private String TAG = "FindDeviceLocationTask";
	
	private Context context;
	private LocationManager locManager;
	private LocationService locService;
	private Location location;
	private FindDeviceLocationListener listener;
	
	public FindDeviceLocationTask(Context context, FindDeviceLocationListener listener) {
		super();
		
		this.context = context;
		this.listener = listener;
		this.location = null;
		
		locManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
		initLocationService();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		try {
			locService.getCurrentLocation();
		} catch (NoLocationProviderException e) {
			super.cancel(true);
		}
	}
	
	@Override
	protected LatLng doInBackground(Void... params) {
		while (location == null && !isCancelled()) {
			//loopidy loop loop
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(LatLng result) {
		listener.onDeviceFound(location);
	}

	private void initLocationService() {
		locService = new LocationService(locManager, AppConfig.USER_LOCATION_ACCURACY_THRESHOLD_M) {

			@Override
			public void onLocationResult(Location result) {
				Log.v(TAG, "got a device location result");
				location = result;
			}


			@Override
			public void onLocationSearchTimeout() {
				Log.v(TAG, "device locating timed out");
				//DO NOTHING
			}
		};
	}
}
