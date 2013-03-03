package org.routy.task;

import java.util.Date;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.GpsNotEnabledException;
import org.routy.exception.NoLocationProviderException;
import org.routy.listener.FindUserLocationListener;
import org.routy.model.AppProperties;
import org.routy.service.AddressService;
import org.routy.service.LocationService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * TODO Use this AsyncTask subclass to do all WIFI/LOCATION getting off the main UI.
 * 
 * @author jtran
 *
 */
public class FindUserLocationTask extends AsyncTask<Integer, Void, Address> {
	
	
	private final String TAG = "FindUserLocationTask";
	
	private Context context;
	private FindUserLocationListener listener;
	private LocationService locService;
	private AddressService addressService;
	private Address address;
	
	private ProgressDialog progressDialog;
	
	public FindUserLocationTask(Context context, FindUserLocationListener listener) {
		super();
		
		this.context = context;
		this.listener = listener;
		this.addressService = new AddressService(new Geocoder(context, Locale.getDefault()), false);
		this.address = null;
	}
	
	
	@Override
	protected void onPreExecute() {
//		progressDialog = ProgressDialog.show(context, "Routy", "Hang tight!", true, true, onCancelListener);

		Log.v(TAG, "preExecute -- address is " + (address==null?"null":"not null"));
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Looking for you...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.cancel();
				
				Log.v(TAG, "progress dialog cancelled");
				FindUserLocationTask.this.cancel(true);
			}
		});
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
		
		final LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		locService = new LocationService(locManager, AppProperties.LOCATION_ACCURACY_THRESHOLD_M) {

			@Override
			public void onLocationResult(Location location) {
				Log.v(TAG, "got a location result...trying to reverse geocode it");
				// Reverse geocode the location into an address and populate the TextEdit
				Date locationUpdated = new Date();
				locationUpdated.setTime(location.getTime());

				try {
					address = addressService.getAddressForLocation(location);
					
				} catch (AmbiguousAddressException e) {
					Log.v(TAG, "more than reverse-geocoded address...using the first one");
					if (e.getAddresses().size() > 0) {
						address = e.getFirstAddress();
					}
				} catch (Exception e) {
					// Display an error to the user...it was already logged
					Log.e(TAG, "Error reverse geocoding user's location.");
					progressDialog.cancel();
					listener.onFailure(e);
					cancel(true);
				}
			}


			@Override
			public void onLocationSearchTimeout() {
				Log.v(TAG, "onLocationSearchTimeout");
				GpsNotEnabledException e = null;
				
				if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					e = new GpsNotEnabledException("GPS is not enabled.");
				}
				
				progressDialog.cancel();
				listener.onTimeout(e);
				cancel(true);
			}
		};
		
		try {
			locService.getCurrentLocation();
		} catch (NoLocationProviderException e) {
			listener.onFailure(e);
			super.cancel(true);
		}
	}
	

	@Override
	protected Address doInBackground(Integer... params) {
		Log.v(TAG, "doInBackground()");
		
		while (address == null) {
			if (isCancelled()) {
				break;
			}
		}
		
		Log.v(TAG, "out of the loop");
		return address;
	}
	
	
	@Override
	protected void onCancelled(Address address) {
		Log.v(TAG, "onCancelled called");
		
		progressDialog.cancel();
		locService.stop();
	}
	
	
	@Override
	protected void onPostExecute(Address userLocation) {
		Log.v(TAG, "postExecute() -- got user location");
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		if (userLocation.getExtras() == null) {
			Log.e(TAG, "userLocation address extras is null");
		}
		listener.onUserLocationFound(userLocation);
	}
	
	
	/*@Override
	protected void onCancelled() {
		super.onCancelled();
		
		Log.v(TAG, "cancelled");
		
		progressDialog.cancel();
		locService.stop();
	}*/
	
	
	/*private OnCancelListener onCancelListener = new OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			Log.v(TAG, "progress dialog cancelled");
			
			FindUserLocationTask.this.cancel(true);
		}
	};*/

}
