package org.routy;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.routy.callback.ValidateAddressCallback;
import org.routy.exception.GpsNotEnabledException;
import org.routy.exception.NoLocationProviderException;
import org.routy.fragment.OneButtonDialog;
import org.routy.fragment.TwoButtonDialog;
import org.routy.listener.FindUserLocationListener;
import org.routy.listener.ReverseGeocodeListener;
import org.routy.model.AddressModel;
import org.routy.model.AppProperties;
import org.routy.model.DeviceLocationModel;
import org.routy.model.GooglePlace;
import org.routy.model.GooglePlacesQuery;
import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.model.RouteRequest;
import org.routy.model.RoutyAddress;
import org.routy.task.CalculateRouteTask;
import org.routy.task.FindUserLocationTask;
import org.routy.task.GooglePlacesQueryTask;
import org.routy.task.ReverseGeocodeTask;
import org.routy.view.DestinationEntryRow;
import org.routy.view.DestinationRowView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

public class OriginActivity extends Activity {

	private static final String TAG = "OriginActivity";
	private static final int ENABLE_GPS_REQUEST = 1;
	private final String SAVED_DESTS_JSON_KEY = "saved_destination_json";
	private final String SAVED_ORIGIN_JSON_KEY = "saved_origin_json";

	private Activity context;
	private AddressModel addressModel;

	private EditText originAddressField;
	private LinearLayout destLayout;

	// shared prefs for origin and destination persistence
	private SharedPreferences originActivityPrefs;


	private SoundPool sounds;
	private int bad;
	private int speak;
	private int click;
	private AudioManager audioManager;
	private float volume;
	RouteOptimizePreference routeOptimized;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate()");
		setContentView(R.layout.activity_origin);
		
		// Audio stuff
		initializeAudio();

		//Initializations
		context 			= this;
		addressModel = AddressModel.getSingleton();
		destLayout = (LinearLayout) findViewById(R.id.LinearLayout_destinations);	//Contains the list of destination rows
		
		originAddressField 	= (EditText) findViewById(R.id.origin_address_field);
		originActivityPrefs = getSharedPreferences("origin_prefs", MODE_PRIVATE);
		routeOptimized = RouteOptimizePreference.PREFER_DURATION;
		
		loadSavedData();
		bindInputFields();
		refreshDestinationLayout();
		refreshOriginLayout();
		originAddressField.requestFocus();

		showNoobInstructions();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.v(TAG, "menu has " + menu.size() + " items BEFORE");
		getMenuInflater().inflate(R.menu.menu_origin, menu);
		Log.v(TAG, "menu has " + menu.size() + " items AFTER");
		return super.onCreateOptionsMenu(menu);
	}


	/**
	 * Takes the destination list in the model and displays it appropriately in the OriginActivity
	 */
	private void refreshDestinationLayout() {
		// TODO Auto-generated method stub
		assert destLayout != null;
		destLayout.removeAllViews();
		
		//TODO Draw a new row for each destination
		if (addressModel.hasDestinations()) {
			for (int i = 0; i < addressModel.getDestinations().size(); i++) {
				addDestinationRow(addressModel.getDestinations().get(i), i);
			}
		}
		
		//If we can still take another destination, we'll display the entry row.
		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			displayDestinationEntryRow();
		}
	}
	
	
	private void addDestinationRow(RoutyAddress address, int indexInLayout) {
		DestinationRowView newRow = new DestinationRowView(this, address, indexInLayout) {
			
			@Override
			public void onRemoveClicked(int indexInLayout, UUID id) {
				// TODO Auto-generated method stub
				addressModel.removeDestination(indexInLayout);
				refreshDestinationLayout();
			}
			
			@Override
			public void onFocusLost(final int indexInLayout, UUID id, Editable s) {
				if (indexInLayout < addressModel.getDestinations().size() && !addressModel.getDestinations().get(indexInLayout).isValid()) {
					validateAddress(s.toString(), null, null, new ValidateAddressCallback() {
						@Override
						public void onAddressValidated(RoutyAddress validatedAddress) {
							addressModel.setDestinationAt(indexInLayout, validatedAddress);
							refreshDestinationLayout();
						}
					});
				}
			}
			
			@Override
			public void destinationTextChanged(int indexInLayout, Editable s) {
				addressModel.getDestinations().get(indexInLayout).setNotValidated();
			}
		};
		
		destLayout.addView(newRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}


	private void displayDestinationEntryRow() {
		DestinationEntryRow destEntryRow = new DestinationEntryRow(this) {
			
			@Override
			public void onEntryConfirmed(Editable s) {
				//TODO Replace this when we're fetching user location behind the scenes
				Double lat = null;
				Double lng = null;
				if (addressModel.getOrigin() != null && addressModel.getOrigin().isValid()) {
					lat = addressModel.getOrigin().getLatitude();
					lng = addressModel.getOrigin().getLongitude();
				}
				
				if (s != null && s.length() > 0) {
					validateAddress(s.toString(), lat, lng, new ValidateAddressCallback() {
						
						@Override
						public void onAddressValidated(RoutyAddress validatedAddress) {
							Log.v(TAG, "new destination entered, validating");
							//Add the validated address to the model and re-draw the destination layout
							addressModel.addDestination(validatedAddress);
							refreshDestinationLayout();
						}
					});
				}
			}
		};
		destLayout.addView(destEntryRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		destEntryRow.focusOnEntryField();
	}


	private void loadSavedData() {
		Log.v(TAG, "onCreate() -- loading model");
		String originJson = originActivityPrefs.getString(SAVED_ORIGIN_JSON_KEY, "");
		Log.v(TAG, "Origin JSON: " + originJson);
		String destJson = originActivityPrefs.getString(SAVED_DESTS_JSON_KEY, "");
		Log.v(TAG, "Destinations JSON: " + destJson);
		addressModel.loadModel(originJson, destJson);
	}


	private void bindInputFields() {
		originAddressField.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Log.v(TAG, "afterTextChanged()");
				
				//TODO this is janky and should be moved
				if (s != null && s.length() > 0) {
					if (addressModel.getOrigin() == null || !s.toString().equals(addressModel.getOrigin().getAddressString())) {
						RoutyAddress newOrigin = new RoutyAddress(Locale.getDefault());
						newOrigin.setExtras(new Bundle());
//						newOrigin.getExtras().putString("address_string", s.toString());
						newOrigin.setAddressString(s.toString());
//						newOrigin.getExtras().putString("validation_status", AddressStatus.NOT_VALIDATED.toString());
						newOrigin.setNotValidated();
						addressModel.setOrigin(newOrigin);
					}
				} else {
					Log.v(TAG, "origin text field was empty, nulling out the origin");
					if (addressModel == null) {
						Log.v(TAG, "addressModel is null");
					}
					addressModel.setOrigin(null);
				}
			}
		});
		originAddressField.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !addressModel.isOriginValid()) {
					Log.v(TAG, "origin field lost focus");
					//Validate the origin
					Editable originText = ((EditText) v).getEditableText();
					String locationQuery = null;
					if (originText != null) {
						locationQuery = originText.toString();
					}
					
					if (locationQuery != null && locationQuery.length() > 0) {
						validateAddress(locationQuery, null, null, new ValidateAddressCallback() {

							@Override
							public void onAddressValidated(RoutyAddress validatedAddress) {
								addressModel.setOrigin(validatedAddress);
								refreshOriginLayout();
							}
							
						});
					}
				}
			}
		});
	}


	private void initializeAudio() {
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0); 
		speak = sounds.load(this, R.raw.routyspeak, 1);  
		bad = sounds.load(this, R.raw.routybad, 1);
		click = sounds.load(this, R.raw.routyclick, 1);
		
		sounds.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
			  if (sampleId == speak) {
			    volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			    volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			    Log.v(TAG, "volume is: " + volume);
			    soundPool.play(sampleId, volume, volume, 1, 0, 1);
			  }
			}
		});
	}


	/**
	 * Takes the origin from the model and displays it appropriately in the OriginActivity
	 */
	private void refreshOriginLayout() {
		//Fill in the display/activity with data from the model
		if (/*addressModel.isOriginValid()*/ addressModel.getOrigin() != null) {
			originAddressField.setText(addressModel.getOrigin().getAddressString());
		}
	}

	
	/**
	 * Displays an {@link AlertDialog} with one button that dismisses the dialog. Dialog displays helpful first-time info.
	 * 
	 * @param message
	 */
	private void showNoobDialog() {
		OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.origin_noob_title), getResources().getString(R.string.origin_noob_instructions)) {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
//		dialog.show(context.getSupportFragmentManager(), TAG);
		dialog.show(context.getFragmentManager(), TAG);
	}
	
	
	/**
	 *  If the user sees the first-time instruction dialog, they won't see it again next time.
	 */
	private void userAintANoobNomore() {
		//TODO: combine these, combine the noob messages
		SharedPreferences.Editor ed = originActivityPrefs.edit();
		ed.putBoolean("noob_cookie", true);
		ed.commit();	
	}
	
	
	private void showNoobInstructions() {		
		// First-time user dialog cookie
		boolean noobCookie = originActivityPrefs.getBoolean("noob_cookie", false);
		if (!noobCookie){
			showNoobDialog();
			userAintANoobNomore();
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ENABLE_GPS_REQUEST:	//Called when the user returns from the GPS settings activity
			locate();
			break;
		}
	}


	/**
	 * Validates an address string.
	 * 
	 * @param c		the callback that is called when validation is successful
	 */
	public void validateAddress(final String locationQuery, final Double centerLat, final Double centerLng, final ValidateAddressCallback c) {
		if (locationQuery != null && locationQuery.length() > 0) {
			volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(click, volume, volume, 1, 0, 1);  

			Log.v(TAG, "validating address -- query: " + locationQuery);
			if (centerLat != null && centerLng != null) {
				Log.v(TAG, "searching around center @ " + centerLat + "," + centerLng);
			}
			
			// use GooglePlacesQueryTask to do this...
			new GooglePlacesQueryTask(context) {
				
				@Override
				public void onResult(GooglePlace place) {	//TODO get rid of GooglePlace object and just use Address?  Something unified.
					// make an Address out of the Google place
					RoutyAddress result = new RoutyAddress(Locale.getDefault());
					result.setFeatureName(place.getName() != null ? place.getName() : "");
					result.setLatitude(place.getLatitude());
					result.setLongitude(place.getLongitude());
					
					if (result.getExtras() == null) {
						result.setExtras(new Bundle());
					}
					
//					result.getExtras().putString("formatted_address", place.getFormattedAddress());
					result.setAddressString(place.getFormattedAddress());
					result.setValid();
					
					c.onAddressValidated(result);
				}
				
				@Override
				public void onFailure(Throwable t) {
					showErrorDialog("Routy couldn't understand \"" + locationQuery + "\".  Please try something a little different.");		// TODO extract to strings.xml
				}
				
				@Override
				public void onNoSelection() {
					//Do nothing?
				}
			}.execute(new GooglePlacesQuery(locationQuery, centerLat, centerLng));
		}
	}


	/**
	 * Called when the "Find Me" button is tapped.
	 * 
	 * @param view
	 */
	public void findUserLocation(View view) {
		Log.v(TAG, "locating user");
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(click, volume, volume, 1, 0, 1);  

		Location deviceLocation = getGoodDeviceLocation();
		if (deviceLocation != null) {
			// Reverse geocode the lat/lng in DeviceLocationModel
			new ReverseGeocodeTask(context, true, true, new ReverseGeocodeListener() {
				@Override
				public void onResult(RoutyAddress address) {
					loadReverseGeocodedOrigin(address);
				}
			}).execute(deviceLocation);
		} else {
			locate();
		}
	}
	
	
	private Location getGoodDeviceLocation() {
		// TODO Check accuracy, check time
		Location deviceLocation = DeviceLocationModel.getSingleton().getDeviceLocation();
		if (deviceLocation == null) {
			return null;
		}
		
		if ((deviceLocation.getTime() > (System.currentTimeMillis() - 300000)) && (deviceLocation.getAccuracy() <= AppProperties.USER_LOCATION_ACCURACY_THRESHOLD_M)) {
			return deviceLocation;
		}
		
		return null;
	}
	
	
	private void loadReverseGeocodedOrigin(RoutyAddress address) {
		if (address != null) {
			Log.v(TAG, "got user location: " + address.getAddressLine(0));
			
			RoutyAddress origin = address;
			if (origin.getExtras() == null) {
				origin.setExtras(new Bundle());
			}
			
			String addressStr = origin.getAddressString();
			origin.setValid();
			addressModel.setOrigin(origin);
			originAddressField.setText(addressStr);		//TODO this should be a call to refreshOriginLayout()
		}
	}


	/**
	 * Kicks off a {@link FindUserLocationTask} to try and obtain the user's location.
	 */
	void locate() {
		new FindUserLocationTask(this, true, new FindUserLocationListener() {
			
			@Override
			public void onUserLocationFound(Location userLocation) {
				new ReverseGeocodeTask(context, true, true, new ReverseGeocodeListener() {
					
					@Override
					public void onResult(RoutyAddress address) {
						loadReverseGeocodedOrigin(address);
					}
				}).execute(userLocation);
			}
			
			@Override
			public void onTimeout(GpsNotEnabledException e) {
				Log.e(TAG, "getting user location timed out and gps was " + (e == null ? "not enabled" : "enabled"));
				
				showErrorDialog(getResources().getString(R.string.locating_fail_error));
			}
			
			@Override
			public void onFailure(Throwable t) {
				Log.e(TAG, "failed getting user location");
				try {
					throw t;
				} catch (NoLocationProviderException e) {
					Log.e(TAG, "GPS was disabled, going to ask the user to enable it and then try again");
					showEnableGpsDialog();
				} catch (Throwable e) {
					Log.e(TAG, "don't know why we couldn't obtain a location...");
					showErrorDialog(getResources().getString(R.string.locating_fail_error));
				}
			}
		}).execute(0);
	}
	


	
	public void acceptDestinations(View v){
		routeIt();
	}
	
	/**
	 * Called when "Route It!" is clicked.  Does any final validation and preparations before calculating 
	 * the best route and passing route data to the results activity.
	 * 
	 * @param v
	 */
	public void routeIt() {
		Log.v(TAG, "route requested");
		
		if (addressModel.getOrigin() == null || addressModel.getOrigin().getAddressString().length() == 0) {
			Log.v(TAG, "no origin");
		} else if (!addressModel.getOrigin().isValid()) {
			//Validate the origin before continuing
			validateAddress(addressModel.getOrigin().getAddressString(), null, null, new ValidateAddressCallback() {
				@Override
				public void onAddressValidated(RoutyAddress validatedAddress) {
					addressModel.setOrigin(validatedAddress);
					refreshOriginLayout();
				}
			});
		}
		
		Log.v(TAG, "out");
		assert addressModel.getOrigin().isValid();
		
		if (addressModel.hasDestinations()) {
			volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(click, volume, volume, 1, 0, 1);
			
			for (int i = 0; i < addressModel.getDestinations().size(); i++) {
				RoutyAddress dest = addressModel.getDestinations().get(i);
				if (!dest.isValid()) {
					//Validate the destination
					final int idx = i;
					validateAddress(dest.getAddressString(), addressModel.getOrigin().getLatitude(), addressModel.getOrigin().getLongitude(), new ValidateAddressCallback() {
						
						@Override
						public void onAddressValidated(RoutyAddress validatedAddress) {
							addressModel.setDestinationAt(idx, validatedAddress);
						}
					});
					
					assert addressModel.getDestinations().get(i).isValid();
				}
			}
			
			new CalculateRouteTask(this) {
				@Override
				public void onRouteCalculated(Route route) {
					// Call ResultsActivity activity
					Intent resultsIntent = new Intent(getBaseContext(), ResultsActivity.class);
					resultsIntent.putExtra("addresses", (ArrayList<Address>) route.getAddresses());
					resultsIntent.putExtra("distance", route.getTotalDistance());
					resultsIntent.putExtra("optimize_for", routeOptimized);
					startActivity(resultsIntent);
				}
			}.execute(new RouteRequest(addressModel.getOrigin(), addressModel.getDestinations(), false, routeOptimized));
		} else {
			Log.e(TAG, "trying to build a route with no destinations!");
			// No destinations entered
			volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(bad, volume, volume, 1, 0, 1);
			showErrorDialog("Please enter at least one destination to continue.");
		}

	}
	
	
	public void onToggleClicked(boolean on) {
		Log.v(TAG, "route optimize preference changed!");
		if (on) {
			routeOptimized = RouteOptimizePreference.PREFER_DURATION;
		} 
		else {
			routeOptimized = RouteOptimizePreference.PREFER_DISTANCE;
		}
	}
	
	
	/**
	 * Displays an {@link AlertDialog} with one button that dismisses the dialog.  Use this to display error messages 
	 * to the user.
	 * 
	 * @param message
	 */
	private void showErrorDialog(String message) {
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(bad, volume, volume, 1, 0, 1);
		OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.error_message_title), message) {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		dialog.show(context.getFragmentManager(), TAG);
	}


	/**
	 * Displays an alert asking the user if they would like to go to the device's Location Settings to enable GPS.
	 * 
	 * @param message
	 */
	private void showEnableGpsDialog() {
		TwoButtonDialog dialog = new TwoButtonDialog(getResources().getString(R.string.error_message_title), getResources().getString(R.string.enable_gps_prompt)) {

			@Override
			public void onRightButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();

				//Show the "Location Services" settings page
				Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				Log.v(TAG, "ENABLE_GPS_REQUEST = " + ENABLE_GPS_REQUEST);
				context.startActivityForResult(gpsIntent, ENABLE_GPS_REQUEST);

			}

			@Override
			public void onLeftButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
				showErrorDialog(getResources().getString(R.string.locating_fail_error));
			}
		};
		dialog.show(context.getFragmentManager(), TAG);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.v(TAG, "onStart()");
	}

	
	@Override
	protected void onResume() {   
		super.onResume(); 

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0); 
		speak = sounds.load(this, R.raw.routyspeak, 1);  
		bad = sounds.load(this, R.raw.routybad, 1);
		click = sounds.load(this, R.raw.routyclick, 1);
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.v(TAG, "onStop()");
	}


	@Override
	public void onPause() {
		super.onPause();

		if(sounds != null) { 
			sounds.release(); 
			sounds = null; 
		} 
	}


	private void saveOrigin() {
		if (addressModel.getOrigin() != null && originActivityPrefs != null) {
			Log.v(TAG, "saving Origin");
			
			String json = addressModel.getOriginJSON();
			Log.v(TAG, "Origin JSON: " + json);
			
			SharedPreferences.Editor ed = originActivityPrefs.edit();
			ed.putString(SAVED_ORIGIN_JSON_KEY, json);
			ed.commit();
		} else if (originActivityPrefs == null) {
			Log.e(TAG, "originActivityPrefs null while trying to save the origin");
		}
	}
	
	private void saveDestinations() {
		Log.v(TAG, "saving destinations list");
		if (originActivityPrefs == null) {
			Log.e(TAG, "originActivityPrefs null while trying to save destinations");
		} else {
			String json = "";
			if (addressModel.hasDestinations()) {
				json = addressModel.getDestinationsJSON();
			}
			
			Log.v(TAG, "Destinations JSON: " + json);
			
			SharedPreferences.Editor ed = originActivityPrefs.edit();
			ed.putString(SAVED_DESTS_JSON_KEY, json);
			ed.commit();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.v(TAG, "onDestroy()");
		
		if (addressModel.getOrigin() == null) {
			Log.v(TAG, "pausing with no origin");
		} else {
			Log.v(TAG, "pausing with origin: " + AddressModel.getSingleton().getOrigin().getAddressString());
		}
		
		saveOrigin();
		saveDestinations();
	}
	
	
	/*@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return ;
	}*/
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
