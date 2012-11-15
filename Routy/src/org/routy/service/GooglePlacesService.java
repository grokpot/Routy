package org.routy.service;

import java.util.List;

import org.routy.model.AppProperties;
import org.routy.model.GooglePlace;

import android.location.Location;

/**
 * This class provides methods to interface with the Google Places API.
 * 
 * @author jtran
 *
 */
public class GooglePlacesService {

//	private Location center;		// Usually the user's location
//	private int radius;				// Radius around the "center" to search
	
	public GooglePlacesService(/*Location center, int radius*/) {
//		this.center = center;
//		this.radius = radius;
	}
	
	public List<GooglePlace> getPlacesForName(String name) {
		// TODO
		StringBuffer placesUrl = new StringBuffer(AppProperties.G_PLACES_API_URL);
		return null;
	}
	
	
	public List<GooglePlace> getPlacesForKeyword(String keyword) {
		// TODO
		return null;
	}
	
	
}
