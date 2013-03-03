package org.routy.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.routy.Util;
import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.GeocoderAPIException;
import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;
import org.routy.model.AppProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
	 * @throws AmbiguousAddressException
	 * @throws RoutyException 
	 * @throws IOException 
	 */
	public Address getAddressForLocationString(String locationName) throws AmbiguousAddressException, RoutyException, IOException {
		if (locationName != null) {
			if (!Geocoder.isPresent()) {
				return getAddressViaWeb(locationName);
			} else {
				return getAddressViaGeocoder(locationName);
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
	 * @throws IOException 
	 * @throws RoutyException 
	 */
	public Address getAddressForLocation(Location location) throws RoutyException, IOException, AmbiguousAddressException {
		Log.v(TAG, "getting address for a location");
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
	 * @throws AmbiguousAddressException 
	 * @throws RoutyException 
	 * @throws IOException 
	 */
	public Address getAddressForCoordinates(double latitude, double longitude) throws AmbiguousAddressException, RoutyException, IOException {
		if (!Geocoder.isPresent()) {
			Log.v(TAG, String.format("using web API to get address for location: %f, %f", latitude, longitude));
			return getAddressViaWeb(latitude, longitude);
		} else {
			return getAddressViaGeocoder(latitude, longitude);
		}
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
		Log.v(TAG, "Getting Address for locationName=" + locationName + " via Geocoder");
		List<Address> results = geocoder.getFromLocationName(locationName, 2);
		
		if (results != null && results.size() > 0) {
			if (results.size() == 1) {
				Address result = results.get(0);
				
				Bundle extras = new Bundle();
				StringBuffer formattedAddress = new StringBuffer();
				for (int i = 0; i < result.getMaxAddressLineIndex(); i++) {
					formattedAddress.append(result.getAddressLine(i));
					formattedAddress.append(", ");
				}
				formattedAddress.append(result.getAddressLine(result.getMaxAddressLineIndex()));
				extras.putString("formatted_address", formattedAddress.toString());
				result.setExtras(extras);
				
				return result;
//				return results.get(0);
			} else {
				for (int j = 0; j < results.size(); j++) {
					Log.v(TAG, "Resulting Address " + j);
					for (int i = 0; i < results.get(j).getMaxAddressLineIndex(); i++) {
						Log.v(TAG, results.get(j).getAddressLine(i));
					}
				}
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
		Log.v(TAG, String.format("using geocoder to get address for location: %f, %f", latitude, longitude));
		int tries = 0;
		
		// Since this process actually involves the Google server, there can be issues on their end out of our control.  We'll try 10 times if something goes wrong.
		while (tries < 10) {
			try {
				List<Address> results = geocoder.getFromLocation(latitude, longitude, 2);
				if (results != null && results.size() > 0) {
					if (results.size() == 1) {
						Address result = results.get(0);
						Util.formatAddress(result);
						Log.v(TAG, "got an address for the location using geocoder");
						return result;
					} else {
						for (Address a : results) {
							Util.formatAddress(a);
						}
						throw new AmbiguousAddressException(results);
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException calling getFromLocation -- trying again");
				tries++;
			}
		}
		
		Log.e(TAG, "couldn't get address using geocoder");
		return null;
	}


	/*private void formatAddress(Address result) {
		Bundle extras = new Bundle();
		StringBuffer formattedAddress = new StringBuffer();
		for (int i = 0; i < result.getMaxAddressLineIndex(); i++) {
			formattedAddress.append(result.getAddressLine(i));
			formattedAddress.append(", ");
		}
		
		formattedAddress.append(result.getAddressLine(result.getMaxAddressLineIndex()));
		
		if (result.getMaxAddressLineIndex() > -1) {
			formattedAddress.append(result.getAddressLine(0));
		}
		
		formattedAddress.append(result.getLocality() == null ? "" : result.getLocality());
		formattedAddress.append(result.getAdminArea() == null ? "" : result.getAdminArea());
		
		if (formattedAddress.length() == 0) {
			formattedAddress.append(result.getLatitude());
			formattedAddress.append(", ");
			formattedAddress.append(result.getLongitude());
		}
		
		extras.putString("formatted_address", formattedAddress.toString());
		result.setExtras(extras);
		
		if (result.getExtras() == null) {
			Log.e(TAG, "result extras is null");
		}
	}*/
	
	
	/**
	 * Makes a call to the Google Geocoding API to get an address for the given location name.
	 * 
	 * @param locationName
	 * @return
	 * @throws RoutyException 
	 * @throws IOException 
	 */
	Address getAddressViaWeb(String locationName) throws IOException, RoutyException {		// TODO make this throw an exception if it gets more than 1 address
		Log.v(TAG, "Getting Address for locationName=" + locationName + " via Web API");
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
	 * @throws RoutyException 
	 * @throws IOException 
	 */
	Address getAddressViaWeb(double latitude, double longitude) throws IOException, RoutyException {	// TODO make this throw an exception if it gets more than 1 address
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
	 * @throws RoutyException 
	 */
	Address getAddressForURL(String url) throws IOException, RoutyException {
		if (url != null && url.length() > 0) {
			try {
				// Get the JSON response from the Geocoding API
				Log.v(TAG, "Geocoding API URL: " + url);
				
				String xmlResponse = InternetService.getStringResponse(url);
				if (xmlResponse != null && xmlResponse.length() > 0) {
					Address result = parseXMLResponse(xmlResponse);
					Util.formatAddress(result);
					return result;
				}
				
			} catch (GeocoderAPIException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
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
	Address parseJSONResponse(String jsonResp) throws JSONException, GeocoderAPIException {
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

	
	Address parseXMLResponse(String xmlResponse) throws GeocoderAPIException, RoutyException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expr;
		
		InputSource inputSource = new InputSource(new StringReader(xmlResponse));
		
		try {
			expr = "/";
			Node root = (Node) xpath.evaluate(expr, inputSource, XPathConstants.NODE);

			
			// GET THE RESPONSE STATUS
			expr = "/GeocodeResponse/status";
//			String status = (String) xpath.evaluate(expr, inputSource, XPathConstants.STRING);
			String status = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
			Log.v(TAG, "status=" + status);
			if (status == null || status.length() == 0) {
				Log.e(TAG, "Failed parsing Geocoder API response status.");
				throw new RoutyException("There was a problem understanding an address.");
			} else if (!status.equalsIgnoreCase("ok")) {
				Log.e(TAG, "Geocoder API response status not ok -- status=" + status);
				throw new RoutyException("There was a problem understanding an address.");
			} else { 
				// TODO status is ok - parse the XML into an Address object
				int lineNumber = 0;
				Address result = new Address(Locale.getDefault());
				
				expr = "/GeocodeResponse/result";
				NodeList results = (NodeList) xpath.evaluate(expr, root, XPathConstants.NODESET);
				if (results.getLength() == 0) {
					return null;
				}
				
				// GET THE PLACE NAME (ESTABLISHMENT) IF THERE IS ONE (EX. "UNIVERSITY OF TEXAS")
				expr = "/GeocodeResponse/result[1]/address_component[type=\"establishment\"]/long_name";
				String establishment = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				result.setFeatureName(establishment);
//				Log.v(TAG, "establishment=" + establishment);
				
				// GET THE STREET NUMBER AND NAME
				expr = "/GeocodeResponse/result[1]/address_component[type=\"street_number\"]/long_name";
				String streetNumber = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
//				Log.v(TAG, "street number=" + streetNumber);
				
				expr = "/GeocodeResponse/result[1]/address_component[type=\"route\"]/long_name";
				String streetName = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
//				Log.v(TAG, "street name=" + streetName);
				
				result.setAddressLine(lineNumber, (streetNumber==null?"":streetNumber) + (streetName==null?"":(" " + streetName)));
				lineNumber++;
				
				
				StringBuffer line1 = new StringBuffer();
					
				// GET THE CITY, STATE AND ZIPCODE
				expr = "/GeocodeResponse/result[1]/address_component[type=\"locality\"]/long_name";
				String cityName = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (cityName != null) {
					result.setLocality(cityName);
					
					line1.append(cityName);
					line1.append(", ");
				}
				
				expr = "/GeocodeResponse/result[1]/address_component[type=\"administrative_area_level_1\"]/long_name";
				String stateName = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (stateName != null) {
					result.setAdminArea(stateName);
				}
				
				expr = "/GeocodeResponse/result[1]/address_component[type=\"administrative_area_level_1\"]/short_name";
				String stateAbbr = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (stateAbbr != null) {
					result.setSubAdminArea(stateAbbr);
					
					line1.append(stateAbbr);
					line1.append(", ");
				}
				
				expr = "/GeocodeResponse/result[1]/address_component[type=\"postal_code\"]/long_name";
				String zipcode = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (zipcode != null) {
					result.setAdminArea(zipcode);
					
					line1.append(zipcode);
				}
				
				result.setAddressLine(lineNumber, line1.toString());
				lineNumber++;
				
				
				// GET THE COORDINATE INFORMATION
				expr = "/GeocodeResponse/result[1]/geometry/location/lat";
				String latitude = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (latitude != null && latitude.length() > 0) {
					result.setLatitude(Double.parseDouble(latitude));
				}
				
				expr = "/GeocodeResponse/result[1]/geometry/location/lng";
				String longitude = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				if (longitude != null && longitude.length() > 0) {
					result.setLongitude(Double.parseDouble(longitude));
				}
				
				
				// GET THE FORMATTED ADDRESS
				expr = "/GeocoderResponse/result[1]/formatted_address";
				String formattedAddress = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
				Bundle extras = new Bundle();
				extras.putString("formatted_address", formattedAddress);
				result.setExtras(extras);
				
				Log.v(TAG, "Done parsing XML.");
				return result;
			}
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
