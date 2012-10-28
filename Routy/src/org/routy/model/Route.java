package org.routy.model;

import java.util.ArrayList;
import java.util.List;

import android.location.Address;

public class Route {
	
	private ArrayList<Address> addresses;
	private int distance;
	
	public Route() {
		this.addresses = new ArrayList<Address>();
		this.distance = 0;
	}
	
	public Route(ArrayList<Address> addresses, int distance) {
		this.addresses = addresses;
		this.distance = distance;
	}
	
	public ArrayList<Address> getAddresses() {
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
