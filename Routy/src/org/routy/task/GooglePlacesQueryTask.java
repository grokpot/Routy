package org.routy.task;

import java.util.List;

import org.routy.adapter.PlacesListAdapter;
import org.routy.exception.RoutyException;
import org.routy.fragment.ListPickerDialog;
import org.routy.model.GooglePlace;
import org.routy.model.GooglePlacesQuery;
import org.routy.service.GooglePlacesService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public abstract class GooglePlacesQueryTask extends AsyncTask<GooglePlacesQuery, Void, List<GooglePlace>> {

	private final String TAG = "GooglePlacesQueryTask";
	
	private Activity activity;
	private ProgressDialog progressDialog;
	
	public abstract void onResult(GooglePlace place);
	public abstract void onFailure(Throwable t);
	public abstract void onNoSelection();
	
	public GooglePlacesQueryTask(Activity context) {
		super();
		
		this.activity = context;
	}
	
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Checking that address or place name...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	
	@Override
	protected List<GooglePlace> doInBackground(GooglePlacesQuery... params) {
		// Use the GooglePlacesService to get the result(s)
		if (params != null && params.length > 0) {
			GooglePlacesQuery q = params[0];
			Log.v(TAG, "Searching..." + q);		// XXX Possible injection point -- this is straight from the EditText the user inputs in some cases (Let Google worry about it?)
			
			GooglePlacesService gpSvc = new GooglePlacesService();
			try {
				List<GooglePlace> results = gpSvc.getPlacesForKeyword(q.getQuery(), q.getCenterLatitude(), q.getCenterLongitude(), q.getRadius());
				return results;
			} catch (RoutyException e) {
				Log.e(TAG, "RoutyException trying to get Google Places results");
				onFailure(e);
				GooglePlacesQueryTask.this.cancel(true);
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
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
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
