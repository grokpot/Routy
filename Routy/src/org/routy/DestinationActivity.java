package org.routy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.routy.adapter.PlacesListAdapter;
import org.routy.exception.RoutyException;
import org.routy.fragment.ListPickerDialog;
import org.routy.fragment.OneButtonDialog;
import org.routy.model.AppProperties;
import org.routy.model.CalculateRouteRequest;
import org.routy.model.GooglePlace;
import org.routy.model.Route;
import org.routy.model.ValidateDestinationRequest;
import org.routy.task.CalculateRouteTask;
import org.routy.task.ValidateDestinationTask;
import org.routy.view.DestinationInputRow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DestinationActivity extends FragmentActivity {
	
	
	private final String TAG = "DestinationActivity";
	
	private FragmentActivity mContext;
	private Address origin;
	private LinearLayout destLayout;
	private Button routeItButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_destination);
		
		mContext = this;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			origin = (Address) extras.get("origin");
			Assert.assertNotNull(origin);
			
			Log.v(TAG, "Origin address: " + origin.getExtras().getString("formatted_address"));			// We need to pass the whole Address object if possible
			TextView originText = (TextView) findViewById(R.id.textview_destinations_origin);
			originText.setText("Starting from: \n" + origin.getExtras().getString("formatted_address"));
		}
		
		destLayout = (LinearLayout) findViewById(R.id.LinearLayout_destinations);
		routeItButton = (Button) findViewById(R.id.button_route_it);
		
		addDestinationEntryRow();
		
		// XXX temp "Test defaults"
        Button buttonTestDefaults = (Button) findViewById(R.id.button_test_defaults);
        buttonTestDefaults.setText("Test Default Destinations");
        buttonTestDefaults.setOnClickListener(listenerTestDefaults);
	}
	
	
	// XXX temp
	/**
	 * Loads the 3 test destinations we've been using.
	 */
	View.OnClickListener listenerTestDefaults = new View.OnClickListener() {
        public void onClick(View v) {
        	if (destLayout.getChildCount() < 3) {
        		for (int i = destLayout.getChildCount(); i < 3; i++) {
        			addDestinationEntryRow();
        		}
        	}
        	
        	DestinationInputRow view = (DestinationInputRow) destLayout.getChildAt(0);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_1));
        	
        	view = (DestinationInputRow) destLayout.getChildAt(1);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_2));
        	
        	view = (DestinationInputRow) destLayout.getChildAt(2);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_3));
        }
    };
	
	
    /**
     * Puts another destination entry row on the screen.
     */
	void addDestinationEntryRow() {
		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			DestinationInputRow v = new DestinationInputRow(mContext) {

				@Override
				public void onRemoveClicked(UUID id) {
					removeDestinationEntryRow(id);
				}

				@Override
				public void onAddClicked(UUID id) {
					// TODO validate this destination row
					Log.v(TAG, "Add clicked from id=" + id);
					validateDestination(id);
					addDestinationEntryRow();
				}
			};
			destLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}

	
	
	/**
	 * Removes the destination entry row ({@link DestinationInputRow}) with the given {@link UUID} from the screen.
	 * @param id
	 */
	void removeDestinationEntryRow(UUID id) {
		// Go through the views list and remove the one that has the given UUID
		
		// TODO check out views.remove(Object o) and see how it compares objects to find the right one
		Log.v(TAG, "Remove DestinationAddView id=" + id);
		
		if (destLayout.getChildCount() > 1) {
			for (int i = 0; i < destLayout.getChildCount(); i++) {
				if (((DestinationInputRow) destLayout.getChildAt(i)).getUUID().equals(id)) {
					destLayout.removeViewAt(i);
					break;
				}
			}
			
			// Re-enable the "+" button on the last row, so they can add more if they want
			((DestinationInputRow) destLayout.getChildAt(destLayout.getChildCount() - 1)).resetButtons();
		} else {
			((DestinationInputRow) destLayout.getChildAt(0)).clear();
		}
	}
	
	
	/**
	 * Adds a destination entry {@link DestinationInputView} row to the list if we're not maxed out.  Max number 
	 * of rows is set in {@link AppProperties}.
	 * @param v
	 *//*
	public void addAnotherDestination(View v) {
		Log.v(TAG, "Add another destination.");
		
		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			addDestinationInputView();
		}
	}*/
	
	
	/**
	 * Validate a single row in the destinations list.
	 * @param id
	 */
	// TODO Show a loading spinner while it's validating.
	private void validateDestination(UUID id) {
		final DestinationInputRow row = getRowById(id);
		
		if (row != null) {
			validateDestination(row);
		} else {
			Log.e(TAG, "Couldn't find a row for id=" + id);
		}
	}
	
	
	private void validateDestination(final DestinationInputRow row) {
		/*if (row.getStatus() == DestinationInputRow.INVALID || row.getStatus() == DestinationInputRow.NOT_VALIDATED) {
			String addressString = row.getAddressString();
			Log.v(TAG, "validating: " + addressString);
			
			if (addressString != null && addressString.length() > 0) {
				// Doing this in an AsyncTask because it uses the network.
				new ValidateDestinationTask() {
					
					@Override
					public void onResult(List<GooglePlace> results) {
						if (results == null || results.size() < 1) {
							// No results.  Display a message.
							Log.v(TAG, "No places found for query");
							row.setInvalid();
							showErrorDialog("No places or addresses found for this destination.  Try broadening your search.");
						} else if (results.size() == 1) {
							// Only one result.  Turn it into an address, set it, and set the valid status
							Log.v(TAG, "1 place found for query");
							row.setAddress(results.get(0).getAddress());
							row.setValid();
						} else {
							// More than 1 result.  Display the pickable list dialog.
							Log.v(TAG, "More than 1 place found for query -- " + results.size() + " results");
							showPlacePickerDialog(row, results);
						}
					}

					@Override
					public void onFailure(RoutyException exception) {
						showErrorDialog(exception.getMessage());
					}
				}.execute(new ValidateDestinationRequest(addressString, origin.getLatitude(), origin.getLongitude(), AppProperties.G_PLACES_SEARCH_RADIUS_M));
			}
		}*/
	}
	
	
	private int getRowIndexById(UUID id) {
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			if (((DestinationInputRow) destLayout.getChildAt(i)).getUUID().equals(id)) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	private DestinationInputRow getRowById(UUID id) {
		int idx = getRowIndexById(id);
		
		if (idx < 0) {
			return null;
		} else {
			return (DestinationInputRow) destLayout.getChildAt(idx);
		}
	}
	
	
	/**
	 * Called when "Route It!" button is clicked.
	 * @param v
	 */
	public void acceptDestinations(View v) {
		Log.v(TAG, "Validate destinations and calculate route if they're good.");
		v.requestFocus();
		
		boolean hasErrors = false;
		List<Address> validAddresses = new ArrayList<Address>();
		// Check if there are any invalid rows
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			DestinationInputRow row = (DestinationInputRow) destLayout.getChildAt(i);
			Log.v(TAG, "Destination " + i + ": " + row.getAddressString());
			
			if (row.getAddressString() != null && row.getAddressString().length() > 0) {
				if (row.getStatus() == DestinationInputRow.NOT_VALIDATED) {
//					row.validate();
					validateDestination(row);
				}
				
				// TODO this will continue processing as the async task goes off and does the validation.  Need to control this.
				
				if (row.getStatus() == DestinationInputRow.INVALID) {
					hasErrors = true;
				} else {
					Log.v(TAG, row.getAddressString() + " validated");
					validAddresses.add(row.getAddress());
				}
			}
		}
		
		if (!hasErrors) {
			if (validAddresses.size() == 0) {
				showErrorDialog("Please enter at least 1 destination to continue.");
			} else {
				Log.v(TAG, "All addresses validated!");
				CalculateRouteTask task = new CalculateRouteTask() {
					
					@Override
					public void onRouteCalculated(Route route) {
						Toast.makeText(mContext, getString(R.string.routed), Toast.LENGTH_LONG).show();	// XXX temp
						
						// Call ResultsActivity activity
		    			Intent resultsIntent = new Intent(getBaseContext(), ResultsActivity.class);
		    			resultsIntent.putExtra("addresses", (Serializable) route.getAddresses());
		    			resultsIntent.putExtra("distance", route.getTotalDistance());
		    			startActivity(resultsIntent);
					}
				};
				
				task.execute(new CalculateRouteRequest(origin, validAddresses, false));
			}
		} else {
			Log.e(TAG, "Errors found in destinations.");
			showErrorDialog("The addresses in red are invalid.  Try being more specific.");
		}
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_destination, menu);
        return true;
    }
	
	
	/**
	 * Onclick for "Go back and choose a new origin" button. Calls finish() which closes DestinationActivity and returns to OriginActivity
	 */
	public void changeOrigin(View v){
		finish();
	}

	
	/**
     * Displays an {@link AlertDialog} with one button that dismisses the dialog.  Use this to display error messages 
     * to the user.
     * 
     * @param message
     */
    private void showErrorDialog(String message) {
    	OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.error_message_title), message) {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		dialog.show(mContext.getSupportFragmentManager(), TAG);
    }
    
    
    private void showPlacePickerDialog(final DestinationInputRow row, List<GooglePlace> options) {
		Log.v(TAG, "Show place picker dialog");
		final PlacesListAdapter adapter = new PlacesListAdapter(mContext, options);
		ListPickerDialog dialog = new ListPickerDialog("Select...", adapter) {

			@Override
			public void onSelection(int which) {
				row.setAddress(((GooglePlace) adapter.getItem(which)).getAddress());
				row.setValid();
				Log.v(TAG, "Address: " + row.getAddress().getLatitude() + ", " + row.getAddress().getLongitude());
			}
			
		};
		dialog.show(mContext.getSupportFragmentManager(), TAG);
	}

}
