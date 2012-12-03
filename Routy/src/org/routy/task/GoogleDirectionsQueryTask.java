package org.routy.task;

import org.routy.listener.GoogleDirectionsQueryListener;
import org.routy.model.GoogleDirections;
import org.routy.model.GoogleDirectionsQuery;
import org.routy.service.GoogleDirectionsService;

import android.os.AsyncTask;

public abstract class GoogleDirectionsQueryTask extends AsyncTask<GoogleDirectionsQuery, Void, GoogleDirections> {
	
	private final String TAG = "GoogleDirectionsQueryTask";
	
	private GoogleDirectionsQueryListener listener;
	
	public GoogleDirectionsQueryTask(GoogleDirectionsQueryListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected GoogleDirections doInBackground(GoogleDirectionsQuery... params) {
		if (params.length > 0) {
			try {
				GoogleDirectionsQuery query = params[0];
				
				GoogleDirectionsService service = new GoogleDirectionsService();
				return service.getDirections(query.getOrigin(), query.getDestination(), query.isSensor());
			} catch (Exception e) {
				// TODO cancel and call onFailure
				listener.onFailure(e);
				cancel(true);
			}
		} else {
			listener.onFailure(new IllegalArgumentException("no google directions query was provided to the async task"));
			cancel(true);
		}

		return null;
	}
	
	
	@Override
	protected void onPostExecute(GoogleDirections directions) {
		// TODO
		listener.onGotDirections(directions);
	}
	
	
	@Override
	protected void onCancelled(GoogleDirections directions) {
		// TODO
		
	}

}
