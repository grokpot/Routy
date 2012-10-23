package org.routy.service;

import java.util.ArrayList;
import java.util.List;

import org.routy.model.Distance;
import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;

import android.location.Address;

/**
 * Hang on to the route provider until the user trashes one or more 
 * locations.
 * 
 * @author jtran
 *
 */
public class RouteService {

	private final String TAG = "RouteService";
	
	private DistanceMatrixService distanceProvider;
	private Address origin;
	private List<Address> destinations;
	private RouteOptimizePreference preference;
	private boolean sensor;
	
	int[][] distances;
	
	List<List<Integer>> possibleRoutes;
	
	
	public RouteService(Address origin, List<Address> destinations, RouteOptimizePreference preference, boolean sensor) throws Exception {
		this.distanceProvider = new DistanceMatrixService();
		this.origin = origin;
		this.destinations = destinations;
		this.preference = preference;
		this.sensor = sensor;
		
		loadDistancesMatrix();
	}
	
	
	public Route getBestRoute() {
		computeAllPossibleRoutes(destinations.size());
		int bestDistance = -1;
		List<Integer> bestRoute = null;
		
		for (int r = 0; r < possibleRoutes.size(); r++) {
			List<Integer> route = possibleRoutes.get(r);
			int idx = 0;
			int distance = distances[0][route.get(idx) - 1];
			
			while (idx < (route.size() - 1)) {
				distance += distances[route.get(idx)][route.get(idx+1) - 1];
				idx++;
			}
			
			if (bestDistance == -1 || distance < bestDistance) {
				bestDistance = distance;
				bestRoute = new ArrayList<Integer>(route);
			}
		}
		
		return indicesToRoute(bestRoute, bestDistance);
	}
	
	
	private Route indicesToRoute(List<Integer> routeIndices, int distance) {
		Route route = new Route();
		route.addAddress(origin);
		
		for (int i = 0; i < routeIndices.size(); i++) {
			route.addAddress(destinations.get(routeIndices.get(i) - 1));
		}
		
		route.addDistance(distance);
		
		return route;
	}
	
	
	private void computeAllPossibleRoutes(int numDests) {
		possibleRoutes = new ArrayList<List<Integer>>();
		
		List<Integer> pool = new ArrayList<Integer>();
		for (int i = 0; i < numDests; i++) {
			pool.add(i+1);
		}
		
		permute(pool, new ArrayList<Integer>());
	}
	
	
	private void permute(List<Integer> pool, List<Integer> permutation) {
		if (pool.size() == 0) {
			possibleRoutes.add(permutation);
		} else {
			for (int i = 0; i < pool.size(); i++) {
				List<Integer> newPermutation = new ArrayList<Integer>(permutation);
				newPermutation.add(pool.get(i));
				
				List<Integer> newPool = new ArrayList<Integer>(pool);
				newPool.remove(i);
				
				permute(newPool, newPermutation);
			}
		}
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
			
			int idx = 0;
			int entered = 0;
			
			while (entered < distsFromDest.size()) {
				if (i != idx) {
					if (preference.equals(RouteOptimizePreference.PREFER_DURATION)) {
						distances[i+1][idx] = distsFromDest.get(entered).getDuration();
					} else {
						distances[i+1][idx] = distsFromDest.get(entered).getDistance();
					}
					entered++;
				}
				idx++;
				
			}
		}
	}
}
