package org.routy.adapter;

import java.util.List;

import org.routy.R;
import org.routy.model.GooglePlace;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlacesListAdapter extends BaseAdapter {

	private final String TAG = "PlacesListAdapter";
	
	private Context mContext;
	private List<GooglePlace> mPlaces;

	
	public PlacesListAdapter(Context context, List<GooglePlace> places) {
		super();
		
		mContext = context;
		mPlaces = places;
	}
	
	
	/*
	 * This is the method that determines how each item in the list will look.  Take the 
	 * object's data and lay it out however you want to.  We'll just stick to text and caption.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;		// This is the view we need to use (it can be "re-used" by Android)
		GPlaceLineItemHolder holder = null;
		
		if (row == null) {		// This means Android handed us a previously used row View...so we gotta rebuild it
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.gplace_list_item, parent, false);

			holder = new GPlaceLineItemHolder();
			holder.nameLine = (TextView) row.findViewById(R.id.textview_gplace_name);
			holder.addressLine = (TextView) row.findViewById(R.id.textview_gplace_address);
			
			row.setTag(holder);		// The "tag" is being used as a place to store data
		} else {
			holder = (GPlaceLineItemHolder) row.getTag();
		}
		
		GooglePlace place = mPlaces.get(position);
//		Log.v(TAG, "Displaying: " + place.getName() + " - " + place.getFormattedAddress());
		holder.nameLine.setText(place.getName());
		holder.addressLine.setText(place.getFormattedAddress());
		
		return row;
	}
	
	
	/**
	 * Container class that lets us hold onto two TextView objects to use when 
	 * the ListView is being generated.  This is for performance reasons.
	 * @author jtran
	 *
	 */
	private class GPlaceLineItemHolder {
		
		public TextView nameLine;
		public TextView addressLine;
	}


	@Override
	public int getCount() {
		return mPlaces.size();
	}


	@Override
	public GooglePlace getItem(int position) {
		return mPlaces.get(position);
	}


	/**
	 * Not used.  Will return 0 all the time.
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

}
