package org.routy.model;

/**
 * A container class that holds the query parameters to the Google Places API.
 * @author jtran
 *
 */
public class GooglePlacesQuery {

	private String query;
	private Double centerLat;
	private Double centerLng;
	private int radius;
	
	public GooglePlacesQuery(String query, Double centerLat, Double centerLng) {
		this(query, centerLat, centerLng, AppConfig.G_PLACES_SEARCH_RADIUS_M);
	}
	
	public GooglePlacesQuery(String query, Double centerLat, Double centerLng, int radius) {
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
	
	public Double getCenterLatitude() {
		return centerLat;
	}
	
	public void setCenterLatitude(Double centerLat) {
		this.centerLat = centerLat;
	}
	
	public Double getCenterLongitude() {
		return centerLng;
	}
	
	public void setCenterLongitude(Double centerLng) {
		this.centerLng = centerLng;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer("Google Places Query: ");
		
		out.append("query=");
		out.append(getQuery());
		out.append(" center=");
		out.append(getCenterLatitude());
		out.append(",");
		out.append(getCenterLongitude());
		out.append(" radius=");
		out.append(getRadius());
		
		return out.toString();
	}
}
