package org.routy.model;

import java.util.List;

import android.location.Address;

public class RouteRequest {

	private RoutyAddress origin;
	private List<RoutyAddress> destinations;
	private boolean sensor;
	private RouteOptimizePreference preference;
	
	/**
	 * Request for a route calculation that finds the best route based on distance.
	 * @param origin
	 * @param destinations
	 * @param sensor
	 */
	public RouteRequest(RoutyAddress origin, List<RoutyAddress> destinations, boolean sensor) {
		this(origin, destinations, sensor, RouteOptimizePreference.PREFER_DISTANCE);
	}
	
	public RouteRequest(RoutyAddress origin, List<RoutyAddress> destinations, boolean sensor, RouteOptimizePreference optimizePreference) {
		super();
		this.origin = origin;
		this.destinations = destinations;
		this.sensor = sensor;
		this.preference = optimizePreference;
	}

	public RoutyAddress getOrigin() {
		return origin;
	}

	public void setOrigin(RoutyAddress origin) {
		this.origin = origin;
	}

	public List<RoutyAddress> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<RoutyAddress> destinations) {
		this.destinations = destinations;
	}
	
	public void addDestination(RoutyAddress destination) { 
		this.destinations.add(destination);
	}

	public boolean isSensor() {
		return sensor;
	}

	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}

	public RouteOptimizePreference getPreference() {
		return preference;
	}

	public void setPreference(RouteOptimizePreference preference) {
		this.preference = preference;
	}
	
	
}
