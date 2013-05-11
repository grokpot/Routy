package org.routy.fragment;

import org.routy.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

/**
 * A dialog with a selectable list of items.
 * 
 * @author jtran
 *
 */
public abstract class ListPickerDialog extends DialogFragment {

	private final String TAG = "ListPickerDialog";
	
	private String mTitle;
	private ListAdapter mAdapter;
	
	
	public ListPickerDialog() {
		super();
		
	}
	
	
	public ListPickerDialog(String title, ListAdapter adapter) {
		super();
		
		mTitle = title;
		mAdapter = adapter;
	}
	
	
	public abstract void onSelection(int which);
	public abstract void onCancelled();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO initialize all the fields that depend on resources here
		if (mTitle == null) {
			mTitle = getResources().getString(R.string.default_listpicker_title);
		}
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitle);
		builder.setAdapter(mAdapter, pickerListener);
		builder.setCancelable(true);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
//		dialog.setOnDismissListener(onDismissListener);
		
		return dialog;
	}
	
	
	/*@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		Log.v(TAG, "ListPickerDialog dismissed");
	}*/
	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		Log.v(TAG, "Place picker dialog cancelled.");
		onCancelled();
	}
	
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		Log.v(TAG, "Place picker dialog dismissed.");
//		onCancelled();
	}
	
	
	private DialogInterface.OnClickListener pickerListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.v(TAG, which + " selected");
			onSelection(which);
		}
	};
	
	
	/*private DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			Log.v(TAG, "Place picker dialog dismissed.");
			onCancelled();
		}
	};*/
}