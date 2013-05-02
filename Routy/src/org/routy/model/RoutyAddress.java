package org.routy.model;

import java.util.Locale;

import android.location.Address;

public class RoutyAddress extends Address {

	private AddressStatus status;
	
	public RoutyAddress(Locale locale) {
		super(locale);
		status = AddressStatus.NOT_VALIDATED;
	}
	
	/**
	 * Converts an Android Address to a RoutyAddress and sets the status to NOT_VALIDATED
	 * @param address
	 */
	public RoutyAddress(Address address) {
		super(address.getLocale());
		
		for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
			this.setAddressLine(i, address.getAddressLine(i));
		}
		
		this.setAdminArea(address.getAdminArea());
		this.setCountryCode(address.getCountryCode());
		this.setCountryName(address.getCountryName());
		this.setExtras(address.getExtras());
		this.setFeatureName(address.getFeatureName());
		this.setLatitude(address.getLatitude());
		this.setLocality(address.getLocality());
		this.setLongitude(address.getLongitude());
		this.setPhone(address.getPhone());
		this.setPostalCode(address.getPostalCode());
		this.setPremises(address.getPremises());
		this.setSubAdminArea(address.getSubAdminArea());
		this.setSubLocality(address.getSubLocality());
		this.setSubThoroughfare(address.getSubThoroughfare());
		this.setThoroughfare(address.getThoroughfare());
		this.setUrl(address.getUrl());
		
		this.setNotValidated();
	}
	
	public AddressStatus getStatus() {
		return status;
	}

	public void setValid() {
		status = AddressStatus.VALID;
	}
	
	public void setInvalid() {
		status = AddressStatus.INVALID;
	}
	
	public void setNotValidated() {
		status = AddressStatus.NOT_VALIDATED;
	}
	
	public boolean isValid() {
		return getStatus().equals(AddressStatus.VALID);
	}
}
