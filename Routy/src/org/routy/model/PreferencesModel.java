package org.routy.model;

public class PreferencesModel {

	private static PreferencesModel singleton = new PreferencesModel();
	
	private RouteOptimizePreference routeOptimizeMode;
	private boolean routyNoob;
//	private boolean entryNoob;
	private boolean resultsNoob;
	
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

	/*public boolean isEntryNoob() {
		return entryNoob;
	}

	public void setEntryNoob(boolean entryNoob) {
		this.entryNoob = entryNoob;
	}*/

	public boolean isResultsNoob() {
		return resultsNoob;
	}

	public void setResultsNoob(boolean resultsNoob) {
		this.resultsNoob = resultsNoob;
	}
}
