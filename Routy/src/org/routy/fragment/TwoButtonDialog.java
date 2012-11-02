package org.routy.fragment;

import android.content.DialogInterface;

public abstract class TwoButtonDialog extends RoutyDialog {
	
	
	public TwoButtonDialog(String title, String message) {
		super(title, message, true, false, true);
	}

	public abstract void onPositiveClicked(DialogInterface dialog, int which);

	@Override
	public void onNeutralClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

	public abstract void onNegativeClicked(DialogInterface dialog, int which);

}
