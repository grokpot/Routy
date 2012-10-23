package org.routy.service;

import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

public class AddressService {

	private final String TAG = "AddressService";
	private final Geocoder geocoder;
	
	
	public AddressService(Geocoder geocoder) {
		this.geocoder = geocoder;
		
		if (!Geocoder.isPresent()) {
			Log.i(TAG, "Geocoder is not present...fall back on Google Maps Web API");
		} else {
			Log.i(TAG, "Geocoder is present.");
		}
	}
	
	
	/**
	 * Reverse geocodes a Location into an Address.
	 * 
	 * @param location
	 * @return				the Address for the given Location<br/>
	 * 						<code>null</code> if there is no Address for the given location
	 */
	public Address getAddressForLocation(Location location) {
		return getAddressForCoordinates(location.getLatitude(), location.getLongitude());
	}
	
	
	/**
	 * Reverse geocodes a GPS location into an Address.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return				the Address for the given GPS coordinates<br/>
	 * 						<code>null</code> if the GPS coordinates are invalid or 
	 * 						there is no Address for the given point 
	 */
	public Address getAddressForCoordinates(double latitude, double longitude) {
		try {
			List<Address> results = geocoder.getFromLocation(latitude, longitude, 1);
			if (results != null && results.size() > 0) {
				return results.get(0);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}
}
