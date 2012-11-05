package org.routy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoInternetConnectionException;
import org.routy.model.Route;
import org.routy.model.RouteRequest;
import org.routy.service.AddressService;
import org.routy.task.CalculateRouteTask;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class DestinationActivity extends FragmentActivity {
	
	private static final int NUM_DEFAULT_DESTINATIONS = 3;		// # of destination fields to show initially
	
	Context mContext;
	Address originAddress;
	ArrayList<EditText> destinationEditTexts 	= new ArrayList<EditText>();
	ArrayList<Button> removeDestinationButtons 	= new ArrayList<Button>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        
        // TODO: should we put 'this.' in front of all class vars used?
        mContext		= this;
        Bundle extras 	= getIntent().getExtras();
        if (extras != null) {
            originAddress = (Address) extras.get("origin");
            assert originAddress != null;
        }
        

        // instantiate edit texts and their 'remove' buttons
        for (int x = 0; x < NUM_DEFAULT_DESTINATIONS; x++){
        	addDestination();
        }
        
        // "Test defaults"
        Button buttonTestDefaults = (Button) findViewById(R.id.button_test_defaults);
        buttonTestDefaults.setOnClickListener(listenerTestDefaults);
        
        
        // "Add another destination"
        Button buttonAddDestination = (Button) findViewById(R.id.button_destination_add);
        buttonAddDestination.setOnClickListener(listenerAddDestination);
        
        // "Go back and choose another origin"
        	// goes back to OriginActivity
        
        // "Route it!"
        Button buttonRouteIt = (Button) findViewById(R.id.button_route_it);
        buttonRouteIt.setOnClickListener(listenerRouteIt);
        
    }
    
    
	/*
	 * Click listener for the "Remove" button
	 */
    View.OnClickListener listenerTestDefaults = new View.OnClickListener() {
        public void onClick(View v) {
        	TableLayout table = (TableLayout) findViewById(R.id.TableLayout_destinations);
        	TableRow row;
        	EditText et;
        	row = (TableRow) table.getChildAt(0);
        	et = (EditText) row.getChildAt(0);
        	et.setText(R.string.test_destination_1);
        	row = (TableRow) table.getChildAt(1);
        	et = (EditText) row.getChildAt(0);
        	et.setText(R.string.test_destination_2);
        	row = (TableRow) table.getChildAt(2);
        	et = (EditText) row.getChildAt(0);
        	et.setText(R.string.test_destination_3);
        }
    };
    

	/*
	 * Click listener for the "Remove" button
	 */
    View.OnClickListener listenerRemoveDestination = new View.OnClickListener() {
        public void onClick(View v) {
        	TableLayout table = (TableLayout) findViewById(R.id.TableLayout_destinations);
        	TableRow tablerow = (TableRow) v.getParent();
        	
        	table.removeView(tablerow);
        }
    };
    
    
	/*
	 * Click listener for the "Add another destination" button
	 */
    View.OnClickListener listenerAddDestination = new View.OnClickListener() {
        public void onClick(View v) {
        	addDestination();
        }
    };
    
    private void addDestination() {
    	// http://forum.codecall.net/topic/70354-tablelayout-programatically/#axzz2B6OUEzdn
    	
    	TableLayout table 	= (TableLayout) findViewById(R.id.TableLayout_destinations);
        TableRow row		= new TableRow(mContext);
		EditText et 		= new EditText(mContext);
		Button b 			= new Button(mContext);
		int newRowIndex		= table.getChildCount();
		
		// Instantiate EditText and Button objects
		et.setText(getResources().getString(R.string.destination_enter));
		et.setOnFocusChangeListener((OnFocusChangeListener) listenerDestinationText);
		b.setText(getResources().getString(R.string.destination_remove));	
		
		// EditTexts and Buttons will have same ID if they're on the same row
		et.setId(newRowIndex);
		b.setId(newRowIndex);
		
		et.setGravity(Gravity.LEFT);
		b.setGravity(Gravity.RIGHT);

		b.setOnClickListener(listenerRemoveDestination);
		
		destinationEditTexts.add(et);
		removeDestinationButtons.add(b);
		
		// Add et and b columns
		row.addView(et);
		row.addView(b);
		
		// Set width of row and add row to table
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		table.addView(row); 
	}
    
	/*
	 * Click listener for the "Enter a destination" EditText
	 */
    View.OnFocusChangeListener listenerDestinationText = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
        	EditText et 		= (EditText) v;
        	String currentText 	= et.getText().toString();
        	String enterText 	= getResources().getString(R.string.destination_enter);
        	if ("".equals(currentText))
        		et.setText(enterText);
        	else if (enterText.equals(currentText))
        		et.setText("");
		}
	};
    

    // TODO: Clean this up
	// Click listener for the "Route it!" button
    View.OnClickListener listenerRouteIt = new View.OnClickListener() {
        public void onClick(View v) {
        	ArrayList<Address> validatedAddresses = new ArrayList<Address>();
        	
        	// Iterates through entered locations and validates them into addresses.
        	Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        	AddressService addressService =  new AddressService(geocoder, false);		// TODO make getting sensor true/false dynamic	
        	// TODO: "please wait" screen so activity doesn't block.
    		try {
    			// gets the destination text from the EditText boxes and tries to validate the strings
            	for (int i = 0; i < destinationEditTexts.size(); i++){
					validatedAddresses.add(addressService.getAddressForLocationString(destinationEditTexts.get(i).getText().toString()));
            	}
    			Toast.makeText(mContext, "Addresses validated!", Toast.LENGTH_LONG).show();	// XXX temp
			} catch (AmbiguousAddressException e) {
				// TODO error handling - must step out of this OnClick
				e.printStackTrace();
			} catch (NoInternetConnectionException e) {
				// TODO error handling - must step out of this OnClick
				e.printStackTrace();
			}

        	
        	// TODO: fill in last two parameters of RouteService instantiation with user choice
        	// Instantiates a Route object with validated addresses and calls ResultsActivity
        	try {
				/*RouteService routeService = new RouteService(originAddress, validatedAddresses, RouteOptimizePreference.PREFER_DURATION, false);
				Route route = routeService.getBestRoute();		// TODO this needs to be done in an AsyncTask (otherwise NetworkOnMainThreadException)*/
        		
        		CalculateRouteTask task = new CalculateRouteTask() {
					
					@Override
					public void onRouteCalculated(Route route) {
						Toast.makeText(mContext, "Route generated!", Toast.LENGTH_LONG).show();	// XXX temp
						
						// Call ResultsActivity activity
		    			Intent resultsIntent = new Intent(getBaseContext(), ResultsActivity.class);
		    			resultsIntent.putExtra("addresses", (Serializable) route.getAddresses());
		    			resultsIntent.putExtra("distance", route.getTotalDistance());
		    			startActivity(resultsIntent);
					}
				};
				
				task.execute(new RouteRequest(originAddress, validatedAddresses, false));
				
			} catch (Exception e) {
				// TODO error handling
				e.printStackTrace();
			}
          
        }
    };
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_destination, menu);
        return true;
    }
}
