package org.routy.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.routy.exception.RoutyException;
import org.routy.model.AppProperties;
import org.routy.model.GooglePlace;

import android.location.Location;
import android.util.Log;

/**
 * This class provides methods to interface with the Google Places API.
 * 
 * @author jtran
 *
 */
public class GooglePlacesService {

	private final String TAG = "GooglePlacesService";
	
//	private Location center;		// Usually the user's location
//	private int radius;				// Radius around the "center" to search
	
	public GooglePlacesService(/*Location center, int radius*/) {
//		this.center = center;
//		this.radius = radius;
	}
	
	public List<GooglePlace> getPlacesForName(String query, Location center, int radius) {
		// TODO
		List<GooglePlace> results = new ArrayList<GooglePlace>();
		
		// We have to have a query
		if (query == null || query.length() == 0) {
			return results;
		}
		
		// Center is not required.  If they don't give us a center, we don't need a radius either.
		if (center != null) {
			if (radius < 0 || radius > 50000) {			// The limits on the radius are set by Google in their API docs
				return results;
			}
		}
		
		// Assemble the URL to get the Google Places result(s)
		StringBuffer placesUrl = new StringBuffer(AppProperties.G_PLACES_API_URL);
		placesUrl.append("key=");
		placesUrl.append(AppProperties.G_API_KEY);
		placesUrl.append("&query=");
		placesUrl.append(query);
		
		if (center != null) {
			placesUrl.append("&location=");
			placesUrl.append(center.getLatitude());
			placesUrl.append(",");
			placesUrl.append(center.getLongitude());
			placesUrl.append("&radius=");
			placesUrl.append(radius);
		}
		
		placesUrl.append("&sensor=false");
		
		return results;
	}
	
	
	private String getXMLResponse(String url) throws RoutyException {
		try {
			URL u = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Google Places URL [" + url + "] is malformed");
			throw new RoutyException("There was an internal problem looking up place names.");
		}
		
		return null;
	}
	
	
	public List<GooglePlace> getPlacesForKeyword(String keyword) {
		// TODO
		return null;
	}
	
	
}
