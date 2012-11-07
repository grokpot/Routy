package org.routy.fragment;

import android.content.DialogInterface;

public abstract class OneButtonDialog extends RoutyAlertDialog {
	
	public OneButtonDialog(String title, String message) {
		super(title, message, null, true, false, false);
	}
	
	public OneButtonDialog(String title, String message, String buttonLabel) {
		this(title, message, new String[] {(buttonLabel==null?"":buttonLabel), "", ""});
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
