package org.routy.service;

import java.io.IOException;

import org.routy.exception.RoutyException;

import android.test.AndroidTestCase;
import android.util.Log;

public class InternetServiceTest extends AndroidTestCase {

	private final String TAG = "InternetServiceTest";
	
	
	@Override
  public void setUp() throws Exception {
		super.setUp();
	}
	
	
	public void testGetStringResponse() {
		String url = "https://maps.googleapis.com/maps/api/geocode/xml?address=11630+parkfield+dr&sensor=false";
		
		try {
			String resp = InternetService.getStringResponse(url);
			
			Log.v(TAG, "Response: " + resp);
		} catch (RoutyException e) {
			Log.e(TAG, "RoutyException - " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException - " + e.getMessage());
		}
	}
	
	
	@Override
  public void tearDown() throws Exception {
		super.tearDown();
	}
}
