package org.routy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import junit.framework.Assert;

import org.routy.exception.GpsNotEnabledException;
import org.routy.exception.NoLocationProviderException;
import org.routy.fragment.OneButtonDialog;
import org.routy.fragment.TwoButtonDialog;
import org.routy.listener.FindUserLocationListener;
import org.routy.listener.ReverseGeocodeListener;
import org.routy.model.AddressModel;
import org.routy.model.AppProperties;
import org.routy.model.GooglePlace;
import org.routy.model.GooglePlacesQuery;
import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.model.RouteRequest;
import org.routy.task.CalculateRouteTask;
import org.routy.task.FindUserLocationTask;
import org.routy.task.GooglePlacesQueryTask;
import org.routy.task.ReverseGeocodeTask;
import org.routy.view.DestinationRowView;

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
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OriginActivity extends FragmentActivity {

	private static final String TAG = "OriginActivity";
	private static final int ENABLE_GPS_REQUEST = 1;
	private final String SAVED_DESTS_JSON_KEY = "saved_destination_json";

	private FragmentActivity context;
	private AddressModel addressModel;

	private EditText originAddressField;
	private Address origin;
	private LinearLayout destLayout;
	private Button addDestButton;
	private boolean originValidated;		// true if the origin was obtained using geolocation (not user entry)

	
	// shared prefs for origin and destination persistence
	private SharedPreferences originActivityPrefs;


	private SoundPool sounds;
	private int bad;
	private int speak;
	private int click;
	private AudioManager audioManager;
	private float volume;
	private Switch preferenceSwitch;
	RouteOptimizePreference routeOptimized;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_origin);
		
		// Audio stuff
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

		// Initializations
		context 			= this;
		addressModel = AddressModel.getSingleton();
		
		addDestButton = (Button) findViewById(R.id.button_destination_add_new);
		// Get the layout containing the list of destination
		destLayout = (LinearLayout) findViewById(R.id.LinearLayout_destinations);
		
		originAddressField 	= (EditText) findViewById(R.id.origin_address_field);
		originAddressField.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				originValidated = false;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// nothing
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// nothing
			}
			// TODO: on focus lost, validate origin
		});
		
		origin				= null;
		originActivityPrefs = getSharedPreferences("origin_prefs", MODE_PRIVATE);
		originValidated		= false;

		restoreSavedOrigin(savedInstanceState);
		restoreSavedDestinations(savedInstanceState);
		
		routeOptimized = RouteOptimizePreference.PREFER_DISTANCE;
		preferenceSwitch = (Switch) findViewById(R.id.toggleDistDur);
		preferenceSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onToggleClicked(isChecked);
			}
		});
		
		showInstructions();

	}


	private void showInstructions() {		
		// First-time user dialog cookie
		boolean noobCookie = originActivityPrefs.getBoolean("noob_cookie", false);
		if (!noobCookie){
			showNoobDialog();
			userAintANoobNomore();
		}
	}


	private void restoreSavedOrigin(Bundle savedInstanceState) {
		// get stored origin address from shared prefs first...if there isn't one, THEN try savedInstanceState
		originValidated = originActivityPrefs.getBoolean("origin_validated", false);
		
		if (!originValidated) {
			Log.v(TAG, "origin was not validated last time...just restoring the string");
			String savedOriginString = originActivityPrefs.getString("saved_origin_string", null);
			originAddressField.setText(savedOriginString);
			
		} else {
			String savedOriginJson = originActivityPrefs.getString("saved_origin", null);
			if (savedOriginJson != null && savedOriginJson.length() > 0) {
				Log.v(TAG, "building origin address from json: " + savedOriginJson);
				origin = Util.readAddressFromJson(savedOriginJson);
				
				if (origin != null) {
					Log.v(TAG, "got the restored origin address from sharedprefs");
					
					Bundle extras = origin.getExtras();
					String addressStr = null;
					if (extras != null) {
						addressStr = extras.getString("formatted_address");
					} else {
						addressStr = String.format("%f, %f", origin.getLatitude(), origin.getLongitude());
					}
					
					originAddressField.setText(addressStr);
					
					SharedPreferences.Editor ed = originActivityPrefs.edit();
					ed.remove("saved_origin");
					ed.commit();
				}
			}
			
			if (origin == null && savedInstanceState != null) {
				// Get the origin as an Address object
				origin = (Address) savedInstanceState.get("origin_address");
				if (origin != null) {
					Log.v(TAG, "got the restored origin address from instance state");
				}
			}
		}
	}
	
	
	// Initialize destination shared preferences
	private void restoreSavedDestinations(Bundle savedInstanceState) {
		originActivityPrefs = getSharedPreferences("activity_prefs", MODE_PRIVATE);
		String storedAddressesJson = originActivityPrefs.getString(SAVED_DESTS_JSON_KEY, null);
	
		if (storedAddressesJson != null && storedAddressesJson.length() > 0) {
			List<Address> restoredAddresses = Util.jsonToAddressList(storedAddressesJson);
	
			for (int i = 0; i < restoredAddresses.size(); i++) {
				Address address = restoredAddresses.get(i);
				Bundle addressExtras = address.getExtras();
	
				if (addressExtras != null) {
					int status = addressExtras.getInt("valid_status");
	
					DestinationRowView newRow = null;
					if (status == DestinationRowView.VALID) {
						// Put the new row in the list
						newRow = addDestinationRow(address.getFeatureName());
						newRow.setAddress(address);
						newRow.setValid();
	
						Log.v(TAG, "restored: " + newRow.getAddress().getFeatureName() + " [status=" + newRow.getStatus() + "]");
	
					} else if (status == DestinationRowView.INVALID || status == DestinationRowView.NOT_VALIDATED) {
						String addressString = addressExtras.getString("address_string");
	
						newRow = addDestinationRow(addressString);
	
						if (status == DestinationRowView.INVALID) {
							newRow.setInvalid();
						} else {
							newRow.clearValidationStatus();
						}
	
						Log.v(TAG, "restored: " + newRow.getAddressString() + " [status=" + newRow.getStatus() + "]");
					}
	
				}
			}
		} else {
			addDestinationRow();
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
		// TODO: This was in DA but not in OA, do we need it?
		//		@Override
		//		public void onButtonClicked(DialogInterface dialog, int which) {
		//			dialog.dismiss();
		//		}
		dialog.show(context.getSupportFragmentManager(), TAG);
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ENABLE_GPS_REQUEST:
			locate();
			break;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_origin, menu);
		return true;
	}
	
	
	/**
	 * Validates the origin address.  If it's good, it gets packaged into an Intent and sent to 
	 * the DestinationActivity screen.
	 * 
	 * @param view
	 */
	public void validateOrigin(View view) {
		// validate the origin address and store it
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(click, volume, volume, 1, 0, 1);  

		final String locationQuery = originAddressField.getText().toString();
		
		if (!originValidated && (locationQuery == null || locationQuery.length() == 0)) {
			showErrorDialog(getResources().getString(R.string.no_origin_address_error));
		} else {
			if (!originValidated) {
				Log.v(TAG, "origin was user-entered and needs to be validated");
				
				// use GooglePlacesQueryTask to do this...
				new GooglePlacesQueryTask(this) {
					
					@Override
					public void onResult(GooglePlace place) {
						// make an Address out of the Google place and start the DestinationActivity
						origin = new Address(Locale.getDefault());
						origin.setFeatureName(place.getName());
						origin.setLatitude(place.getLatitude());
						origin.setLongitude(place.getLongitude());
						
						Bundle extras = new Bundle();
						extras.putString("formatted_address", place.getFormattedAddress());
						extras.putInt("valid_status", DestinationRowView.VALID);
						origin.setExtras(extras);
						
						originAddressField.setText(place.getFormattedAddress());	// TODO set the text in the edittext field
						originValidated = true;
						
					}
					
					@Override
					public void onFailure(Throwable t) {
						showErrorDialog("Routy couldn't understand \"" + locationQuery + "\".  Please try something a little different.");		// TODO extract to strings.xml
					}
					
					@Override
					public void onNoSelection() {
						// TODO do nothing?
						
					}
				}.execute(new GooglePlacesQuery(locationQuery, null, null));
			}
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

		locate();
	}
	
	
	/**
	 * Kicks off a {@link FindUserLocationTask} to try and obtain the user's location.
	 */
	void locate() {
		new FindUserLocationTask(this, new FindUserLocationListener() {
			
			/*@Override
			public void onUserLocationFound(Address userLocation) {
				if (userLocation != null) {
					Log.v(TAG, "got user location: " + userLocation.getAddressLine(0));
					
					origin = userLocation;
					
					Bundle extras = origin.getExtras();
					if (extras == null) {
						Log.e(TAG, "origin extras is null");
					}
					extras.putInt("valid_status", DestinationRowView.VALID);
					origin.setExtras(extras);
					
					String addressStr = origin.getExtras().getString("formatted_address");
					
					originAddressField.setText(addressStr);
					originValidated = true;
				}
			}*/
			
			@Override
			public void onUserLocationFound(Location userLocation) {
				new ReverseGeocodeTask(context, true, new ReverseGeocodeListener() {
					
					@Override
					public void onResult(Address address) {
						if (address != null) {
							Log.v(TAG, "got user location: " + address.getAddressLine(0));
							
							origin = address;
							
							Bundle extras = origin.getExtras();
							if (extras == null) {
								Log.e(TAG, "origin extras is null");
							}
							extras.putInt("valid_status", DestinationRowView.VALID);
							origin.setExtras(extras);
							
							String addressStr = origin.getExtras().getString("formatted_address");
							
							originAddressField.setText(addressStr);
							originValidated = true;
						}
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
	
	
	/**
	 * Adds a {@link DestinationRowView} to the Destinations list.
	 * @param address
	 * @return			the row that was added, or null if no row was added
	 */
	DestinationRowView addDestinationRow() {
		return addDestinationRow("");
	}
	
	
	/**
	 * Adds a {@link DestinationRowView} to the Destinations list.
	 * @param address
	 * @return			the row that was added, or null if no row was added
	 */
	DestinationRowView addDestinationRow(String address) {
		
		// TODO: if origin isn't validated, prompt user to do that first
		
		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			DestinationRowView v = new DestinationRowView(context, address) {

				@Override
				public void onRemoveClicked(UUID id) {
					removeDestinationRow(id);
				}

				// The user tapped on a different row and this row lost focus
				@Override
				public void onFocusLost(final UUID id) {
					final DestinationRowView row = getRowById(id);

					Log.v(TAG, "FOCUS LOST row id=" + row.getUUID() + ": " + row.getAddressString() + " valid status=" + row.getStatus());

					// The user tapped on a different text box.  Do validation if necessary, but don't add an additional row.
					if (row.getAddressString() != null && row.getAddressString().length() > 0) {
						if (row.getStatus() == DestinationRowView.INVALID || row.getStatus() == DestinationRowView.NOT_VALIDATED) {
							new GooglePlacesQueryTask(context) {

								@Override
								public void onResult(GooglePlace place) {
									if (place != null && place.getAddress() != null) {
										row.setAddress(place.getAddress());
										row.setValid();

										if ((getRowIndexById(id) == destLayout.getChildCount() - 1) && (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS - 1)) {
											Log.v(TAG, "place validated...showing add button");
											showAddButton();
										} else {
											Log.v(TAG, "place validated...hiding add button");
											hideAddButton();
										}
									} else {
										row.setInvalid();
									}
								}
								
								@Override
								public void onFailure(Throwable t) {
									showErrorDialog("Routy couldn't understand \"" + row.getAddressString() + "\".  Please try something a little different.");		// TODO extract to strings.xml
								}

								@Override
								public void onNoSelection() {
									// Doing nothing leaves it NOT_VALIDATED
								}
							}.execute(new GooglePlacesQuery(row.getAddressString(), origin.getLatitude(), origin.getLongitude()));
						}
					}
				}
			};

			destLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			v.focusOnAddressField();

			/*if (destLayout.getChildCount() == AppProperties.NUM_MAX_DESTINATIONS) {
				addDestButton.setVisibility(View.INVISIBLE);
			}*/

			return v;
		} else {
			/*volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			sounds.play(bad, 1, 1, 1, 0, 1);

			showErrorDialog("Routy is all maxed out at " + AppProperties.NUM_MAX_DESTINATIONS + " destinations for now.");*/

			return null;
		}
	}
	
	
	public void onAddDestinationClicked(View v) {
		Log.v(TAG, "new destination row requested by user");
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(click, volume, volume, 1, 0, 1);

		// If the last row is not empty, add a new row
		DestinationRowView lastRow = (DestinationRowView) destLayout.getChildAt(destLayout.getChildCount() - 1);
		if (lastRow.getAddressString() != null && lastRow.getAddressString().length() > 0) {
			if (lastRow.needsValidation()) {
				// Validate the last row if it has not been validated.  Otherwise, it puts the new row up first, and then validates due to focusChanged.
				Log.v(TAG, "validating last row before adding new one");
				final DestinationRowView r = lastRow;

				// disable the onFocusLost listener just once so it doesn't try to validate twice here
				r.disableOnFocusLostCallback(true);

				// do the validation
				new GooglePlacesQueryTask(context) {

					@Override
					public void onResult(GooglePlace place) {
						if (place != null && place.getAddress() != null) {
							r.setAddress(place.getAddress());
							r.setValid();

							Log.v(TAG, "adding a new destination row");
							addDestinationRow();
						} else {
							r.setInvalid();
						}

						// If the list is full, hide the add button
						/*if (destLayout.getChildCount() == AppProperties.NUM_MAX_DESTINATIONS) {
							addDestButton.setVisibility(View.INVISIBLE);
						}*/
					}
					
					@Override
					public void onFailure(Throwable t) {
						showErrorDialog("Routy couldn't understand \"" + r.getAddressString() + "\".  Please try something a little different.");		// TODO extract to strings.xml
					}

					@Override
					public void onNoSelection() {
						// Doing nothing leaves it NOT_VALIDATED
					}
				}.execute(new GooglePlacesQuery(lastRow.getAddressString(), origin.getLatitude(), origin.getLongitude()));
			} else {
				if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
					Log.v(TAG, "adding a new destination row");
					addDestinationRow();
				} else {
					volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
					volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
					sounds.play(bad, 1, 1, 1, 0, 1);

					showErrorDialog("Routy is all maxed out at " + AppProperties.NUM_MAX_DESTINATIONS + " destinations for now.");
				}
			}
		}
	}
	
	
	/**
	 * Removes the EditText and "remove" button row ({@link DestinationRowView}} with the given {@link UUID} from the screen.
	 * @param id
	 */
	void removeDestinationRow(UUID id) {
		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(click, volume, volume, 1, 0, 1);

		int idx = 0;
		if (destLayout.getChildCount() > 1) {
			idx = getRowIndexById(id);

			if (idx >= 0) {
				Log.v(TAG, "Remove DestinationAddView id=" + id);
				destLayout.removeViewAt(idx);
			} else {
				Log.e(TAG, "attempt to remove a row that does not exist -- id=" + id);
			}
		} else if (destLayout.getChildCount() == 1) {
			idx = getRowIndexById(id);		// Should be 0 all the time

			((DestinationRowView) destLayout.getChildAt(idx)).reset();
		}

		// set focus on the row idx one less than idx
		((DestinationRowView) destLayout.getChildAt(Math.max(0, idx - 1))).focusOnAddressField();

		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			addDestButton.setVisibility(View.VISIBLE);
		}
	}
	
	
	private DestinationRowView getRowById(UUID id) {
		int idx = getRowIndexById(id);
		return (DestinationRowView) destLayout.getChildAt(idx);
	}
	
	
	private int getRowIndexById(UUID id) {
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			if (((DestinationRowView) destLayout.getChildAt(i)).getUUID().equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Called when "Route It!" is clicked.  Does any final validation and preparations before calculating 
	 * the best route and passing route data to the results activity.
	 * 
	 * @param v
	 */
	public void acceptDestinations(final View v) {
		Log.v(TAG, "Validate destinations and calculate route if they're good.");

		volume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		volume = volume / audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		sounds.play(click, volume, volume, 1, 0, 1);

		// Go through the list until you find one that is INVALID or NOT_VALIDATED
		List<Address> validAddresses = new ArrayList<Address>();
		boolean hasDestinations = false;
		boolean hasError = false;
		DestinationRowView row = null;
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			row = (DestinationRowView) destLayout.getChildAt(i);

			if (row.getAddressString() != null && row.getAddressString().length() > 0) {
				if (row.getStatus() == DestinationRowView.INVALID || row.getStatus() == DestinationRowView.NOT_VALIDATED) {
					hasDestinations = true;
					hasError = true;
					Log.v(TAG, "row id=" + row.getUUID() + " has valid status=" + row.getStatus());
					break;
				} else {
					hasDestinations = true;
					validAddresses.add(row.getAddress());
				}
			}
		}

		if (hasDestinations) {
			// If you encountered an "error" take steps to validate it.  When it's done validating, call acceptDestinations again.
			if (hasError && row != null) {
				Log.v(TAG, "The destinations list has a row (id=" + row.getUUID() + ") in need of validation.");
				// Validate "row"
				final DestinationRowView r = row;
				new GooglePlacesQueryTask(context) {

					@Override
					public void onResult(GooglePlace place) {
						if (place != null && place.getAddress() != null) {
							r.setAddress(place.getAddress());
							r.setValid();

							acceptDestinations(v);
						} else {
							r.setInvalid();

							// TODO Show an error message: couldn't match the query string to a place or address
						}
					}
					
					@Override
					public void onFailure(Throwable t) {
						showErrorDialog("Routy couldn't understand \"" + r.getAddressString() + "\".  Please try something a little different.");		// TODO extract to strings.xml
					}

					@Override
					public void onNoSelection() {
						// Doing nothing leaves it NOT_VALIDATED
					}
				}.execute(new GooglePlacesQuery(row.getAddressString(), origin.getLatitude(), origin.getLongitude()));
			} else {
				Log.v(TAG, "All destinations have been validated.");

				// If everything is valid, move on to the Results screen
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
				}.execute(new RouteRequest(origin, validAddresses, false, 
						routeOptimized/* ? RouteOptimizePreference.PREFER_DISTANCE : RouteOptimizePreference.PREFER_DURATION*/));
			}
		} else {
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
	
	
	public void showAddButton() {
		addDestButton.setVisibility(View.VISIBLE);
	}

	
	public void hideAddButton() {
		addDestButton.setVisibility(View.INVISIBLE);
	}

	
	/**
	 *  Saves the validated origin in shared preferences, saves user time when using Routy next.
	 *//*
	private void saveOriginInSharedPrefs() {
		// TODO onDestroy needs to save the origin address OBJECT in sharedprefs
		
		SharedPreferences.Editor ed = originActivityPrefs.edit();
		ed.putString("saved_origin_address", "");
		ed.putString("saved_origin_string", originAddressField.getText().toString());
		ed.commit();	
	}*/


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
		//TODO: This was in DA but not OA, do we need it?
//		OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.error_message_title), message) {
//			@Override
//			public void onButtonClicked(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		};
		dialog.show(context.getSupportFragmentManager(), TAG);
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

				// Show the "Location Services" settings page
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
		dialog.show(context.getSupportFragmentManager(), TAG);
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
	public void onPause() {
		super.onPause();

		if(sounds != null) { 
			sounds.release(); 
			sounds = null; 
		} 
		
		Log.v(TAG, "building the JSON string from all the destination addresses");
		List<Address> addressesToSave = new ArrayList<Address>();
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			DestinationRowView destView = (DestinationRowView) destLayout.getChildAt(i);

			if (destView.getAddressString() != null && destView.getAddressString().length() > 0) {
				Address address = null;
				if (destView.getStatus() == DestinationRowView.VALID) {
					// Add the address from this row to the list
					address = destView.getAddress();

					// It's valid so we'll just keep track of that
					Bundle extras = address.getExtras();
					extras.putInt("valid_status", destView.getStatus());
				} else if (destView.getStatus() == DestinationRowView.INVALID || destView.getStatus() == DestinationRowView.NOT_VALIDATED) {
					// Manually create an Address object with just the EditText value and the status because it won't be there
					address = new Address(Locale.getDefault());

					// Since it'll need to be validated when we come back, we need to save what was in the EditText with the status
					Bundle extras = new Bundle();
					extras.putString("address_string", destView.getAddressString());
					extras.putInt("valid_status", destView.getStatus());
					address.setExtras(extras);
				} else {
					// bah!
					throw new IllegalStateException("Destination row " + i + " had an invalid status value of: " + destView.getStatus());
				}

				Assert.assertNotNull(address);
				addressesToSave.add(address);
			}
		}

		String json = Util.addressListToJSON(addressesToSave);
		Log.v(TAG, "saved destinations json: " + json);

		// Put the storedAddresses into shared prefs via a set of strings
		Log.v(TAG, "Saving destinations in shared prefs");
		SharedPreferences.Editor ed = originActivityPrefs.edit();
		ed.putString(SAVED_DESTS_JSON_KEY, json);
		//		ed.putStringSet("saved_destination_strings", storedAddresses);
		ed.commit();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (!originValidated && originActivityPrefs != null) {
			// save the origin input string
			Log.v(TAG, "origin not validated, saving just the input string");
			SharedPreferences.Editor ed = originActivityPrefs.edit();
			ed.putString("saved_origin_string", originAddressField.getText().toString());
			ed.putBoolean("origin_validated", originValidated);
			ed.commit();
			
		} else if (origin != null && originActivityPrefs != null) {
			// store origin in shared prefs
			Log.v(TAG, "saving origin to SharedPreferences");
			String json = Util.writeAddressToJson(origin);
			SharedPreferences.Editor ed = originActivityPrefs.edit();
			ed.putString("saved_origin", json);
			Log.v(TAG, "saving json: " + json);
			ed.putBoolean("origin_validated", originValidated);
			ed.commit();
		}
		
		
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelable("origin_address", origin);
		outState.putBoolean("origin_validated", originValidated);
		Log.v(TAG, "saved the origin object");
	}
}
