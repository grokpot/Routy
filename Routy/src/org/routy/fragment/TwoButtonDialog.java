package org.routy.fragment;

import android.content.DialogInterface;

public abstract class TwoButtonDialog extends RoutyDialog {
	
	public TwoButtonDialog(String title, String message) {
		this(title, message, null);
	}
	
	public TwoButtonDialog(String title, String message, String[] buttonLabels) {
		super(title, message, buttonLabels, true, false, true);
	}

	public abstract void onRightButtonClicked(DialogInterface dialog, int which);
	public abstract void onLeftButtonClicked(DialogInterface dialog, int which);
	
	@Override
	public void onPositiveClicked(DialogInterface dialog, int which) {
		onRightButtonClicked(dialog, which);
	}

	@Override
	public void onNeutralClicked(DialogInterface dialog, int which) {
		// NO-OP
	}

	@Override
	public void onNegativeClicked(DialogInterface dialog, int which) {
		onLeftButtonClicked(dialog, which);
	}

}
