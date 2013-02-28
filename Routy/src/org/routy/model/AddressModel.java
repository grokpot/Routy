package org.routy.model;

import java.util.List;

import android.location.Address;

public class AddressModel {

	private static final AddressModel addressModel = new AddressModel();
	
	private Address origin;
	private List<Address> destinations;
	
	private AddressModel() {
		super();
	}
	
	public static AddressModel getSingleton() {
		return addressModel;
	}

	public Address getOrigin() {
		return origin;
	}

	public void setOrigin(Address origin) {
		this.origin = origin;
	}
	
	public void addDestination(Address destination) {
		destinations.add(destination);
	}

	public List<Address> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Address> destinations) {
		this.destinations = destinations;
	}
	
	
}
