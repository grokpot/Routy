package org.routy.model;

import java.util.ArrayList;
import java.util.List;

public class Route {
	
	private List<RoutyAddress> addresses;
	private int distance;
	
	public Route() {
		this.addresses = new ArrayList<RoutyAddress>();
		this.distance = 0;
	}
	
	public Route(List<RoutyAddress> addresses2, int distance) {
		this.addresses = addresses2;
		this.distance = distance;
	}
	
	public List<RoutyAddress> getAddresses() {
		return addresses;
	}
	
	public void addAddress(RoutyAddress address) {
		this.addresses.add(address);
	}
	
	/*public List<RoutyAddress> getAddresses(RoutyAddress address) {
		return this.addresses;
	}*/
	
	public int getTotalDistance() {
		return distance;
	}
	
	public void addDistance(int distance) {
		this.distance += distance;
	}
}
