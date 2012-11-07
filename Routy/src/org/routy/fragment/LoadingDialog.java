package org.routy.fragment;

import org.routy.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

// TODO Create an abstract method to interact with the dialog's progress
public class LoadingDialog extends DialogFragment {

	
	private String title;
	private String message;
	
	
	public LoadingDialog() {
		super();
		
		this.message = null;
	}
	
	
	public LoadingDialog(String message) {
		super();
		
		this.message = message;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.title = getResources().getString(R.string.app_name);
		if (message == null) {
			message = getResources().getString(R.string.default_loading_message);
		}
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(true);
		dialog.setTitle(title);
		dialog.setMessage(message);

		// Don't show percentages...doesn't make sense for indeterminate
		dialog.setProgressPercentFormat(null);
		dialog.setProgressNumberFormat(null);
		
		return dialog;
	}
}
