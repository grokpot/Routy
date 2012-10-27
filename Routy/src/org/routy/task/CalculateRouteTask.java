package org.routy.task;

import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.model.RouteRequest;
import org.routy.service.RouteService;

import android.os.AsyncTask;
import android.util.Log;

public abstract class CalculateRouteTask extends AsyncTask<RouteRequest, Void, Route> {

	private final String TAG = "CalculateRouteTask";


	public CalculateRouteTask() {

	}
	
	
	@Override
	protected Route doInBackground(RouteRequest...requests) {
		try {
//			List<Route> routes = getRoutes(requests[0].getOrigin(), requests[0].getDestinations(), null);
			if (requests.length == 0) {
				// TODO Handle this error...
			} else {
				RouteRequest request = requests[0];
				
				RouteService routeService = new RouteService(request.getOrigin(), request.getDestinations(), RouteOptimizePreference.PREFER_DURATION, false);
				Route bestRoute = routeService.getBestRoute();
				
				return bestRoute;
			}
			
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
	
	
	/*private List<Route> getRoutes(Address origin, List<Address> destinations, Route route) throws Exception {
		if (destinations.size() == 0) {
			return new ArrayList<Route>();
		}
		
		List<Distance> distances = distanceMatrixService.getDistanceMatrix(origin, destinations, false);
		
		return new ArrayList<Route>();
	}*/


	/*@Override
	protected Address doInBackground(RouteRequest... requests) {
		Log.i(TAG, "Calculating route...");
		RouteRequest request = requests[0];
		DistanceMatrixProvider provider = new DistanceMatrixProvider();
		List<Address> destinations = request.getDestinations();
		List<Address> route = new ArrayList<Address>();
		
		Log.d(TAG, "[route] size = " + route.size() + " - [destinations] size = " + destinations.size());
		
		try {
			// Calculate route by finding next consecutive nearest destination
			Address start = request.getOrigin();
			Address end = null;
			
			route.add(start);
			
			do {
				Log.d(TAG, "[route] size = " + route.size() + " - [destinations] size = " + destinations.size());
				end = provider.getClosestDestination(start, destinations, request.isSensor());
				destinations.remove(end);		// XXX remove from List might be costly...
				route.add(end);
				start = end;
			} while (destinations.size() > 1);
			
			assert (destinations.size() == 1);
			route.add(destinations.get(0));
			
			Log.i(TAG, "Calculated route: ");
			for (int i = 0; i < route.size(); i++) {
				Log.i(TAG, route.get(i).getAddressLine(0));
			}
			
			return request.getOrigin();
//			return provider.getClosestDestination(request.getOrigin(), request.getDestinations(), request.isSensor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	
	@Override
	protected void onPostExecute(Route result) {
		if (result != null) {
//			Log.d(TAG, "Closest destination address: " + result.getAddressLine(0));
			Log.d(TAG, "Best route calculated.");
			onRouteCalculated(result);
		} else {
			Log.e(TAG, "Best route returned was null.");
		}
	}
	
	
	public abstract void onRouteCalculated(Route route);

}