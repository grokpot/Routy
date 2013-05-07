package org.routy.model;

import android.util.Log;


public class UserLocationModel {

	private String TAG = "UserLocationModel";
	private static final UserLocationModel singleton = new UserLocationModel();
	
	private RoutyAddress userLocation;
	
	private UserLocationModel() {
		super();
	}
	
	public static UserLocationModel getSingleton() {
		return singleton;
	}

	public RoutyAddress getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(RoutyAddress userLocation) {
		Log.v(TAG, "setting user location to: " + userLocation.getAddressString());
		this.userLocation = userLocation;
	}
	
	
}
