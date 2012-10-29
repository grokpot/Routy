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

public class ErrorDialog extends DialogFragment {

	private String mErrorMessage;
	private TextView mTextView;
	
	public ErrorDialog() {
		super();
		mErrorMessage = getResources().getString(R.string.default_error_message);
	}
	
	
	public ErrorDialog(String message) {
		super();
		
		if (message != null) {
			mErrorMessage = message;
		} else {
			mErrorMessage = getResources().getString(R.string.default_error_message);
		}
	}
	
	
	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_error_notification, container);
		mTextView = (TextView) view.findViewById(R.id.message_dialog_error);
		mTextView.setText(mErrorMessage);
		getDialog().setTitle("Ruh-Roh!");
		
		return view;
	}*/
	
	
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
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
}
