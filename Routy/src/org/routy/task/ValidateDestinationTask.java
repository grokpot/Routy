package org.routy.task;

import java.util.ArrayList;
import java.util.List;

import org.routy.exception.RoutyException;
import org.routy.model.GooglePlace;
import org.routy.model.ValidateDestinationRequest;
import org.routy.service.GooglePlacesService;

import android.os.AsyncTask;
import android.util.Log;

public abstract class ValidateDestinationTask extends AsyncTask<ValidateDestinationRequest, Void, List<GooglePlace>> {

	private final String TAG = "ValidateDestinationTask";
	
	
	public abstract void onResult(List<GooglePlace> results);
	public abstract void onFailure(RoutyException exception);
	
	@Override
	protected List<GooglePlace> doInBackground(ValidateDestinationRequest... params) {
		if (params != null && params.length > 0) {
			ValidateDestinationRequest request = params[0];
			
			GooglePlacesService placesService = new GooglePlacesService();
			try {
				List<GooglePlace> results = placesService.getPlacesForKeyword(request.getQuery(), request.getCenterLatitude(), request.getCenterLongitude(), request.getRadius());		// TODO pass in the origin's lat/lng
//				Log.v(TAG, "Got results from Google Places API");
				return results;
			} catch (RoutyException e) {
				Log.e(TAG, "RoutyException - " + e.getMessage());
				onFailure(e);
				cancel(true);
			}
		}
		
		return new ArrayList<GooglePlace>();
	}
	
	
	@Override
	protected void onPostExecute(List<GooglePlace> results) {
//		Log.v(TAG, "onPostExecute results size=" + results.size());
		onResult(results);
	}

}
