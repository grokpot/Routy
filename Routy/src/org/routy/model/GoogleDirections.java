package org.routy.model;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;


public class GoogleDirections {

	private List<Leg> legs;
	private List<LatLng> polypoints;
	private String overviewPolyString;
	
	public GoogleDirections() {
		super();
		
		this.legs = new ArrayList<Leg>();
	}

	public String getOverviewPolyString() {
		return overviewPolyString;
	}

	public List<LatLng> getOverviewPolypoints() {
		return polypoints;
	}

	public void setOverviewPolypoints(List<LatLng> polypoints) {
		this.polypoints = polypoints;
	}

	public void setOverviewPolyString(String overviewPolyString) {
		this.overviewPolyString = overviewPolyString;
	}

	public List<Leg> getLegs() {
		return legs;
	}

	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}
	
	public void addLeg(Leg leg) {
		this.legs.add(leg);
	}
}
