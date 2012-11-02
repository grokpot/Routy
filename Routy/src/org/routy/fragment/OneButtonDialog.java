package org.routy.fragment;

import android.content.DialogInterface;

public abstract class OneButtonDialog extends RoutyDialog {
	
	public OneButtonDialog(String title, String message) {
		this(title, message, null);
	}
	
	public OneButtonDialog(String title, String message, String[] buttonLabels) {
		super(title, message, buttonLabels, true, false, false);
	}

	public abstract void onButtonClicked(DialogInterface dialog, int which);
	
	@Override
	public void onPositiveClicked(DialogInterface dialog, int which) {
		onButtonClicked(dialog, which);
	}

	@Override
	public void onNeutralClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

	@Override
	public void onNegativeClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

}
