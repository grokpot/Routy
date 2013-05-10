package org.routy.model;

import android.location.Location;
import android.util.Log;


public class DeviceLocationModel {

	private String TAG = "UserLocationModel";
	private static final DeviceLocationModel singleton = new DeviceLocationModel();
	
	private Location deviceLocation;
	
	private DeviceLocationModel() {
		super();
	}
	
	public static DeviceLocationModel getSingleton() {
		return singleton;
	}

	public void setDeviceLocation(Location deviceLocation) {
		Log.v(TAG, "setting device coordinates to: " + deviceLocation.getLatitude() + ", " + deviceLocation.getLongitude());
		this.deviceLocation = deviceLocation;
	}
	
	public Location getDeviceLocation() {
		return this.deviceLocation;
	}
}
