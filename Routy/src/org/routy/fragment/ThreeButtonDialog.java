package org.routy.fragment;

import android.content.DialogInterface;

public abstract class ThreeButtonDialog extends RoutyAlertDialog {
	
	/**
	 * Creates a 3-button alert dialog with the given title and message.  The buttons will 
	 * be labeled (from right to left): "OK", "Cancel", and "No"
	 * @param title
	 * @param message
	 */
	public ThreeButtonDialog(String title, String message) {
		this(title, message, null);
	}
	
	/**
	 * Creates a 3-button alert dialog with the given title, message, and button labels.  The 
	 * buttons will be labeled from right to left (ie. right button = buttonLabels[0], middle button = 
	 * buttonLabels[1], and left button = buttonLabels[2]) and will default to preset values if null.
	 * @param title
	 * @param message
	 * @param buttonLabels
	 */
	public ThreeButtonDialog(String title, String message, String[] buttonLabels) {
		super(title, message, buttonLabels, true, true, true);
	}
	
	public abstract void onRightButtonClicked(DialogInterface dialog, int which);
	public abstract void onMiddleButtonClicked(DialogInterface dialog, int which);
	public abstract void onLeftButtonClicked(DialogInterface dialog, int which);

	@Override
	public void onPositiveClicked(DialogInterface dialog, int which) {
		onRightButtonClicked(dialog, which);
	}
	
	@Override
	public void onNeutralClicked(DialogInterface dialog, int which) {
		onMiddleButtonClicked(dialog, which);
	}
	
	@Override
	public void onNegativeClicked(DialogInterface dialog, int which) {
		onLeftButtonClicked(dialog, which);
	}
	

}
