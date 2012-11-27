package org.routy;

import java.util.ArrayList;

import org.routy.model.Route;

import android.content.Intent;
import android.location.Address;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

//public class ResultsActivity extends FragmentActivity {
public class ResultsActivity extends MapActivity {

	// The Route sent by DestinationActivity
	Route route;
	
	private MapView mapView;

	// Segment Labels
	private TextView segmentTexts[];
	// Segment buttons
	private Button segmentButtons[]; 

	private final String TAG = "ResultsActivity";

	private SoundPool sounds;
	private int click;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0); 
		click = sounds.load(this, R.raw.routyclick, 1);
		setContentView(R.layout.activity_results);

		Bundle extras 	= getIntent().getExtras();
		if (extras != null) {
			int distance = (Integer) extras.get("distance");
			ArrayList<Address> addresses =  (ArrayList<Address>) extras.get("addresses");
			Log.v(TAG, "Results: " + addresses.size() + " addresses");
			route = new Route(addresses, distance);
		}

		buildResultsView();
		
		initMapView();
	}

	// Dynamically build the results screen by iterating through segments and creating TextViews/Buttons
	private void buildResultsView() {
		int addressesSize	= route.getAddresses().size();
		segmentTexts 		= new TextView[addressesSize];
		segmentButtons 		= new Button[addressesSize];
		LinearLayout resultsLayout = (LinearLayout) findViewById(R.id.layout_results);

		TextView text_total_distance = (TextView) findViewById(R.id.textview_total_distance);
		text_total_distance.setText(getString(R.string.total_distance) + ((double) route.getTotalDistance())/1000 + "km");

		for (int addressIndex = 0; addressIndex < addressesSize; addressIndex++){

			// Create the TextView for this specific segment
			segmentTexts[addressIndex] = new TextView(this);

			// Dynamically set the text for the TextViews
			//			String addressText = route.getAddresses().get(addressIndex).getAddressLine(0);
			String addressText = route.getAddresses().get(addressIndex).getFeatureName();
			if (addressText == null || addressText.length() == 0) {
				addressText = route.getAddresses().get(addressIndex).getExtras().getString("formatted_address");
			}
			segmentTexts[addressIndex].setText(addressText);

			// Dynamically position TextView on the screen
			segmentTexts[addressIndex].layout(0, 20, 0, 0);
			resultsLayout.addView(segmentTexts[addressIndex]);

			// If it's not the last address, add a button segment
			if (addressIndex != (addressesSize - 1)){
				// Create the Button for this specific segment
				segmentButtons[addressIndex] = new Button(this);

				// Dynamically set the text for the Button
				segmentButtons[addressIndex].setText(getString(R.string.view_segment));
				segmentButtons[addressIndex].setId(addressIndex);

				// Instantiate the onClickListener
				segmentButtons[addressIndex].setOnClickListener(map_segment_listener);

				// Dynamically position button on the screen
				segmentButtons[addressIndex].layout(0, 20, 0, 0);
				resultsLayout.addView(segmentButtons[addressIndex]);
			}
		}	
	}
	
	void initMapView() {
		mapView = (MapView) findViewById(R.id.mapview_results);
		mapView.setBuiltInZoomControls(false);		// Don't let the user do anything to the map, and don't display zoom buttons
		
		MapController controller = mapView.getController();
		controller.setCenter(new GeoPoint(30390960, -97697490));
		controller.setZoom(17);
	}

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

		if(sounds != null) { 
			sounds.release(); 
			sounds = null; 
		} 
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
