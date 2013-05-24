package org.routy.model;

import java.util.List;

public class GoogleDirectionsQuery {

	//private final String TAG = "GoogleDirectionsQuery";
	
	private List<RoutyAddress> addresses;
//	private GeoPoint origin;
//	private GeoPoint destination;
	private boolean sensor;
	
	
	public GoogleDirectionsQuery(List<RoutyAddress> addresses, boolean sensor) {
		super();
		this.addresses = addresses;
//		this.origin = origin;
//		this.destination = destination;
		this.sensor = sensor;
	}


	public List<RoutyAddress> getAddresses() {
		return addresses;
	}


	public void setAddresses(List<RoutyAddress> addresses) {
		this.addresses = addresses;
	}


/*	public GeoPoint getOrigin() {
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
	}*/


	public boolean isSensor() {
		return sensor;
	}


	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}
	
	
}
