package org.routy.fragment;

import org.routy.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

		return builder.create();
	}
	
	
	private DialogInterface.OnClickListener pickerListener = new DialogInterface.OnClickListener() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.v(TAG, which + " selected");
			onSelection(which);
		}
	};
}
