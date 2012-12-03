package org.routy.service;

import org.routy.model.GoogleDirections;
import org.routy.model.Step;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class GoogleDirectionsServiceTest extends AndroidTestCase {

	private final String TAG = "GoogleDirectionsServiceTest";
	
	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void testGetDirections() {
		GeoPoint start = new GeoPoint(52310000, 16710000);
		GeoPoint end = new GeoPoint(51270000,6750000);
		boolean sensor = false;
		
		GoogleDirectionsService service = new GoogleDirectionsService();
		try {
			GoogleDirections directions = service.getDirections(start, end, sensor);
			assertEquals("number of steps mismatch", 23, directions.getSteps().size());
			
			for (Step step : directions.getSteps()) {
				assertEquals("travel mode mismatch", "DRIVING", step.getMode());
				assertNotNull(step.getPolyline());
			}
		} catch (Exception e) {
			Log.e(TAG, "couldn't get directions");
			fail();
		}
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
