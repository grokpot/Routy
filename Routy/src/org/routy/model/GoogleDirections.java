package org.routy.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;


public class GoogleDirections {

//	private List<Step> steps;
	private List<LatLng> polypoints;
	private String overviewPolyString;
	
	public GoogleDirections() {
		super();
	}

	/*public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}*/
	
	public String getOverviewPolyString() {
		return overviewPolyString;
	}

	public List<LatLng> getPolypoints() {
		return polypoints;
	}

	public void setPolypoints(List<LatLng> polypoints) {
		this.polypoints = polypoints;
	}

	public void setOverviewPolyString(String overviewPolyString) {
		this.overviewPolyString = overviewPolyString;
	}
	
	
}
