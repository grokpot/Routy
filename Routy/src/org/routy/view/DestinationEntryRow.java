package org.routy.view;

import org.routy.R;
import org.routy.model.AddressModel;
import org.routy.model.AppProperties;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class DestinationEntryRow extends LinearLayout {

	private final String TAG = "DestinationEntryRow";
	
	/**
	 * Invoked when user changes focus from the primary entry row to 
	 * the secondary row signifying that they've completed their entry.
	 * @param s		the {@link Editable} from the primary entry row
	 */
	public abstract void onEntryConfirmed(Editable s);
	public abstract void onFocusGained();
	
	private EditText primary;
	private EditText secondary;
	
	
	public DestinationEntryRow(Context context) {
		this(context, "");
	}
	
	public DestinationEntryRow(Context context, String initial) {
		super(context);
		
		initializeLayout(initial);
	}
	
	public DestinationEntryRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initializeLayout(null);
	}

	public DestinationEntryRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initializeLayout(null);
	}
	
	private void initializeLayout(String initial) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_destination_entry_row, this);
		
		primary = (EditText) findViewById(R.id.primary_entry_field);
		if (initial != null && initial.length() > 0) {
			primary.setText(initial);
		}
		primary.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText e = (EditText) v;
				if (!hasFocus) {
					onEntryConfirmed(e.getEditableText());
				} else {
					onFocusGained();
				}
			}
		});
		primary.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				AddressModel.getSingleton().setUnvalidatedDestEntry(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//DO NOTHING
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && s.length() > 0) {
					showSecondaryDestField();
				} else {
					hideSecondaryDestField();
				}
			}
		});
		
		LinearLayout destEntryRowView = (LinearLayout) findViewById(R.id.destination_entry_row);
		
		secondary = (EditText) findViewById(R.id.secondary_entry_field);
		hideSecondaryDestField();
	}
	
	private void hideSecondaryDestField() {
		secondary.setVisibility(View.GONE);
	}
	
	private void showSecondaryDestField() {
		if (AddressModel.getSingleton().getDestinations().size() < AppProperties.NUM_MAX_DESTINATIONS - 1) {
			secondary.setVisibility(View.VISIBLE);
		}
	}
	
	public void focusOnEntryField() {
		primary.requestFocus();
	}
}
