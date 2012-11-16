package org.routy.service;

import java.util.List;

import org.routy.exception.RoutyException;
import org.routy.model.GooglePlace;

import android.test.AndroidTestCase;
import android.util.Log;

public class GooglePlacesServiceTest extends AndroidTestCase {

	private final String TAG = "GooglePlacesServiceTest";
	
	public void setUp() throws Exception {
		
	}
	
	public void testGetPlacesForKeyword() {
		String keyword = "heb";
		
		GooglePlacesService svc = new GooglePlacesService();
		
		try {
			List<GooglePlace> places = svc.getPlacesForKeyword(keyword, 30.28749, -97.73783, 5000);
			
			if (places != null) {
				Log.v(TAG, places.size() + " places found.");
			}
			
			for (GooglePlace p : places) {
				Log.v(TAG, p.getName() + " - " + p.getFormattedAddress());
			}
		} catch (RoutyException e) {
			Log.e(TAG, "RoutyException");
		}
	}
	
	public void tearDown() throws Exception {
		
	}
}
