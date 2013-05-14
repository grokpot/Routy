package org.routy.task;

import java.util.List;
import java.util.Timer;

import org.routy.adapter.PlacesListAdapter;
import org.routy.exception.RoutyException;
import org.routy.fragment.ListPickerDialog;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.model.GooglePlace;
import org.routy.model.GooglePlacesQuery;
import org.routy.service.GooglePlacesService;
import org.routy.timer.Timeout;
import org.routy.timer.TimeoutCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public abstract class GooglePlacesQueryTask extends AsyncTask<GooglePlacesQuery, Void, List<GooglePlace>> {

	private final String TAG = "GooglePlacesQueryTask";
	
	private Activity activity;
	private Timer timer;
	private ProgressDialog progressDialog;
	
	public abstract void onResult(GooglePlace place);
	public abstract void onFailure(Throwable t);
	public abstract void onNoSelection();
	public abstract void onGooglePlacesQueryTimeout();
	
	public GooglePlacesQueryTask(Activity context) {
		super();
		
		this.activity = context;
	}
	
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Checking that address or place name...");
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}
	
	
	@Override
	protected List<GooglePlace> doInBackground(GooglePlacesQuery... params) {
		// Use the GooglePlacesService to get the result(s)
		if (params != null && params.length > 0) {
			GooglePlacesQuery q = params[0];
			GooglePlacesService gpSvc = new GooglePlacesService();
			timer = new Timer();
			try {
				timer.schedule(new Timeout(this, new TimeoutCallback() {
					
					@Override
					public void onTimeout() {
						onGooglePlacesQueryTimeout();
					}
				}), AppConfig.G_PLACES_TIMEOUT_MS);
				List<GooglePlace> results = gpSvc.getPlacesForKeyword(q.getQuery(), q.getCenterLatitude(), q.getCenterLongitude(), q.getRadius());
				return results;
			} catch (RoutyException e) {
				Log.e(TAG, "RoutyException trying to get Google Places results");
				Log.e(TAG, e.getMessage());
				onFailure(e);
				GooglePlacesQueryTask.this.cancel(true);
			} finally {
				if (timer != null) {
					timer.cancel();
				}
			}
			
		}
		
		return null;
	}

	
	@Override
	protected void onPostExecute(List<GooglePlace> results) {
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		
		if (results == null || results.size() < 1) {
			onResult(null);
		} else if (results.size() == 1) {
			onResult(results.get(0));
		} else if (results.size() > 1) {
			showMultiResultsPickerDialog(results);
		}
	}
	
	
	@Override
	protected void onCancelled(List<GooglePlace> results) {
		this.cancel(true);
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		
		if (timer != null) {
			timer.cancel();
		}
	}
	
	
	/**
	 * Shows a dialog box with a list of possible results for the user to select from.
	 * 
	 * @param results
	 */
	private void showMultiResultsPickerDialog(List<GooglePlace> results) {
		final PlacesListAdapter adapter = new PlacesListAdapter(activity, results);
		new ListPickerDialog("Did you mean...", adapter) {
			
			@Override
			public void onSelection(int which) {
				onResult(adapter.getItem(which));
			}

			@Override
			public void onCancelled() {
				onNoSelection();
			}
			
			
		}.show(activity.getFragmentManager(), TAG);
	}
}
