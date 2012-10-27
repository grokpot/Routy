package org.routy;

import org.routy.model.Route;

import android.location.Address;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

public class ResultsActivity extends Activity {

	// The Route sent by DestinationActivity
	Route route;
	
	// Segment Labels
	private EditText segmentTexts[];
	// Segment buttons
	private Button segmentButtons[]; 

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        
        Bundle extras 	= getIntent().getExtras();
        if (extras != null) {
            route = (Route) extras.get("route");
            assert route != null;
        }
        
        buildResultsView();
    }

    private void buildResultsView() {
    	int addressesSize	= route.getAddresses().size();
		segmentTexts 		= new EditText[addressesSize];
		segmentButtons 		= new Button[addressesSize];
		
		for (int address = 0; address < addressesSize; address++){
			segmentTexts[address].setText(
					route.getAddresses().get(address).toString());
			// TODO: change this to a string resource
			segmentButtons[address].setText("click to view segment");
		}
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }
}
