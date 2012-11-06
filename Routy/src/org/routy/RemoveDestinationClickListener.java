package org.routy;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;


public abstract class RemoveDestinationClickListener implements OnClickListener {
	
	
	private Fragment fragment;
	

	public RemoveDestinationClickListener(Fragment fragment) {
		super();
		this.fragment = fragment;
	}

	
	public abstract void onClick(View v);

}
