package org.routy.fragment;

import org.routy.R;
import org.routy.RemoveDestinationClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public abstract class AddDestinationFragment extends Fragment {

	
	private RemoveDestinationClickListener removeListener;

	public AddDestinationFragment() {
		super();
		
		this.removeListener = null;
	}
	
	public AddDestinationFragment(RemoveDestinationClickListener removeListener) {
		super();
		
		this.removeListener = removeListener;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_destination_add, container);
		
		Button removeButton = (Button) view.findViewById(R.id.button_destination_remove);
		if (removeListener != null) {
			removeButton.setOnClickListener(removeListener);
		}
		
		return view;
	}
	
	
	
	
}
