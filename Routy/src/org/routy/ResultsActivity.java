package org.routy;

import java.util.ArrayList;

import org.routy.model.Route;

import android.location.Address;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	// The Route sent by DestinationActivity
	Route route;
	
	// Segment Labels
	private TextView segmentTexts[];
	// Segment buttons
	private Button segmentButtons[]; 

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        
        Bundle extras 	= getIntent().getExtras();
        if (extras != null) {
            int distance = (Integer) extras.get("distance");
            ArrayList<Address> addresses=  (ArrayList<Address>) extras.get("addresses");
            route = new Route(addresses, distance);
        }
        
        buildResultsView();
    }

    private void buildResultsView() {
    	int addressesSize	= route.getAddresses().size();
		segmentTexts 		= new TextView[addressesSize];
		segmentButtons 		= new Button[addressesSize];
		LinearLayout resultsLayout = (LinearLayout) findViewById(R.id.layout_results);
		
		TextView text_total_distance = (TextView) findViewById(R.id.textview_total_distance);
		text_total_distance.setText("Your total distance is: " + route.getTotalDistance());
		
		for (int address = 0; address < addressesSize; address++){
			segmentTexts[address] = new TextView(this);
			segmentButtons[address] = new Button(this);
			String addressText = route.getAddresses().get(address).getAddressLine(0);
			segmentTexts[address].setText(addressText);
			// TODO: change this to a string resource
			segmentButtons[address].setText("click to view segment");
			
			// Position texts and views
			segmentTexts[address].layout(0, 20, 0, 0);
			segmentButtons[address].layout(0, 20, 0, 0);
			resultsLayout.addView(segmentTexts[address]);
			resultsLayout.addView(segmentButtons[address]);
		}
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }
}
