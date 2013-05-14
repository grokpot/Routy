package org.routy;

import org.routy.fragment.TwoButtonDialog;
import org.routy.listener.FindDeviceLocationListener;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.model.DeviceLocationModel;
import org.routy.model.PreferencesModel;
import org.routy.model.RouteOptimizePreference;
import org.routy.service.InternetService;
import org.routy.sound.SoundPlayer;
import org.routy.task.FindDeviceLocationTask;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";

	private Context mContext;
	private TwoButtonDialog noInternetErrorDialog;
	private SharedPreferences defaultSharedPrefs;
	
	/*private SoundPool sounds;
	private int bad;*/
	
//  AudioManager audioManager;
//  float volume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().hide();
		setContentView(R.layout.activity_main);

		mContext = this;
		
		startDeviceLocationTask();
		
		defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		loadPreferencesModel();
		
		SoundPlayer.playSpeak(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				checkForInternetAndContinue();
			}
			
		}, AppConfig.SPLASH_SCREEN_DELAY_MS);
	}
	
	
	private void loadPreferencesModel() {
		//Restore route optimization preference
		if (!defaultSharedPrefs.getBoolean("route_mode", false)) {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DURATION);
		} else {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DISTANCE);
		}
		
		//Restore in-app sounds preference
		PreferencesModel.getSingleton().setSoundsOn(defaultSharedPrefs.getBoolean("sounds_mode", true));
		
		PreferencesModel.getSingleton().setRoutyNoob(defaultSharedPrefs.getBoolean("routy_noob", true));
		defaultSharedPrefs.edit().putBoolean("routy_noob", false).commit();
		
		PreferencesModel.getSingleton().setResultsNoob(defaultSharedPrefs.getBoolean("results_noob", true));
	}


	private void checkForInternetAndContinue() {
		Log.v(TAG, "Checking for internet connection...");
		if (!InternetService.deviceHasInternetConnection(mContext)) {
			Log.v(TAG, "No internet connection.");
			/*if (PreferencesModel.getSingleton().isSoundsOn()) {
				volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
				volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
				sounds.play(bad, volume, volume, 1, 0, 1);
			}*/
			
			SoundPlayer.playBad(this);
			initErrorDialog(getResources().getString(R.string.no_internet_error));
			noInternetErrorDialog.show(MainActivity.this.getFragmentManager(), TAG);
		} else {
			Log.v(TAG, "Found an internet connection.");
			gotoOriginScreen();
		}
	}


	private void gotoOriginScreen() {
	   /*if(sounds != null) { 
	      sounds.release(); 
	      sounds = null; 
	    }*/
		// Start an intent to bring up the origin screen
		Intent originIntent = new Intent(MainActivity.this, OriginActivity.class);
		startActivity(originIntent);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// Analytics
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Analytics
		EasyTracker.getInstance().activityStop(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		SoundPlayer.done();
	}
	
	
	private void initErrorDialog(String errorMsg) {
		noInternetErrorDialog = new TwoButtonDialog(getResources().getString(R.string.error_message_title), errorMsg, new String[] {"Try Again", "", "Quit"}) {
			
			@Override
			public void onRightButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
				checkForInternetAndContinue();
			}
			
			@Override
			public void onLeftButtonClicked(DialogInterface dialog, int which) {
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
				//Store the device's location in DeviceLocationModel
				DeviceLocationModel.getSingleton().setDeviceLocation(deviceLocation);
			}
		}).execute();
	}
}
