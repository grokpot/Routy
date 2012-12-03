package org.routy.model;

import com.google.android.maps.GeoPoint;

public class GoogleDirectionsQuery {

	private GeoPoint origin;
	private GeoPoint destination;
	private boolean sensor;
	
	
	public GoogleDirectionsQuery(GeoPoint origin, GeoPoint destination, boolean sensor) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.sensor = sensor;
	}
	
	
}
