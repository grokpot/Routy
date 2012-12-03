package org.routy.fragment;

import org.routy.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

// Backwards compatible in case they're on pre-API11

public abstract class RoutyAlertDialog extends DialogFragment {

	private String mTitle;
	private String mMessage;
	private TextView mTextView;
	private String[] mButtonLabels;
	
	private String rightButtonLabel;
	private String middleButtonLabel;
	private String leftButtonLabel;
	
	private boolean showPositive;
	private boolean showNeutral;
	private boolean showNegative;
	
	public RoutyAlertDialog() {
		super();
		mMessage = getResources().getString(R.string.default_error_message);
		
		setDefaultButtonLabels();
		
		this.showPositive = true;
		this.showNeutral = false;
		this.showNegative = false;
	}
	
	
	public RoutyAlertDialog(String title, String message, String[] buttonLabels, boolean showPositive, boolean showNeutral, boolean showNegative) {
		super();
		
		mTitle		= title;
		mMessage	= message;
		
		if (buttonLabels != null && buttonLabels.length != 3) {
			throw new IllegalArgumentException("buttonLabels passed into RoutyDialog (or a subclass) needs to be a String[] of length 3.");
		} else {
			mButtonLabels = buttonLabels;
		}
		
		this.showPositive 	= showPositive;
		this.showNeutral 	= showNeutral;
		this.showNegative 	= showNegative;
	}
	
	
	private void setDefaultButtonLabels() {
		if (isAdded()) {
			this.rightButtonLabel = getResources().getString(R.string.ok);
			this.middleButtonLabel = getResources().getString(R.string.cancel);
			this.leftButtonLabel = getResources().getString(R.string.no);
		}
	}
	
	
	private void assignButtonLabels(String[] buttonLabels) {
		setDefaultButtonLabels();
		
		if (buttonLabels[0] != null) {
			rightButtonLabel = buttonLabels[0];
		}
		
		if (buttonLabels[1] != null) {
			middleButtonLabel = buttonLabels[1];
		}
		
		if (buttonLabels[2] != null) {
			leftButtonLabel = buttonLabels[2];
		}
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mTitle == null) {
			mTitle = getResources().getString(R.string.error_message_title);
		}
		
		if (mMessage == null) {
			mMessage = getResources().getString(R.string.default_error_message);
		}
		
		if (mButtonLabels == null) {
			setDefaultButtonLabels();
		} else {
			assignButtonLabels(mButtonLabels);
		}
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitle);
		builder.setMessage(mMessage);
		builder.setCancelable(true);
		
		if (showPositive) {
			builder.setPositiveButton(rightButtonLabel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onPositiveClicked(dialog, which);
				}
			});
		}
		
		if (showNeutral) {
			builder.setNeutralButton(middleButtonLabel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onNeutralClicked(dialog, which);
				}
			});
		}
		
		if (showNegative) {
			builder.setNegativeButton(leftButtonLabel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onNegativeClicked(dialog, which);
				}
			});
		}
		
		return builder.create();
	}
	
	
	/**
	 * Invoked when the "positive" button on the dialog is tapped.
	 * @param dialog
	 * @param which
	 */
	public abstract void onPositiveClicked(DialogInterface dialog, int which);
	
	
	/**
	 * Invoked when the "neutral" button on the dialog is tapped.
	 * @param dialog
	 * @param which
	 */
	public abstract void onNeutralClicked(DialogInterface dialog, int which);
	
	
	/**
	 * Invoked when the "negative" button on the dialog is tapped.
	 * @param dialog
	 * @param which
	 */
	public abstract void onNegativeClicked(DialogInterface dialog, int which);
}
