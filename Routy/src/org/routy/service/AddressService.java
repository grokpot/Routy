package org.routy.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.GeocoderAPIException;
import org.routy.exception.NoInternetConnectionException;
import org.routy.model.AppProperties;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

/**
 * Uses the device's Geocoder backend (or Google Geocoding API -- Use Google Geocoding API: https://developers.google.com/maps/documentation/geocoding/ 
 * if one is not available on the device) to get Address information.
 * 
 * @author jtran
 *
 */
public class AddressService {

	private final String TAG = "AddressService";
	private final Geocoder geocoder;
	private boolean sensor;
	
	
	public AddressService(Geocoder geocoder, boolean sensor) {
		this.geocoder = geocoder;
		this.sensor = sensor;
		
		if (!Geocoder.isPresent()) {
			Log.i(TAG, "Geocoder is not present...fall back on Google Maps Web API");
		} else {
			Log.i(TAG, "Geocoder is present.");
		}
	}
	
	
	/**
	 * Tries to get an {@link Address} from a location string.
	 * 
	 * @param locationName
	 * @return					the {@link Address} obtained using the given location name and <code>null</code> 
	 * 							if <code>locationName</code> is <code>null</code> or there are no results
	 * 
	 * @throws IllegalArgumentException		if the given <code>locationName</code> returns more than 1 result (is ambiguous)
	 */
	public Address getAddressForLocationString(String locationName) throws AmbiguousAddressException, NoInternetConnectionException {
		if (locationName != null) {
			try {
				if (!Geocoder.isPresent()) {
					return getAddressViaWeb(locationName);
				} else {
					return getAddressViaGeocoder(locationName);
				}
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				throw new NoInternetConnectionException();
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
	 * @throws AmbiguousAddressException 
	 * @throws NoInternetConnectionException 
	 */
	public Address getAddressForLocation(Location location) throws AmbiguousAddressException, NoInternetConnectionException {
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
	 * @throws NoInternetConnectionException 
	 */
	public Address getAddressForCoordinates(double latitude, double longitude) throws AmbiguousAddressException, NoInternetConnectionException {
		try {
			if (!Geocoder.isPresent()) {
				return getAddressViaWeb(latitude, longitude);
			} else {
				return getAddressViaGeocoder(latitude, longitude);
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			throw new NoInternetConnectionException();
		}

		return null;
	}
	
	
	/**
	 * Gets an {@link Address} object from a given location string.  This can be used to verify user input address strings.
	 * 
	 * TODO Implement the Google Places API here in case the location string is a place name, not an address
	 * @param locationName
	 * @return
	 * @throws IOException
	 * @throws AmbiguousAddressException
	 */
	Address getAddressViaGeocoder(String locationName) throws IOException, AmbiguousAddressException {
		Log.v(TAG, "Using Geocoder backend");
		
		List<Address> results = geocoder.getFromLocationName(locationName, 2);
		
		if (results != null && results.size() > 0) {
			if (results.size() == 1) {
				return results.get(0);
			} else {
				throw new AmbiguousAddressException(results);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Reverse geocodes the given GPS coordinates into an {@link Address} using the Geocoder backend on the device
	 * .
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws IOException
	 * @throws AmbiguousAddressException
	 */
	Address getAddressViaGeocoder(double latitude, double longitude) throws IOException, AmbiguousAddressException {
		Log.v(TAG, "Using Geocoder backend");
		
		List<Address> results = geocoder.getFromLocation(latitude, longitude, 2);
		
		if (results != null && results.size() > 0) {
			if (results.size() == 1) {
				return results.get(0);
			} else {
				throw new AmbiguousAddressException(results);
			}
		}
		
		return null;
	}
	
	
	/**
	 * Makes a call to the Google Geocoding API to get an address for the given location name.
	 * 
	 * @param locationName
	 * @return
	 */
	Address getAddressViaWeb(String locationName) throws NoInternetConnectionException {		// TODO make this throw an exception if it gets more than 1 address
		Log.v(TAG, "Using Geocoding Web API");
		if (locationName != null && locationName.length() > 0) {
			StringBuilder geoUrl = new StringBuilder(AppProperties.G_GEOCODING_API_URL);
			geoUrl.append("address=");
			geoUrl.append(locationName.replaceAll(" ", "+"));
			geoUrl.append("&sensor=");
			geoUrl.append(sensor?"true":"false");
			
			Address address = getAddressForURL(geoUrl.toString());
			return address;
		}
		
		return null;
	}
	
	
	/**
	 * Makes a call to the Google Geocoding API to get an address for the given latitude/longitude coordinates.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	Address getAddressViaWeb(double latitude, double longitude) throws NoInternetConnectionException {	// TODO make this throw an exception if it gets more than 1 address
		Log.v(TAG, "Using Geocoding Web API");
		StringBuilder geoUrl = new StringBuilder(AppProperties.G_GEOCODING_API_URL);
		geoUrl.append("latlng=");
		geoUrl.append(latitude);
		geoUrl.append(",");
		geoUrl.append(longitude);
		geoUrl.append("&sensor=");
		geoUrl.append(sensor?"true":"false");
		
		return getAddressForURL(geoUrl.toString());
	}
	
	
	/**
	 * Gets an {@link Address} object by making a web API call to the given URL and parsing the response.<br/>
	 * (Used internally)
	 * 
	 * @param url
	 * @return
	 * @throws IllegalArgumentException		if url is null or empty
	 */
	Address getAddressForURL(String url) throws IllegalArgumentException, NoInternetConnectionException {
		// TODO Get this as an XML response so we can more thoroughly fill in the Address object (eg. different address lines, locality, etc)
		if (url != null && url.length() > 0) {
			try {
				// Get the JSON response from the Geocoding API
				Log.v(TAG, "Geocoding API URL: " + url);
				String jsonResp = InternetService.getJSONResponse(url);

				// Parse the response into an Address
				if (jsonResp != null && jsonResp.length() > 0) {
					Address result = parseJSONResponse(jsonResp);
					Log.v(TAG, "Address Line 0: " + result.getAddressLine(0));
					return result;
				}
				
			} catch (GeocoderAPIException e) {
				Log.e(TAG, e.getMessage());
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			
			return null;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Parses a JSON response from the <a href="https://developers.google.com/maps/documentation/geocoding/">Google Geocoding API</a>.<br/>
	 * (Used internally)
	 *  
	 * @param jsonResp
	 * @return
	 * @throws JSONException
	 * @throws GeocoderAPIException
	 */
	Address parseJSONResponse(String jsonResp) throws JSONException, GeocoderAPIException/*, AmbiguousAddressException*/ {
		JSONObject response = (JSONObject) new JSONTokener(jsonResp.toString()).nextValue();
		String status = response.getString("status");
		
		if (status.equalsIgnoreCase("ok")) {
			JSONArray results = response.getJSONArray("results");

			// TODO Handle ambiguous addresses that get multiple results
			Address address = new Address(Locale.getDefault());
			
			JSONObject result = results.getJSONObject(0);
			JSONObject geometry = result.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");
			
			address.setAddressLine(0, result.getString("formatted_address"));
			address.setLatitude(location.getDouble("lat"));
			address.setLongitude(location.getDouble("lng"));
			
			return address;
		} else if (status.equalsIgnoreCase("zero_results")){
			return null;
		} else {
			throw new GeocoderAPIException("Geocoding API failed with status=" + status);
		}
	}
}
