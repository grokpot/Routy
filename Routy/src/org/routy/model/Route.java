package org.routy.model;

import java.util.List;

import android.location.Address;

public class Route {
	
	private List<Address> addresses;
	private int distance;
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	public void addAddress(Address address) {
		addresses.add(address);
	}
	
	public int getTotalDistance() {
		return distance;
	}
	
	public void addDistance(int distance) {
		this.distance += distance;
	}
}
