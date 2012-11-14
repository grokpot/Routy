package org.routy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import junit.framework.Assert;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;
import org.routy.fragment.OneButtonDialog;
import org.routy.model.AppProperties;
import org.routy.model.Route;
import org.routy.model.RouteRequest;
import org.routy.service.AddressService;
import org.routy.task.CalculateRouteTask;
import org.routy.view.DestinationInputView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_destination);
		
		mContext = this;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			origin = (Address) extras.get("origin");
			Assert.assertNotNull(origin);
			
			Log.v(TAG, "Origin address: " + origin.getExtras().getString("formatted_address"));
			TextView originText = (TextView) findViewById(R.id.textview_destinations_origin);
			originText.setText("Starting from: \n" + origin.getExtras().getString("formatted_address"));
		}
		
		destLayout = (LinearLayout) findViewById(R.id.LinearLayout_destinations);
		
		addDestinationInputView();
		
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
        			addDestinationInputView();
        		}
        	}
        	
        	DestinationInputView view = (DestinationInputView) destLayout.getChildAt(0);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_1));
        	
        	view = (DestinationInputView) destLayout.getChildAt(1);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_2));
        	
        	view = (DestinationInputView) destLayout.getChildAt(2);
        	((EditText) view.findViewById(R.id.edittext_destination_add)).setText(getResources().getString(R.string.test_destination_3));
        }
    };
	
	
    /**
     * Puts another EditText and "remove" button on the screen for another destination.
     */
	void addDestinationInputView() {
		DestinationInputView v = new DestinationInputView(mContext) {

			@Override
			public void onRemoveClicked(UUID id) {
				Log.v(TAG, "Remove DestinationAddView id=" + id);
				removeDestinationAddView(id);
			}
		};
		
		destLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	
	/**
	 * Removes the EditText and "remove" button row ({@link DestinationInputView}} with the given {@link UUID} from the screen.
	 * @param id
	 */
	void removeDestinationAddView(UUID id) {
		// Go through the views list and remove the one that has the given UUID
		
		// TODO check out views.remove(Object o) and see how it compares objects to find the right one
		
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			if (((DestinationInputView) destLayout.getChildAt(i)).getUUID().equals(id)) {
				destLayout.removeViewAt(i);
				break;
			}
		}
	}
	
	
	/**
	 * Adds a {@link DestinationInputView} row to the list if we're not maxed out.  Max number 
	 * of rows is set in {@link AppProperties}.
	 * @param v
	 */
	public void addAnotherDestination(View v) {
		Log.v(TAG, "Add another destination.");
		
		if (destLayout.getChildCount() < AppProperties.NUM_MAX_DESTINATIONS) {
			addDestinationInputView();
		}
	}
	
	
	// TODO Right now it's validating ALL destinations every time (regardless of if any were already validated) -- make it more efficient
	public void acceptDestinations(View v) {
		Log.v(TAG, "Validate destinations and calculate route if they're good.");
		
		for (int i = 0; i < destLayout.getChildCount(); i++) {
			Log.v(TAG, "Destination " + i + ": " + ((EditText) ((DestinationInputView) destLayout.getChildAt(i)).findViewById(R.id.edittext_destination_add)).getText().toString());
		}
		
		// Validate the addresses and highlight any errors.
		List<Address> validatedAddresses = validateDestinations();
		
		Log.v(TAG, validatedAddresses.size() + " addresses");
		
		if (validatedAddresses.size() == 0) {
			// All fields were empty
			showErrorDialog("Please enter at least 1 destination to continue.");
		} else {
			boolean hasErrors = false;
			for (int i = 0; i < validatedAddresses.size(); i++) {
				if (validatedAddresses.get(i) == null) {
					flagInvalidDestination(i);
					hasErrors = true;
				}
			}
			
			if (!hasErrors) {
				// TODO Calculate route and go to results activity
//				Toast.makeText(mContext, getString(R.string.validated), Toast.LENGTH_LONG).show();	// XXX temp
				
				// TODO: fill in last two parameters of RouteService instantiation with user choice
	        	// Instantiates a Route object with validated addresses and calls ResultsActivity
	        	try {
					/*RouteService routeService = new RouteService(origin, validatedAddresses, RouteOptimizePreference.PREFER_DURATION, false);
					Route route = routeService.getBestRoute();*/
	        		
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
					
					task.execute(new RouteRequest(origin, validatedAddresses, false));
					
				} catch (Exception e) {
					// TODO error handling
					e.printStackTrace();
				}
			} else {
				Log.e(TAG, "Errors found in destinations.");
				showErrorDialog("The addresses in red are invalid.  Try being more specific.");
			}
		}
	}
	
	
	private void flagInvalidDestination(int position) {
		if (position >= 0 && position < destLayout.getChildCount()) {
			DestinationInputView view = (DestinationInputView) destLayout.getChildAt(position);
			view.setInvalid();
		}
	}
	
	
	private List<Address> validateDestinations() {
		List<Address> addresses = new ArrayList<Address>();
		
		// Iterates through entered locations and validates them into addresses.
    	Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
    	AddressService addressService =  new AddressService(geocoder, false);		// TODO make getting sensor true/false dynamic	

    	// TODO: "please wait" screen so activity doesn't block.
		DestinationInputView view = null;
		Address address = null;
		String userInput = null;
		
		// gets the destination text from the EditText boxes and tries to validate the strings
    	for (int i = 0; i < destLayout.getChildCount(); i++){
			try {
        		view = (DestinationInputView) destLayout.getChildAt(i);
        		userInput = ((EditText) view.findViewById(R.id.edittext_destination_add)).getText().toString();
        		if (userInput != null && userInput.trim().length() > 0) {
        			address = addressService.getAddressForLocationString(userInput);
            		addresses.add(address);
        		}
			} catch (AmbiguousAddressException e) {
				// TODO error handling - must step out of this OnClick
				Log.v(TAG, "Ambiguous address.");
				addresses.add(null);
			} catch (RoutyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
		return addresses;
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_destination, menu);
        return true;
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

}
