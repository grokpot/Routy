package org.routy.exception;

import java.util.ArrayList;
import java.util.List;

import org.routy.model.RoutyAddress;

import android.location.Address;

@SuppressWarnings("serial")
public class AmbiguousAddressException extends Exception {

	private List<? extends Address> addresses;
	
	public AmbiguousAddressException() {
		super();
		
		addresses = new ArrayList<RoutyAddress>();
	}
	
	
	public AmbiguousAddressException(String message) {
		super(message);
		
		addresses = new ArrayList<RoutyAddress>();
	}
	
	
	public AmbiguousAddressException(List<? extends Address> addresses) {
		super();
		
		this.addresses = addresses;		// TODO copy the list??
	}
	
	
	public List<? extends Address> getAddresses() {
		return addresses;
	}
	
	
	public RoutyAddress getFirstAddress() {
		if (addresses.size() > 0) {
			if (addresses.get(0) instanceof RoutyAddress) {
				return (RoutyAddress) addresses.get(0);
			} else {
				return new RoutyAddress(addresses.get(0));
			}
		} else {
			return null;
		}
	}
	
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
