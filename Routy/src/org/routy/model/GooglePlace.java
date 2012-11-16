package org.routy.model;

import java.util.Locale;

import android.location.Address;
import android.os.Bundle;

/**
 * Data container for results obtained from calls to the Google Places API.
 * 
 * @author jtran
 *
 */
public class GooglePlace {

	private String name;
	private String formattedAddress;
	private double latitude;
	private double longitude;
	
	
	public GooglePlace() {
		super();
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
	
	public String getFormattedAddress() {
		return formattedAddress;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	
	/**
	 * Builds an {@link Address} based on this Google Place.
	 * 
	 * @return	an {@link Address}
	 */
	public Address getAddress() {
		Address address = new Address(Locale.getDefault());
		address.setFeatureName(name);
		address.setLatitude(latitude);
		address.setLongitude(longitude);
		
		Bundle extras = new Bundle();
		extras.putString("formatted_address", formattedAddress);
		address.setExtras(extras);
		
		return address;
	}

}
