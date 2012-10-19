package org.routy.provider;

import java.util.ArrayList;
import java.util.List;

import org.routy.model.Distance;
import org.routy.model.Route;

import android.location.Address;

/**
 * Hang on to the route provider until the user trashes one or more 
 * locations.
 * 
 * @author jtran
 *
 */
public class RouteProvider {

	private DistanceMatrixProvider distanceProvider;
	private Address origin;
	private List<Address> destinations;
	private RouteOptimizePreference preference;
	private boolean sensor;
	
	int[][] distances;
	
	
	public RouteProvider(Address origin, List<Address> destinations, RouteOptimizePreference preference, boolean sensor) throws Exception {
		this.distanceProvider = new DistanceMatrixProvider();
		this.origin = origin;
		this.destinations = destinations;
		this.preference = preference;
		this.sensor = sensor;
		
		loadDistancesMatrix();
	}
	
	
	public Route getShortestRoute() {
		Route shortest = null;
		
		for (int r = 0; r < destinations.size(); r++) {
			
		}
		
		return null;
	}
	
	
	
	
	private void loadDistancesMatrix() throws Exception {
		int rows = destinations.size() + 1;
		int cols = destinations.size();
		
		distances = new int[rows][cols];
		
		// Origin to each destination
		List<Distance> distsFromOrigin = distanceProvider.getDistanceMatrix(origin, destinations, sensor);
		for (int i = 0; i < distsFromOrigin.size(); i++) {
			if (preference.equals(RouteOptimizePreference.PREFER_DURATION)) {
				distances[0][i] = distsFromOrigin.get(i).getDuration();
			} else {
				distances[0][i] = distsFromOrigin.get(i).getDistance();
			}
		}
		
		// Each destination to others
		List<Distance> distsFromDest = null;
		for (int i = 0; i < destinations.size(); i++) {
			List<Address> otherDests = new ArrayList<Address>(destinations);
			otherDests.remove(i);
			
			distsFromDest = distanceProvider.getDistanceMatrix(destinations.get(i), otherDests, sensor);
			
			for (int j = 0; j < distsFromDest.size(); j++) {
				if (i != j) {
					if (preference.equals(RouteOptimizePreference.PREFER_DURATION)) {
						distances[i+1][j] = distsFromDest.get(j).getDuration();
					} else {
						distances[i+1][j] = distsFromDest.get(j).getDistance();
					}
				}
			}
		}
	}
}
