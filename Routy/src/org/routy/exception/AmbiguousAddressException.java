package org.routy.exception;

import java.util.ArrayList;
import java.util.List;

import android.location.Address;

public class AmbiguousAddressException extends Exception {

	private List<Address> addresses;
	
	public AmbiguousAddressException() {
		super();
		
		addresses = new ArrayList<Address>();
	}
	
	
	public AmbiguousAddressException(String message) {
		super(message);
		
		addresses = new ArrayList<Address>();
	}
	
	
	public AmbiguousAddressException(List<Address> addresses) {
		super();
		
		this.addresses = addresses;		// TODO copy the list??
	}
	
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	
	public Address getFirstAddress() {
		if (addresses.size() > 0) {
			return addresses.get(0);
		} else {
			return null;
		}
	}
	
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
