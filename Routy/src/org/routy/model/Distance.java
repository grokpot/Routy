package org.routy.model;

public class Distance {

	private int distance;
	private String distanceText;
	private int duration;
	private String durationText;
	
	public Distance() {
		distance = 0;
		distanceText = null;
		duration = 0;
		durationText = null;
	}
	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getDistanceText() {
		return distanceText;
	}
	public void setDistanceText(String distanceText) {
		this.distanceText = distanceText;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getDurationText() {
		return durationText;
	}
	public void setDurationText(String durationText) {
		this.durationText = durationText;
	}
	
	
	
}
