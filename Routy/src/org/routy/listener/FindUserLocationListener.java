package org.routy.listener;

import org.routy.exception.GpsNotEnabledException;

import android.location.Location;

public abstract class FindUserLocationListener {

	/** User's location has been determined */
//	public abstract void onUserLocationFound(Address userLocation);
	public abstract void onUserLocationFound(Location userLocation);
	
	/** Timeout reached while searching for user's location */
	public abstract void onTimeout(GpsNotEnabledException e);
	
	/** Some exception was encountered while searching for user's location */
	public abstract void onFailure(Throwable t);
}
