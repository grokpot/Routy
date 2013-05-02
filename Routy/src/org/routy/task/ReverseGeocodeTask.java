package org.routy.task;

import java.io.IOException;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.RoutyException;
import org.routy.listener.ReverseGeocodeListener;
import org.routy.model.RoutyAddress;
import org.routy.service.AddressService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class ReverseGeocodeTask extends AsyncTask<Location, Void, RoutyAddress> {

	private final String TAG = "ReverseGeocodeTask";
	
	private final Context context;
	private final AddressService service;
	private final ReverseGeocodeListener listener;
	private ProgressDialog progressDialog;
	
	public ReverseGeocodeTask(Context context, boolean sensor, ReverseGeocodeListener listener) {
		this.context = context;
		this.service = new AddressService(new Geocoder(context), sensor);
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		// Build and display the loading spinner
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Hang Tight!");
		progressDialog.setMessage("Getting an address...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.cancel();
				
				Log.v(TAG, "progress dialog cancelled");
				ReverseGeocodeTask.this.cancel(true);
			}
		});
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	@Override
	protected RoutyAddress doInBackground(Location... params) {
		if (params.length > 0) {
			try {
				return service.getAddressForLocation(params[0]);
			} catch (AmbiguousAddressException e) {
				return e.getFirstAddress();
			} catch (RoutyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(RoutyAddress address) {
		Log.v(TAG, "postExecute() -- got user location");
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		listener.onResult(address);
	}
	
	
	@Override
	protected void onCancelled(RoutyAddress address) {
		Log.v(TAG, "reverse geocoding cancelled");
		progressDialog.cancel();
	}

}
