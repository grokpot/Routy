package org.routy.task;

import java.util.Timer;

import org.routy.Timeout;
import org.routy.TimeoutCallback;
import org.routy.exception.GpsNotEnabledException;
import org.routy.exception.NoLocationProviderException;
import org.routy.listener.FindUserLocationListener;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.service.LocationService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

/**
 * Use this AsyncTask subclass to do all WIFI/LOCATION getting off the main UI.  This 
 * doesn't HAVE to be done in an AsyncTask, but it also allows us to put up a loading 
 * spinner while the location is determined.
 * 
 * @author jtran
 *
 */
public class FindUserLocationTask extends AsyncTask<Integer, Void, Location> {
	
	
	private final String TAG = "FindUserLocationTask";
	
	private Context context;
	private boolean showDialogs;
	private FindUserLocationListener listener;
	private LocationManager locManager;
	private LocationService locService;
	private Location location;
	
	private ProgressDialog progressDialog;
	
	public FindUserLocationTask(Context context, boolean showDialogs, FindUserLocationListener listener) {
		super();
		
		this.context = context;
		this.showDialogs = showDialogs;
		this.listener = listener;
		this.location = null;
		
		locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		initLocationService();
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		// Build and display the loading spinner
		if (showDialogs) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Hang Tight!");
			progressDialog.setMessage("Looking for you...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressDialog.cancel();
					FindUserLocationTask.this.cancel(true);
				}
			});
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		try {
			locService.getCurrentLocation();
		} catch (NoLocationProviderException e) {
			listener.onFailure(e);
			super.cancel(true);
		}
	}
	

	@Override
	protected Location doInBackground(Integer... params) {
		//This timer runs in a separate thread.  It'll call the Timeout (inner class)'s run method which calls callback.onTimeout() in 10 seconds.
		Timer timer = new Timer();
		timer.schedule(new Timeout(this, new TimeoutCallback() {
			@Override
			public void onTimeout() {
				listener.onTimeout(new GpsNotEnabledException());
			}
		}), AppConfig.LOCATION_FETCH_TIMEOUT_MS);
		while (location == null && !isCancelled()) {
//			loop
		}
		
		timer.cancel();
		return location;
	}
	
	
	@Override
	protected void onCancelled(Location location) {
		if (showDialogs) {
			progressDialog.cancel();
		}
		locService.stop();
	}
	
	@Override
	protected void onPostExecute(Location location) {
		if (showDialogs && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		listener.onUserLocationFound(location);
	}
	
	private void initLocationService() {
		locService = new LocationService(locManager, AppConfig.USER_LOCATION_ACCURACY_THRESHOLD_M) {

			@Override
			public void onLocationResult(Location result) {
				Log.v(TAG, "got a location result");
				location = result;
			}


			@Override
			public void onLocationSearchTimeout() {
				Log.v(TAG, "user location timed out");
				GpsNotEnabledException e = null;
				
				if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Log.i(TAG, "GPS was not enabled");
					e = new GpsNotEnabledException("GPS is not enabled.");
				}
				
				if (showDialogs) {
					progressDialog.cancel();
				}
				listener.onTimeout(e);
				cancel(true);
			}
		};
	}
	
}
