package org.routy.model;

import com.google.android.maps.GeoPoint;

/**
 * Corresponds to a "step" in the Google Directions API response.
 * 
 * @author jtran
 *
 */
public class Step {

	//private final String TAG = "Step";
	
	private GeoPoint start;
	private GeoPoint end;
	private String mode;
	private String polyline;
	
	public Step() {
		super();
	}
	
	public Step(GeoPoint start, GeoPoint end, String mode) {
		super();
		
		this.start = start;
		this.end = end;
		this.mode = mode;
	}

	public GeoPoint getStart() {
		return start;
	}

	public void setStart(GeoPoint start) {
		this.start = start;
	}

	public GeoPoint getEnd() {
		return end;
	}

	public void setEnd(GeoPoint end) {
		this.end = end;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPolyline() {
		return polyline;
	}

	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}
	
	
	
}
