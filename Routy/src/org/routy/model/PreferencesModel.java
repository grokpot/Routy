package org.routy.model;

public class PreferencesModel {

	private static PreferencesModel singleton = new PreferencesModel();
	
	private RouteOptimizePreference routeOptimizeMode;
	
	private boolean routyNoob;
	private boolean resultsNoob;
	
	private boolean soundsOn;
	
	private PreferencesModel() {
		super();
	}
	
	public static PreferencesModel getSingleton() {
		return singleton;
	}

	public RouteOptimizePreference getRouteOptimizeMode() {
		return routeOptimizeMode;
	}

	public void setRouteOptimizeMode(RouteOptimizePreference routeOptimizeMode) {
		this.routeOptimizeMode = routeOptimizeMode;
	}

	public boolean isRoutyNoob() {
		return routyNoob;
	}

	public void setRoutyNoob(boolean routyNoob) {
		this.routyNoob = routyNoob;
	}

	public boolean isResultsNoob() {
		return resultsNoob;
	}

	public void setResultsNoob(boolean resultsNoob) {
		this.resultsNoob = resultsNoob;
	}

	public boolean isSoundsOn() {
		return soundsOn;
	}

	public void setSoundsOn(boolean soundsOn) {
		this.soundsOn = soundsOn;
	}
}
