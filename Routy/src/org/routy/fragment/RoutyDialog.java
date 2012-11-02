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
	
	private boolean showPositive;
	private boolean showNeutral;
	private boolean showNegative;
	
	public RoutyDialog() {
		super();
		mErrorMessage = getResources().getString(R.string.default_error_message);
		
		this.showPositive = true;
		this.showNeutral = false;
		this.showNegative = false;
	}
	
	
	public RoutyDialog(String title, String message, boolean showPositive, boolean showNeutral, boolean showNegative) {
		super();
		
		if (message != null) {
			mErrorMessage = message;
		} else {
			mErrorMessage = getResources().getString(R.string.default_error_message);
		}
		
		this.showPositive = showPositive;
		this.showNeutral = showNeutral;
		this.showNegative = showNegative;
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
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onPositiveClicked(dialog, which);
				}
			});
		}
		
		if (showNeutral) {
			builder.setNeutralButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onNeutralClicked(dialog, which);
				}
			});
		}
		
		if (showNegative) {
			builder.setNegativeButton("No", new OnClickListener() {
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
