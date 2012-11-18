package org.routy.model;

/**
 * A container class that holds the query parameters to the Google Places API.
 * @author jtran
 *
 */
public class GooglePlacesQuery {

	private String query;
	private double centerLat;
	private double centerLng;
	private int radius;
	
	public GooglePlacesQuery(String query, double centerLat, double centerLng) {
		this(query, centerLat, centerLng, AppProperties.G_PLACES_SEARCH_RADIUS_M);
	}
	
	public GooglePlacesQuery(String query, double centerLat, double centerLng, int radius) {
		super();
		
		this.query = query;
		this.centerLat = centerLat;
		this.centerLng = centerLng;
		this.radius = radius;
	}

	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public double getCenterLatitude() {
		return centerLat;
	}
	
	public void setCenterLatitude(double centerLat) {
		this.centerLat = centerLat;
	}
	
	public double getCenterLongitude() {
		return centerLng;
	}
	
	public void setCenterLongitude(double centerLng) {
		this.centerLng = centerLng;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
}
