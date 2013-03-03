package org.routy.model;

import java.util.ArrayList;
import java.util.List;

import android.location.Address;

public class Route {
	
	private List<Address> addresses;
	private int distance;
	
	public Route() {
		this.addresses = new ArrayList<Address>();
		this.distance = 0;
	}
	
	public Route(List<Address> addresses2, int distance) {
		this.addresses = addresses2;
		this.distance = distance;
	}
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	public void addAddress(Address address) {
		this.addresses.add(address);
	}
	
	public List<Address> getAddresses(Address address) {
		return this.addresses;
	}
	
	public int getTotalDistance() {
		return distance;
	}
	
	public void addDistance(int distance) {
		this.distance += distance;
	}
}
