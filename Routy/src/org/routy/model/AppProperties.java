package org.routy.model;

/**
 * Global properties used all over the app.  Centralized to make propagating  
 * updated data easy.
 * 
 * @author jtran
 *
 */
public class AppProperties {

	/** The minimum accuracy required to achieve a "good" location fix in meters. */
	public static final double LOCATION_ACCURACY_THRESHOLD_M = 50;
	
	/** The amount of time before the last known location provided by Android is deemed "stale" */
	public static final double LOCATION_EXPIRE_TIME_MS = 0;//300000;
	
	/** The amount of time Routy should spend searching for the user's location in milliseconds */
	public static final long LOCATION_FETCH_TIMEOUT_MS = 10000;
	
	/** The base URL for accessing the Google Distance Matrix Web API */
	public static final String G_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	
	/** The base URL for accessing the <a href="https://developers.google.com/maps/documentation/geocoding/">Google Geocoding Web API</a> */
	public static final String G_GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?";

	public static final int NUM_MAX_DESTINATIONS = 5;
	
	
}
