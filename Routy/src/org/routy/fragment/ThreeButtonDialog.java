package org.routy.fragment;

import android.content.DialogInterface;

public abstract class ThreeButtonDialog extends RoutyDialog {
	
	
	public ThreeButtonDialog(String title, String message) {
		super(title, message, true, true, true);
	}

	public abstract void onPositiveClicked(DialogInterface dialog, int which);

	public abstract void onNeutralClicked(DialogInterface dialog, int which);

	public abstract void onNegativeClicked(DialogInterface dialog, int which);

}
