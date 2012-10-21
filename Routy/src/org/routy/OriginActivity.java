package org.routy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class OriginActivity extends Activity {
	
	private final String TAG = "OriginActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "OriginActivity created.");
	}
}
