package org.routy.model;

import com.google.android.maps.GeoPoint;

public class GoogleDirectionsQuery {

	//private final String TAG = "GoogleDirectionsQuery";
	
	private GeoPoint origin;
	private GeoPoint destination;
	private boolean sensor;
	
	
	public GoogleDirectionsQuery(GeoPoint origin, GeoPoint destination, boolean sensor) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.sensor = sensor;
	}


	public GeoPoint getOrigin() {
		return origin;
	}


	public void setOrigin(GeoPoint origin) {
		this.origin = origin;
	}


	public GeoPoint getDestination() {
		return destination;
	}


	public void setDestination(GeoPoint destination) {
		this.destination = destination;
	}


	public boolean isSensor() {
		return sensor;
	}


	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}
	
	
}
