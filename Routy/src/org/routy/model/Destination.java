package org.routy.model;

import android.location.Address;

/**
 * POJO required for DestinationAdapter which is used to dynamically add/remove destinations 
 * on the DestinationActivity screen.
 * 
 * @author jtran
 *
 */
public class Destination {

	private Address address;
	
	public Destination() {
		super();
	}
	
	public Destination(Address address) {
		super();
		
		this.address = address;
	}
}
