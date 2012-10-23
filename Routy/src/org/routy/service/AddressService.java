package org.routy.service;

import java.io.IOException;
import java.util.List;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoNetworkConnectionException;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

/**
 * TODO Make the reverse-geocoding fall back on the Google Maps Web API in case there's no Geocoder backend on the device (rare...)
 * 
 * @author jtran
 *
 */
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
	 * Tries to get an {@link Address} from a location name.
	 * 
	 * @param locationName
	 * @return					the {@link Address} obtained using the given location name and <code>null</code> 
	 * 							if <code>locationName</code> is <code>null</code> or there are no results
	 * 
	 * @throws IllegalArgumentException		if the given <code>locationName</code> returns more than 1 result (is ambiguous)
	 */
	public Address getAddressForLocationName(String locationName) throws AmbiguousAddressException, NoNetworkConnectionException {
		if (locationName != null) {
			try {
				List<Address> results = geocoder.getFromLocationName(locationName, 2);
				if (results != null) {
					switch (results.size()) {
					case 0:
						return null;
					case 1:
						return results.get(0);
					case 2:
						
						throw new AmbiguousAddressException(locationName);
					}
				}
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				throw new NoNetworkConnectionException();
			}
		}
		
		return null;
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
