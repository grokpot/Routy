package org.routy;

import java.util.ArrayList;
import java.util.List;

import org.routy.adapter.DestinationAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class DestinationListAdapterActivity extends FragmentActivity {

	private final String TAG = "DestinationListAdapterActivity";
	
	private ListView listView;
	private DestinationAdapter adapter;
	private List<String> addresses;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_destination_adaptered);
		
		addresses = new ArrayList<String>();
		addresses.add("Destination 1");
		
		adapter = new DestinationAdapter(this, R.layout.fragment_destination_add, addresses);
		
		listView = (ListView) findViewById(R.id.listview_destinations);
		listView.setAdapter(adapter);
	}
	
	
	public void addDestinationToList(View v) {
		addresses.add(getResources().getString(R.string.prompt_destination));
		adapter.notifyDataSetChanged();
	}
	
	public void getRouteResults(View v) {
		StringBuilder sb = new StringBuilder("Get route results for addresses: ");
		
		for (int i = 0; i < addresses.size() - 1; i++) {
			sb.append(addresses.get(i));
			sb.append(", ");
		}
		sb.append(addresses.get(addresses.size() - 1));
		
		Log.v(TAG, sb.toString());
	}
}
