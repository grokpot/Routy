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

public abstract class RoutyDialog extends DialogFragment {

	private String mErrorMessage;
	private TextView mTextView;
	
	private String rightButtonLabel;
	private String middleButtonLabel;
	private String leftButtonLabel;
	
	private boolean showPositive;
	private boolean showNeutral;
	private boolean showNegative;
	
	public RoutyDialog() {
		super();
		mErrorMessage = getResources().getString(R.string.default_error_message);
		
		setDefaultButtonLabels();
		
		this.showPositive = true;
		this.showNeutral = false;
		this.showNegative = false;
	}
	
	
	public RoutyDialog(String title, String message, String[] buttonLabels, boolean showPositive, boolean showNeutral, boolean showNegative) {
		super();
		
		if (message != null) {
			mErrorMessage = message;
		} else {
			mErrorMessage = getResources().getString(R.string.default_error_message);
		}
		
		if (buttonLabels == null) {
			setDefaultButtonLabels();
		} else if (buttonLabels.length == 3) {
			assignButtonLabels(buttonLabels);
		} else {
			throw new IllegalArgumentException("buttonLabels passed into RoutyDialog (or a subclass) needs to be a String[] of length 3.");
		}
		
		this.showPositive = showPositive;
		this.showNeutral = showNeutral;
		this.showNegative = showNegative;
	}
	
	
	private void setDefaultButtonLabels() {
		this.rightButtonLabel = "OK";
		this.middleButtonLabel = "Cancel";
		this.leftButtonLabel = "No";
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
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		
		final View view = inflater.inflate(R.layout.fragment_error_notification, null);
		mTextView = (TextView) view.findViewById(R.id.message_dialog_error);
		mTextView.setText(mErrorMessage);
		
		AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.error_message_title));
		builder.setView(view);
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
