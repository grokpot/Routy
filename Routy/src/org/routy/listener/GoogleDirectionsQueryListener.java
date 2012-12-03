package org.routy.listener;

import org.routy.model.GoogleDirections;

public abstract class GoogleDirectionsQueryListener {

	public GoogleDirectionsQueryListener() {
		super();
	}
	
	public abstract void onGotDirections(GoogleDirections directions);
	public abstract void onFailure(Throwable t);
}
