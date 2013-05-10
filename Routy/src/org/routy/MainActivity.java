package org.routy;

import org.routy.fragment.TwoButtonDialog;
import org.routy.listener.FindDeviceLocationListener;
import org.routy.model.AppProperties;
import org.routy.model.DeviceLocationModel;
import org.routy.service.InternetService;
import org.routy.task.FindDeviceLocationTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	private final String TAG = "MainActivity";

	private Context mContext;
	private TwoButtonDialog noInternetErrorDialog;
	
	private SoundPool sounds;
	private int bad;
	
  AudioManager audioManager;
  float volume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

		setContentView(R.layout.activity_main);

		mContext = this;
		
		sounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		bad = sounds.load(this, R.raw.routybad, 1);
    
//		initErrorDialog();
		
		startDeviceLocationTask();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				checkForInternetAndContinue();
			}
			
		}, AppProperties.SPLASH_SCREEN_DELAY_MS);
	}
	
	
	private void checkForInternetAndContinue() {
		Log.v(TAG, "Checking for internet connection...");
		
		if (!InternetService.deviceHasInternetConnection(mContext)) {
			Log.v(TAG, "No internet connection.");
			volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(bad, volume, volume, 1, 0, 1);
			initErrorDialog(getResources().getString(R.string.no_internet_error));
			noInternetErrorDialog.show(MainActivity.this.getSupportFragmentManager(), TAG);
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
			
			/*@Override
			public void onUserLocationFound(Location userLocation) {
				Log.v(TAG, "got device location");
				new ReverseGeocodeTask(mContext, true, false, new ReverseGeocodeListener() {
					
					@Override
					public void onResult(RoutyAddress address) {
						if (address != null) {
							RoutyAddress userLoc = address;
							if (userLoc.getExtras() == null) {
								userLoc.setExtras(new Bundle());
							}
							UserLocationModel.getSingleton().setUserLocation(userLoc);
						}
					}
				}).execute(userLocation);
			}
			
			@Override
			public void onTimeout(GpsNotEnabledException e) {
				//DO NOTHING -- user will just have to try again by tapping "Find Me"
			}
			
			@Override
			public void onFailure(Throwable t) {
				//DO NOTHING -- user will just have to try again by tapping "Find Me"
			}*/
		}).execute();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
