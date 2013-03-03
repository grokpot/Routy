package org.routy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.view.ResultsSegmentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPositionCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

//public class ResultsActivity extends FragmentActivity {
public class ResultsActivity extends Activity {
	
	private static final int INSTRUCTIONS_DIALOG = 1;
	Context mContext;
	
	// MapView stuff
//	private MapView					mapView;
//	private MapController			mapController;
	private ArrayList<GeoPoint> 	geoPoints;
	private List<Overlay> 			mapOverlays;
	
	// MapFragment stuff
	private MapFragment mapFragment;
	private GoogleMap mMap;
	private List<LatLng> points;
	private List<Address> addresses;
	
	private SharedPreferences resultsActivityPrefs;
	
	// The Route sent by DestinationActivity
	Route route;

	private LinearLayout resultsLayout;

	private final String TAG = "ResultsActivity";

	private SoundPool sounds;
	private int click;
	private RouteOptimizePreference routeOptimizePreference;
	AudioManager audioManager;
	float volume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
//		context  = this;
		mContext = this;

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_SYSTEM);

		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		click = sounds.load(this, R.raw.routyclick, 1);
		

		// Get the layout containing the list of destination
		resultsLayout = (LinearLayout) findViewById(R.id.linearlayout_results);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int distance = (Integer) extras.get("distance");
			addresses = (ArrayList<Address>) extras.get("addresses");
			Log.v(TAG, "Results: " + addresses.size() + " addresses");
			route = new Route(addresses, distance);
			routeOptimizePreference = (RouteOptimizePreference) extras.get("optimize_for");
		}
		
		initMapView();
		
		resultsActivityPrefs = getSharedPreferences("results_prefs", MODE_PRIVATE);
		// First-time user dialog cookie
		boolean noobCookie = resultsActivityPrefs.getBoolean("noob_cookie", false);
		if (!noobCookie){
			showNoobDialog();
			userAintANoob();
		}
	}
	

	/**
	 * Displays an {@link AlertDialog} with one button that dismisses the dialog. Dialog displays helpful first-time info.
	 * 
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	private void showNoobDialog() {
		// Yes, this is deprecated, but there's a conflict with RA.java extending FragmentActivity and MapActivity
		showDialog(INSTRUCTIONS_DIALOG);
	}
	
	/**
	 *  If the user sees the first-time instruction dialog, they won't see it again next time.
	 */
	private void userAintANoob() {
		SharedPreferences.Editor ed = resultsActivityPrefs.edit();
		ed.putBoolean("noob_cookie", true);
		ed.commit();	
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
			case INSTRUCTIONS_DIALOG:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle(R.string.results_noob_title);
		        builder.setMessage(R.string.results_noob_instructions);
				builder.setPositiveButton(android.R.string.ok, 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
		                      return;
		                } });
		        return builder.create();
			}
		return null;
	}
	
	
	void initMapView() {	
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfrag_results);
		mMap = mapFragment.getMap();
		
		points = new ArrayList<LatLng>();
		
		if (mMap == null) {
			Log.e(TAG, "map service is not available");
		} else {
			try {
				MapsInitializer.initialize(this);
				buildResultsView();
			} catch (Exception e) {
				Log.e(TAG, "Error initializing Map -- " + e.getMessage());
			}
		}
		
		Drawable drawable 	= this.getResources().getDrawable(R.drawable.pin1);
		geoPoints 			= new ArrayList<GeoPoint>();
	}
	
	
	private void zoomToOverlays(List<LatLng> points) {
		// Compute northeast and southeast corners
		// Marker icons are 67x67 for the largest one (home)
		
		LatLngBounds.Builder builder = LatLngBounds.builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		
		CameraUpdate update = CameraUpdateFactory.newLatLngBounds(builder.build(), 300, 300, 0);		// TODO Need to figure out how to get the width of the viewing area
		mMap.animateCamera(update);
	}
	

	// Dynamically build the results screen by building a ResultsRowView, which inflates view_result_segment
	private void buildResultsView() {
		int addressesSize = route.getAddresses().size();

		Address address = null;
		// TODO: do we need lastAddress?
		boolean isLastAddress = false;
		ResultsSegmentView v;
		LatLng latlng = null;

		for (int addressIndex = 0; addressIndex < addressesSize; addressIndex++) {
			address = route.getAddresses().get(addressIndex);
			// special case if it's the last segment
			if (addressIndex == addressesSize - 1) {
				isLastAddress = true;
			}
			
			// Put point on MapView
			//http://stackoverflow.com/questions/3577866/android-geopoint-with-lat-long-values
//			GeoPoint geopoint = new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
//			geoPoints.add(geopoint);
//			OverlayItem overlayitem = new OverlayItem(geopoint, address.getFeatureName(), "Location #" + addressIndex);
//			RoutyItemizedOverlay itemizedOverlay = new RoutyItemizedOverlay(Util.getItemizedPin(addressIndex, mContext));
//			itemizedOverlay.addOverlay(overlayitem);
			
			// Convert all the lat/long values to LatLng objects to use for computing the map boundaries later
			latlng = new LatLng(address.getLatitude(), address.getLongitude());
			points.add(latlng);
			
			if (addressIndex == 0) {
				// Set the initial camera position looking right at the origin location
				mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latlng).build()));
			}
			
			mMap.addMarker(new MarkerOptions().position(latlng)
								.title(address.getFeatureName())
								.icon(BitmapDescriptorFactory.fromResource(Util.getItemizedPinId(addressIndex))));
			
			v = new ResultsSegmentView(mContext, address, addressIndex, isLastAddress) {

				@Override
				public void onSegmentClicked(int id, boolean isLastAddress) {
					showSegmentInGoogleMaps(id, isLastAddress);
				}
			};

			resultsLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//			mapOverlays.add(itemizedOverlay);
		}
		
		// Sit-chee-ate the map
		zoomToOverlays(points);

		// Route information display
		TextView text_total_distance = (TextView) findViewById(R.id.textview_total_distance);
		TextView text_total_duration = (TextView) findViewById(R.id.textview_total_duration);
		
		if (routeOptimizePreference.equals(RouteOptimizePreference.PREFER_DISTANCE)) {
			text_total_duration.setVisibility(View.INVISIBLE);
			String truncatedDistanceInMiles = convertMetersToMiles(route.getTotalDistance());
			text_total_distance.setText(getString(R.string.total_distance) + truncatedDistanceInMiles + " miles");
		} else if (routeOptimizePreference.equals(RouteOptimizePreference.PREFER_DURATION)) {
			text_total_distance.setVisibility(View.INVISIBLE);
			String durationInMinutes = convertSecondsToMinutes(route.getTotalDistance());
			text_total_duration.setText(getString(R.string.total_duration) + durationInMinutes + " minutes");
		}
		
		
	}
	
	private void showSegmentInGoogleMaps(int id,
			boolean isLastAddress) {
		if (!isLastAddress){
			volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(click, volume, volume, 1, 0, 1);
				
			// Get start and end Addresses from route[] - the index is the id in ResultsSegmentView
			Address startAddress	= route.getAddresses().get(id);
			Address endAddress 		= route.getAddresses().get(id + 1);

			// Build call to Google Maps native app
			double startAddressLat = startAddress.getLatitude();
			double startAddressLong = startAddress.getLongitude();
			double endAddressLat = endAddress.getLatitude();
			double endAddressLong = endAddress.getLongitude();

			// Button segment GMaps call
			String mapsCall = "http://maps.google.com/maps?saddr="
					+ startAddressLat + "," + startAddressLong
					+ "&daddr=" + endAddressLat + ","
					+ endAddressLong;
			Log.d(TAG, "maps segment call URI: " + mapsCall);

			// Open Google Maps App on the device
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsCall));
			startActivity(intent);
		}
	}

	private String convertMetersToMiles(int distanceInMeters) {
		final double MILE_RATIO 	= 0.000621371;
		double distanceInMiles 		= distanceInMeters * MILE_RATIO; 
		return new DecimalFormat("#.##").format(distanceInMiles);
	}
	
	private String convertSecondsToMinutes(int durationInSeconds) {
		final double RATIO = 60;
		double durationInMinutes = durationInSeconds / RATIO;
		return Long.valueOf(Math.round(durationInMinutes)).toString();
	}

	// Currently unused
	/*private void drawPath(GeoPoint startPoint,GeoPoint endPoint){		
	    MapRoute oRoute = new MapRoute(startPoint,endPoint);
	    oRoute.getPoints(new RouteListener(){
	    	
	        @Override
	        public void onDetermined(ArrayList<GeoPoint> geoPoints){
	            GeoPoint oPointA = null;
	            GeoPoint oPointB = null;

//	            mapView.getOverlays().clear();

	            for(int i=1; i<geoPoints.size()-1; i++){
	                oPointA = geoPoints.get(i-1);
	                oPointB = geoPoints.get(i);
	                mapOverlays.add(new MapRouteOverlay(oPointA,oPointB,2,Color.RED));
	            }
//	            mapOverlays.add(new MapRoutePinOverlay(geoPoints.get(0),dPin));
//	            mapOverlays.add(new MapRoutePinOverlay(geoPoints.get(geoPoints.size()-1),dPin));

	            mapView.invalidate();
	        }
	        
	        @Override
	        public void onError(){
	        }           
	        
	    });
	}*/
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_results, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		click = sounds.load(this, R.raw.routyclick, 1);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (sounds != null) {
			sounds.release();
			sounds = null;
		}
	}

	/*@Override
	protected boolean isRouteDisplayed() {
		return false;
	}*/
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}
}
