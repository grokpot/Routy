package org.routy.task;

import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.model.RouteRequest;
import org.routy.service.RouteService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class CalculateRouteTask extends AsyncTask<RouteRequest, Void, Route> {

	private final String TAG = "CalculateRouteTask";

	private Context context;
	private ProgressDialog progressDialog;

	public CalculateRouteTask(Context context) {
		super();
		
		this.context = context;
	}
	
	
	@Override
	protected void onPreExecute() {
		// TODO make this cancelable -- just stop generating!
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Generating your route...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	
	@Override
	protected Route doInBackground(RouteRequest...requests) {
		try {
			if (requests.length == 0) {
//				Log.e(TAG, "CalculateRouteTask has no RouteRequest to process");
				CalculateRouteTask.this.cancel(true);
			} else {
				RouteRequest request = requests[0];
				
//				Log.v(TAG, "user prefers: " + (request.getPreference().equals(RouteOptimizePreference.PREFER_DISTANCE)?"distance":"") + (request.getPreference().equals(RouteOptimizePreference.PREFER_DURATION)?"duration":""));
				RouteService routeService = new RouteService(request.getOrigin(), request.getDestinations(), request.getPreference(), false);
				Route bestRoute = routeService.getBestRoute();
				
				return bestRoute;
			}
			
			
		} catch (Exception e) {
//			Log.e(TAG, "could not generate route");
			CalculateRouteTask.this.cancel(true);
		}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Route result) {
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		
		if (result != null) {
//			Log.d(TAG, "Best route calculated.");
			onRouteCalculated(result);
		} else {
//			Log.e(TAG, "Best route returned was null.");
		}
	}
	
	
	@Override
	protected void onCancelled(Route result) {
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}
	
	
	public abstract void onRouteCalculated(Route route);

}