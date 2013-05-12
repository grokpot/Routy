package org.routy;

import org.routy.fragment.TwoButtonDialog;
import org.routy.listener.FindDeviceLocationListener;
import org.routy.model.AppProperties;
import org.routy.model.DeviceLocationModel;
import org.routy.model.PreferencesModel;
import org.routy.model.RouteOptimizePreference;
import org.routy.service.InternetService;
import org.routy.task.FindDeviceLocationTask;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";

	private Context mContext;
	private TwoButtonDialog noInternetErrorDialog;
	private SharedPreferences defaultSharedPrefs;
	
	private SoundPool sounds;
	private int bad;
	
  AudioManager audioManager;
  float volume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

		getActionBar().hide();
		setContentView(R.layout.activity_main);

		mContext = this;
		
		sounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		bad = sounds.load(this, R.raw.routybad, 1);
    
		startDeviceLocationTask();
		
		defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		loadPreferencesModel();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				checkForInternetAndContinue();
			}
			
		}, AppProperties.SPLASH_SCREEN_DELAY_MS);
	}
	
	
	private void loadPreferencesModel() {
		if (!defaultSharedPrefs.getBoolean("route_mode", false)) {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DISTANCE);
		} else {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DURATION);
		}
		
		PreferencesModel.getSingleton().setRoutyNoob(defaultSharedPrefs.getBoolean("routy_noob", true));
		defaultSharedPrefs.edit().putBoolean("routy_noob", false).commit();
		
		PreferencesModel.getSingleton().setResultsNoob(defaultSharedPrefs.getBoolean("results_noob", true));
	}


	private void checkForInternetAndContinue() {
		Log.v(TAG, "Checking for internet connection...");
		
		if (!InternetService.deviceHasInternetConnection(mContext)) {
			Log.v(TAG, "No internet connection.");
			volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(bad, volume, volume, 1, 0, 1);
			initErrorDialog(getResources().getString(R.string.no_internet_error));
			noInternetErrorDialog.show(MainActivity.this.getFragmentManager(), TAG);
		} else {
			Log.v(TAG, "Found an internet connection.");

			gotoOriginScreen();
		}
	}


	private void gotoOriginScreen() {
	   if(sounds != null) { 
	      sounds.release(); 
	      sounds = null; 
	    }
		// Start an intent to bring up the origin screen
		Intent originIntent = new Intent(MainActivity.this, OriginActivity.class);
		startActivity(originIntent);
	}
	
	
	private void initErrorDialog(String errorMsg) {
		noInternetErrorDialog = new TwoButtonDialog(getResources().getString(R.string.error_message_title), errorMsg, new String[] {"Try Again", "", "Quit"}) {
			
			@Override
			public void onRightButtonClicked(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				checkForInternetAndContinue();
			}
			
			@Override
			public void onLeftButtonClicked(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				MainActivity.this.finish();
			}
		};
	}
	
	
	private void startDeviceLocationTask() {
		Log.v(TAG, "starting device location");
		new FindDeviceLocationTask(this, new FindDeviceLocationListener() {

			@Override
			public void onDeviceFound(Location deviceLocation) {
				// TODO Store the device's location in DeviceLocationModel
				DeviceLocationModel.getSingleton().setDeviceLocation(deviceLocation);
			}
		}).execute();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		return false;
	}
}
