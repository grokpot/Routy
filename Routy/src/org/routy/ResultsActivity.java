package org.routy;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.routy.fragment.OneButtonDialog;
import org.routy.model.Route;
import org.routy.view.ResultsSegmentView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

//public class ResultsActivity extends FragmentActivity {
public class ResultsActivity extends MapActivity {

	// TODO: consolidate these? I don't know the difference here - Ryan
	private FragmentActivity context;
	Context mContext;

	private SharedPreferences resultsActivityPrefs;
	
	// The Route sent by DestinationActivity
	Route route;
	
	private MapView mapView;

	// Segment Labels
	private TextView segmentTexts[];
	// Segment buttons
	private Button segmentButtons[]; 

	private LinearLayout resultsLayout;

	private final String TAG = "ResultsActivity";

	private SoundPool sounds;
	private int click;
	AudioManager audioManager;
	float volume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		context  = this;
		mContext = this;

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_SYSTEM);

		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		click = sounds.load(this, R.raw.routyclick, 1);
		setContentView(R.layout.activity_results);

		// Get the layout containing the list of destination
		resultsLayout = (LinearLayout) findViewById(R.id.linearlayout_results);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int distance = (Integer) extras.get("distance");
			ArrayList<Address> addresses = (ArrayList<Address>) extras.get("addresses");
			Log.v(TAG, "Results: " + addresses.size() + " addresses");
			route = new Route(addresses, distance);
		}

		buildResultsView();
		
		resultsActivityPrefs = getSharedPreferences("results_prefs", MODE_PRIVATE);
		// TODO: for testing purposes. Remove before prod.
		showNoobDialog();
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
	private void showNoobDialog() {
		OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.results_noob_title), getResources().getString(R.string.results_noob_instructions)) {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		};
		dialog.show(context.getSupportFragmentManager(), TAG);
	}
	
	/**
	 *  If the user sees the first-time instruction dialog, they won't see it again next time.
	 */
	private void userAintANoob() {
		SharedPreferences.Editor ed = resultsActivityPrefs.edit();
		ed.putBoolean("noob_cookie", true);
		ed.commit();	
	}
	
	/*void initMapView() {
		mapView = (MapView) findViewById(R.id.mapview_results);
		mapView.setBuiltInZoomControls(false);		// Don't let the user do anything to the map, and don't display zoom buttons
		
		MapController controller = mapView.getController();
		// TODO instead of doing this, calculate the span of lat and lng and call zoomToSpan
		controller.setCenter(new GeoPoint(30390960, -97697490));
		controller.setZoom(17);
	}*/

	View.OnClickListener map_segment_listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			sounds.play(click, 1, 1, 1, 0, 1);

			// Get the ID assigned in buildResultsView() so we can get the respective segment
			final int start 	= v.getId();
			// We need the next address to calculate a segment to send to Google, so we get the next destination index as well.
			final int dest		= start + 1;
			assert route.getAddresses().get(dest) != null;

			// Build call to Google Maps native app
			double startingAddressLat 		= route.getAddresses().get(start).getLatitude();
			double startingAddressLong 		= route.getAddresses().get(start).getLongitude();
			double destinationAddressLat 	= route.getAddresses().get(dest).getLatitude();
			double destinationAddressLong 	= route.getAddresses().get(dest).getLongitude();
			String mapsCall = "http://maps.google.com/maps?saddr=" 
					+ startingAddressLat + ","
					+ startingAddressLong + "&daddr="
					+ destinationAddressLat + ","
					+ destinationAddressLong;
			Log.d(TAG, "maps segment call URI: " + mapsCall);

			// Open Google Maps App on the device
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapsCall));
			startActivity(intent);
		}
	};

	// Dynamically build the results screen by building a ResultsRowView, which inflates view_result_segment
	private void buildResultsView() {
		int addressesSize = route.getAddresses().size();

		Address address = null;
		// TODO: do we need lastAddress?
		boolean isLastAddress = false;
		for (int addressIndex = 0; addressIndex < addressesSize; addressIndex++) {
			address = route.getAddresses().get(addressIndex);
			// special case if it's the last segment
			if (addressIndex == addressesSize - 1) {
				isLastAddress = true;
			}

			// TODO: do we need to send addressIndex?
			ResultsSegmentView v = new ResultsSegmentView(mContext, address, addressIndex, isLastAddress) {

				@Override
				public void onSegmentClicked(int id, boolean isLastAddress) {
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
			};

			resultsLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		TextView text_total_distance = (TextView) findViewById(R.id.textview_total_distance);
		String truncatedDistanceInMiles = convertMetersToMiles(route.getTotalDistance());
		text_total_distance.setText(getString(R.string.total_distance) + truncatedDistanceInMiles + " miles");
	}

	private String convertMetersToMiles(int distanceInMeters) {
		final double MILE_RATIO 	= 0.000621371;
		double distanceInMiles 		= distanceInMeters * MILE_RATIO; 
		return new DecimalFormat("#.##").format(distanceInMiles);
	}

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
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
