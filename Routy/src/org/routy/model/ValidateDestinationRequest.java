package org.routy.model;


public class ValidateDestinationRequest {

	private String query;
	private double centerLat;
	private double centerLng;
	private int radius;
	
	public ValidateDestinationRequest(String query, double centerLat, double centerLng, int radius) {
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
