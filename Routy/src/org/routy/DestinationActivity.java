package org.routy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import org.routy.exception.AmbiguousAddressException;
import org.routy.exception.NoNetworkConnectionException;
import org.routy.model.Route;
import org.routy.model.RouteOptimizePreference;
import org.routy.model.RouteRequest;
import org.routy.service.AddressService;
import org.routy.service.RouteService;
import org.routy.task.CalculateRouteTask;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DestinationActivity extends Activity {
	
	Context mContext;
	Address originAddress;

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
        
        Button button_route_it = (Button) findViewById(R.id.button_route_it);
        button_route_it.setOnClickListener(listener_route_it);
        
    }

    // Click listener for the "Route it!" button
    View.OnClickListener listener_route_it = new View.OnClickListener() {
        public void onClick(View v) {
        	ArrayList<String> addresses = new ArrayList<String>();
        	ArrayList<Address> validatedAddresses = new ArrayList<Address>();
        	
        	// TODO: make this dynamic
        	EditText e0 = (EditText) findViewById(R.id.edittext_address0);
        	EditText e1 = (EditText) findViewById(R.id.edittext_address1);
        	EditText e2 = (EditText) findViewById(R.id.edittext_address2);
        	addresses.add(e0.getText().toString());
        	addresses.add(e1.getText().toString());
        	addresses.add(e2.getText().toString());
        	
        	// Iterates through entered locations and validates them into addresses.
        	Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        	AddressService addressService =  new AddressService(geocoder);	
        	// TODO: "please wait" screen so activity doesn't block.
    		try {
            	for (int addressIndex = 0; addressIndex < addresses.size(); addressIndex++){
					validatedAddresses.add(
							addressService.getAddressForLocationName(
									addresses.get(addressIndex)));
            	}
    			Toast.makeText(mContext, "Addresses validated!", Toast.LENGTH_LONG).show();	// XXX temp
			} catch (AmbiguousAddressException e) {
				// TODO error handling - must step out of this OnClick
				e.printStackTrace();
			} catch (NoNetworkConnectionException e) {
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
		    			Bundle b = new Bundle();
		    			// TODO: Route.getBundle()
		    			b.putSerializable("addresses", (Serializable) route.getAddresses());
		    			b.putInt("distance", route.getTotalDistance());
		    			resultsIntent.putExtra("route", b);
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
