package org.routy.model;

import java.util.List;

import org.routy.service.DistanceMatrixService;

import android.location.Address;

public class RouteRequest {

	private Address origin;
	private List<Address> destinations;
	private boolean sensor;
	private int preference;
	
	public RouteRequest(Address origin, List<Address> destinations, boolean sensor) {
		this(origin, destinations, sensor, DistanceMatrixService.PREFER_DISTANCE);
	}
	
	public RouteRequest(Address origin, List<Address> destinations, boolean sensor, int preference) {
		super();
		this.origin = origin;
		this.destinations = destinations;
		this.sensor = sensor;
		this.preference = preference;
	}

	public Address getOrigin() {
		return origin;
	}

	public void setOrigin(Address origin) {
		this.origin = origin;
	}

	public List<Address> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Address> destinations) {
		this.destinations = destinations;
	}
	
	public void addDestination(Address destination) { 
		this.destinations.add(destination);
	}

	public boolean isSensor() {
		return sensor;
	}

	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}

	public int getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}
	
	
}
