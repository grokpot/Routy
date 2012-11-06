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
	private String locationString;
	
	public Destination() {
		super();
		
		this.address = null;
		this.locationString = "";
	}
	
	public Destination(Address address) {
		super();
		
		this.address = address;
		this.locationString = "";
	}
	
	public Destination(String locationString) {
		super();
		
		this.address = null;
		this.locationString = locationString;
	}
	
	
	public Address getAddress() {
		return this.address;
	}
	
	public String getLocationString() {
		return this.locationString;
	}
	
	public boolean isValid() {
		return this.address != null && (this.locationString == null || this.locationString.length() == 0);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Destination)) {
			return false;
		}
		
		Destination other = (Destination) o;
		
		// Compare addresses first, then compare location strings.
		if (this.address != null && other.getAddress() != null) {
			if ((this.address.getLatitude() != other.getAddress().getLatitude()) || (this.address.getLongitude() != other.getAddress().getLongitude())) {
				return false;
			} else {
				return true;
			}
		} else if (this.address == null && other.getAddress() == null) {
			if (this.locationString != null) {
				return this.locationString.equals(other.getLocationString());
			} else if (other.getLocationString() == null) {
				return true;
			}
		}
		
		return false;
	}
}
