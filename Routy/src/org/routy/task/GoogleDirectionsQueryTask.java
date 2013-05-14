package org.routy.task;

import java.util.Timer;

import org.routy.listener.GoogleDirectionsQueryListener;
import org.routy.model.GoogleDirections;
import org.routy.model.GoogleDirectionsQuery;
import org.routy.service.GoogleDirectionsService;
import org.routy.timer.Timeout;
import org.routy.timer.TimeoutCallback;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Doesn't look like this is being used at all (5/13/2013)
 *
 */
public abstract class GoogleDirectionsQueryTask extends AsyncTask<GoogleDirectionsQuery, Void, GoogleDirections> {
	
	//private final String TAG = "GoogleDirectionsQueryTask";
	
	private Context context;
	private Timer timer;
	private GoogleDirectionsQueryListener listener;
	private ProgressDialog progressDialog;
	
	
	public GoogleDirectionsQueryTask(Context context, GoogleDirectionsQueryListener listener) {
		super();
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Checking that address or place name...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	@Override
	protected GoogleDirections doInBackground(GoogleDirectionsQuery... params) {
		if (params.length > 0) {
			timer = new Timer();
			try {
				timer.schedule(new Timeout(this, new TimeoutCallback() {
					
					@Override
					public void onTimeout() {
						// TODO Auto-generated method stub
						
					}
				}), 10000);
				GoogleDirectionsQuery query = params[0];
				GoogleDirectionsService service = new GoogleDirectionsService();
				return service.getDirections(query.getOrigin(), query.getDestination(), query.isSensor());
			} catch (Exception e) {
				listener.onFailure(e);
				cancel(true);
			} finally {
				if (timer != null) {
					timer.cancel();
				}
			}
		} else {
			listener.onFailure(new IllegalArgumentException("no google directions query was provided to the async task"));
			cancel(true);
		}

		return null;
	}
	
	
	@Override
	protected void onPostExecute(GoogleDirections directions) {
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		
		listener.onGotDirections(directions);
		
		if (timer != null) {
			timer.cancel();
		}
	}
	
	
	@Override
	protected void onCancelled(GoogleDirections directions) {
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

}
