package org.routy.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.routy.model.Distance;

import android.location.Address;
import android.util.Log;

public class DistanceMatrixService {

	private final String TAG = "DistanceMatrixProvider";
	
	public static final int PREFER_DISTANCE = 0;
	public static final int PREFER_DURATION = 1;
	
	private final String G_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	
	
	/**
	 * Default method for getting the closest destination in terms of distance.
	 * @param origin
	 * @param destinations
	 * @param sensor
	 * @return
	 * @throws Exception
	 */
	public Address getClosestDestination(final Address origin, final List<Address> destinations, boolean sensor) throws Exception {
		return getClosestDestination(origin, destinations, sensor, PREFER_DISTANCE);
	}
	
	
	/**
	 * Returns the destination address from the destination list that is closest in terms of the preference to the origin address.
	 * @param origin
	 * @param destinations
	 * @param sensor
	 * @param preference
	 * @return					address from destination list that is closest in travel time or distance to the origin address
	 * @throws Exception
	 */
	public Address getClosestDestination(final Address origin, final List<Address> destinations, boolean sensor, int preference) throws Exception {
		int idx = 0;
		int best = -1;
		
		List<Distance> distances = getDistanceMatrix(origin, destinations, sensor);
		
		for (int i = 0; i < distances.size(); i++) {
			Log.d(TAG, "current distance: " + distances.get(i).getDistance());
			if (preference == PREFER_DISTANCE && (best == -1 || distances.get(i).getDistance() < best)) {
				best = distances.get(i).getDistance();
				idx = i;
				Log.d(TAG, "new best distance: " + distances.get(i).getDistance());
				Log.d(TAG, "idx=" + idx);
			} else if (preference == PREFER_DURATION && (best == -1 || distances.get(i).getDuration() < best)) {
				best = distances.get(i).getDuration();
				idx = i;
			}
		}
		
		return destinations.get(idx);
	}
	
	
	/**
	 * Gets the results from calling Google's Distance Matrix API with the given origin and destinations list.<br />
	 * (This is where the JSON response is parsed)
	 * @param origin
	 * @param destinations
	 * @param sensor
	 * @return
	 * @throws Exception
	 */
	public List<Distance> getDistanceMatrix(final Address origin, final List<Address> destinations, boolean sensor) throws Exception {
		// Get the JSON string response from the webservice
		String jsonResp = getJSONResponse(origin, destinations, sensor);
		Log.v(TAG, "jsonResp: " + jsonResp);
		
		// Parse the JSON string into distance objects
		List<Distance> distances = new ArrayList<Distance>();
		JSONObject response = (JSONObject) new JSONTokener(jsonResp.toString()).nextValue();

		JSONArray rows = response.getJSONArray("rows");
		JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");		// there will only be 1 "row" because there's only 1 origin
		
		Distance d = null;
		for (int e = 0; e < elements.length(); e++) {
			d = new Distance();
			JSONObject element = elements.getJSONObject(e);
			JSONObject distance = element.getJSONObject("distance");
			JSONObject duration = element.getJSONObject("duration");
			
			d.setDistance(distance.getInt("value"));
			d.setDistanceText(distance.getString("text"));
			d.setDuration(duration.getInt("value"));
			d.setDurationText(duration.getString("text"));
			
			distances.add(d);
		}
		
		return distances;
	}


	/**
	 * Builds the Google Distance Matrix API URL string and retrieves the JSON string response.
	 * @param origin
	 * @param destinations
	 * @param sensor
	 * @return
	 * @throws Exception
	 */
	private String getJSONResponse(Address origin, List<Address> destinations, boolean sensor) throws Exception {
		// Add origin
		StringBuilder url = new StringBuilder(G_DISTANCE_MATRIX_URL);
		url.append("origins=");
		url.append(origin.getLatitude());
		url.append(",");
		url.append(origin.getLongitude());
		
		// Add destinations
		boolean first = true;
		url.append("&destinations=");
		for (Address dest : destinations) {
			if (first) {
				first = false;
			} else {
				url.append("|");
			}
			
			url.append(dest.getLatitude());
			url.append(",");
			url.append(dest.getLongitude());
		}
		
		url.append("&sensor=");
		if (sensor) {
			url.append("true");
		} else {
			url.append("false");
		}
		
		Log.d(TAG, "DIST MAT URL: " + url.toString());
		
		URL distMatUrl = new URL(url.toString());
		URLConnection conn = distMatUrl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer jsonResp = new StringBuffer();
		
		try {
			String line = null;
			while ((line = in.readLine()) != null) {
				jsonResp.append(line);
			}
		} finally {
			in.close();
		}
		
		return jsonResp.toString();
	}
}
