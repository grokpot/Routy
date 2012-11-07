package org.routy;

import org.routy.fragment.OneButtonDialog;
import org.routy.model.AppProperties;
import org.routy.service.InternetService;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	private final String TAG = "MainActivity";

	private Context mContext;
	private OneButtonDialog noInternetErrorDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mContext = this;
		
		initErrorDialog();

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
			initErrorDialog();
			noInternetErrorDialog.show(MainActivity.this.getSupportFragmentManager(), TAG);
		} else {
			Log.v(TAG, "Found an internet connection.");

			gotoOriginScreen();
		}
	}


	private void gotoOriginScreen() {
		// Start an intent to bring up the origin screen
		Intent originIntent = new Intent(MainActivity.this, OriginActivity.class);
		startActivity(originIntent);
	}
	
	
	private void initErrorDialog() {
		noInternetErrorDialog = new OneButtonDialog(getResources().getString(R.string.error_message_title), getResources().getString(R.string.no_internet_error), "Try Again") {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
				checkForInternetAndContinue();
			}
		};
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
