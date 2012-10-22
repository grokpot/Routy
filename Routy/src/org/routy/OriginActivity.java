package org.routy;

import org.routy.service.LocationService;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OriginActivity extends Activity {

	private final String TAG = "OriginActivity";
	
	private LocationService locationService;
	private TextView locationResultOutput;
	private Button findUserButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        
        locationResultOutput = (TextView) findViewById(R.id.location_result);
        
        findUserButton = (Button) findViewById(R.id.find_user_button);
        
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationService = new LocationService(locManager, 200) {
			
			@Override
			public void onLocationResult(Location location) {
				// TODO Auto-generated method stub
				locationResultOutput.setText("Location: " + location.getLatitude() + ", " + location.getLongitude());
				findUserButton.setText(R.string.find_user_prompt);
			}
		};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_origin, menu);
        return true;
    }
    
    
    public void findUserLocation(View view) {
    	Log.v(TAG, "get user location here...");
    	// TODO Get and display user location
    	
    	findUserButton.setText(R.string.please_wait);
    	findUserButton.setEnabled(false);
    	
    	try {
    		locationService.getCurrentLocation();
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage());
    		findUserButton.setText(R.string.find_user_prompt);
    		findUserButton.setEnabled(true);
    	}
    }
}
