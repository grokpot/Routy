package org.routy.fragment;

import android.content.DialogInterface;

public abstract class OneButtonDialog extends RoutyDialog {
	
	public OneButtonDialog(String title, String message) {
		super(title, message, true, false, false);
	}

	public abstract void onPositiveClicked(DialogInterface dialog, int which);

	@Override
	public void onNeutralClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

	@Override
	public void onNegativeClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

}
