package org.routy.view;

import java.util.UUID;

import org.routy.R;
import org.routy.Util;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ResultsSegmentView extends LinearLayout{
	
	private final String TAG = "DestinationRowView";
	private Address 	startAddress;
	private boolean	isLastAddress;
	private TextView 	segmentText;
	private ImageButton		segmentButton;
	private int		id;
	
	
	/**
	 * Called when the "click to view segment" button is clicked for a view_results_segment
	 * @param id		the {@link UUID} of the view_results_segment to remove
	 */
	public abstract void onSegmentClicked(int id, boolean isLastAddress);
	
	public abstract void onAddressClicked(int id);
	
	public ResultsSegmentView(Context context, Address startAddress, int addressIndex, boolean isLastAddress){
		super(context);
		
		this.id 			= addressIndex;
		this.startAddress	= startAddress;
		this.isLastAddress 	= isLastAddress;
		
		initViews(context);
	}
	
	private void initViews(Context context){
		// Inflate the view for a segment (start destination TextView and map Button)
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.view_result_segment, this);
		
		// Moved to Util.java for consistency across the app in how we display place text
		// Hacky "if" statement that displays the address if it's not a Google Place
		/*String addressText = startAddress.getFeatureName();
		if (startAddress.getThoroughfare() != null) {
			addressText = startAddress.getThoroughfare();
			if (startAddress.getSubThoroughfare() != null) {
				addressText = startAddress.getSubThoroughfare() + " " + addressText;
			}
		}*/
		String addressText = Util.getAddressText(startAddress);
		
		segmentText		= (TextView) findViewById(R.id.textview_results_segment);
		segmentText.setText(addressText);
		// Set respective pin as drawable-left for above TextView 
		segmentText.setCompoundDrawablesWithIntrinsicBounds(Util.getItemizedTag(id, context), null, null, null);
		segmentText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onAddressClicked(id);
			}
			
		});
		
		segmentButton	= (ImageButton) findViewById(R.id.button_results_segment);
		if (isLastAddress){
			segmentButton.setVisibility(GONE);
		}
		segmentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Log.v(TAG, "onSegmentClicked from row with id=" + id);
				onSegmentClicked(id, isLastAddress);
			}
		});
	}
	
}
