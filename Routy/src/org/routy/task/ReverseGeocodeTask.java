package org.routy.task;

import java.io.IOException;
import java.util.Timer;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;
import org.routy.listener.ReverseGeocodeListener;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.model.RoutyAddress;
import org.routy.service.AddressService;
import org.routy.timer.Timeout;
import org.routy.timer.TimeoutCallback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class ReverseGeocodeTask extends AsyncTask<Location, Void, RoutyAddress> {

	private final String TAG = "ReverseGeocodeTask";
	
	private final Context context;
	private Timer timer;
	private boolean showDialogs;
	private final AddressService service;
	private final ReverseGeocodeListener listener;
	private ProgressDialog progressDialog;
	
	public ReverseGeocodeTask(Context context, boolean sensor, boolean showDialogs, ReverseGeocodeListener listener) {
		this.context = context;
		this.showDialogs = showDialogs;
		this.service = new AddressService(this, new Geocoder(context), sensor);
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		if (showDialogs) {
			// Build and display the loading spinner
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Hang Tight!");
			progressDialog.setMessage("Getting an address...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressDialog.cancel();
					
//					Log.v(TAG, "progress dialog cancelled");
					ReverseGeocodeTask.this.cancel(true);
				}
			});
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
	}
	
	@Override
	protected RoutyAddress doInBackground(Location... params) {
		if (params.length > 0) {
			timer = new Timer();
			try {
				timer.schedule(new Timeout(this, new TimeoutCallback() {
					
					@Override
					public void onTimeout() {
						Log.v(TAG, "reverse geocode task timed out");
						listener.onReverseGeocodeTimeout();
					}
				}), AppConfig.REVERSE_GEOCODE_TIMEOUT_MS);
				
				return service.getAddressForLocation(params[0]);
			} catch (AmbiguousAddressException e) {
				return e.getFirstAddress();
			} catch (RoutyException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoInternetConnectionException e) {
				listener.onNoInternetConnectionException();
				ReverseGeocodeTask.this.cancel(true);
			} finally {
				if (timer != null) {
					timer.cancel();
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(RoutyAddress address) {
		if (showDialogs && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		listener.onResult(address);
	}
	
	
	@Override
	protected void onCancelled(RoutyAddress address) {
		Log.v(TAG, "reverse geocode task cancelled");
		if (showDialogs) {
			progressDialog.cancel();
		}
		
		if (timer != null) {
			timer.cancel();
		}
	}

}
