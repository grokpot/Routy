package org.routy.adapter;

import java.util.List;

import org.routy.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class DestinationAdapter extends ArrayAdapter<String> {

	private final String TAG = "DestinationAdapter";
	
	private Context context;
	private int textViewResourceId;
	private List<String> addresses;
	
	
	public DestinationAdapter(Context context, int textViewResourceId, List<String> addresses) {
		super(context, textViewResourceId, addresses);
		
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.addresses = addresses;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DestinationContainer container = null;
		
		// If Android is handing us a row to reuse.
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(textViewResourceId, parent, false);
			
			container = new DestinationContainer();
			container.editText = (EditText) row.findViewById(R.id.edittext_destination_add);
			container.removeButton = (Button) row.findViewById(R.id.button_destination_remove);
			
			row.setTag(container);		// Using the tag to store data
		} else {
			container = (DestinationContainer) row.getTag();
		}
		
		container.editText.setText(addresses.get(position));
		container.editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
//					addresses.set(position, ((EditText) v).getText().toString());
				}
			}
		});
		
		container.removeButton.setText("Remove");
		container.removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Remove string at position " + position);
				// Remove the destination from the list and tell Android to redraw the list
				addresses.remove(position);
				notifyDataSetChanged();
			}
		});
		
		return row;
	}
	
	
	/**
	 * Helps with performance since we don't have to instantiate these for every row.
	 * @author jtran
	 *
	 */
	static class DestinationContainer {
		EditText editText;
		Button removeButton;
	}

}
