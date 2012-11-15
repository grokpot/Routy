package org.routy.adapter;

import java.util.List;

import org.routy.model.GooglePlace;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GooglePlacesListAdapter extends BaseAdapter {

	private Context mContext;
	private List<GooglePlace> mPlaces;

	
	public GooglePlacesListAdapter(Context context, List<GooglePlace> places) {
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
		TwoLineHolder holder = null;
		
		if (row == null) {		// This means Android handed us a previously used row View...so we gotta rebuild it
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
			
			holder = new TwoLineHolder();
			holder.line1 = (TextView) row.findViewById(android.R.id.text1);
			holder.line2 = (TextView) row.findViewById(android.R.id.text2);
			
			row.setTag(holder);		// The "tag" is being used as a place to store data
		} else {
			holder = (TwoLineHolder) row.getTag();
		}
		
		GooglePlace place = mPlaces.get(position);
		holder.line1.setText(place.getPlaceName());
		holder.line2.setText(place.getPlaceAddress());
		
		return null;
	}
	
	
	/**
	 * Container class that lets us hold onto two TextView objects to use when 
	 * the ListView is being generated.  This is for performance reasons.
	 * @author jtran
	 *
	 */
	private class TwoLineHolder {
		
		public TextView line1;
		public TextView line2;
	}


	@Override
	public int getCount() {
		return mPlaces.size();
	}


	@Override
	public Object getItem(int position) {
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
