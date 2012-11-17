package org.routy.view;

import java.util.List;
import java.util.UUID;

import org.routy.R;
import org.routy.adapter.PlacesListAdapter;
import org.routy.exception.RoutyException;
import org.routy.fragment.ListPickerDialog;
import org.routy.fragment.OneButtonDialog;
import org.routy.model.GooglePlace;
import org.routy.model.ValidateDestinationRequest;
import org.routy.task.ValidateDestinationTask;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class DestinationInputRow extends LinearLayout {

	private final String TAG = "DestinationInputView";
	
	public static final int VALID = 0;
	public static final int NOT_VALIDATED = 1;
	public static final int INVALID = 2;
	
	private FragmentActivity mContext;
	private UUID id;
	private Address address;
	private String addressString;
	private double centerLat;
	private double centerLng;
	private int radius;
	private EditText editText;
	private Button addButton;
	private int status;
	
	
	/**
	 * Called when the "+" button is clicked for a DestinationInputView row.  The 
	 * string in this row should be validated when this happens.
	 * @param id		the {@link UUID} of the DestinationInputView row
	 */
	public abstract void onAddClicked(UUID id);
	
	/**
	 * Called when the "remove" button is clicked for a DestinationAddView
	 * @param id		the {@link UUID} of the DestinationInputView to remove
	 */
	public abstract void onRemoveClicked(UUID id);
	
	/**
	 * Called when the {@link EditText} in this DestinationInputView row loses focus.
	 * @param id		the {@link UUID} of the DestinationInputView to remove
	 *//*
	public abstract void onLostFocus(UUID id);*/
	
	
	public DestinationInputRow(FragmentActivity context) {
		this(context, "");
	}
	
	public DestinationInputRow(FragmentActivity context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mContext = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = "";
		this.status = DestinationInputRow.NOT_VALIDATED;
		
		initViews(context);
	}
	
	public DestinationInputRow(FragmentActivity context, String addressString) {
		this(context, addressString, -1, -1, 0);
	}
	
	public DestinationInputRow(FragmentActivity context, double centerLat, double centerLng, int radius) {
		this(context, "", centerLat, centerLng, radius);
	}
	
	public DestinationInputRow(FragmentActivity context, String addressString, double centerLat, double centerLng, int radius) {
		super(context);
		
		this.mContext = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = addressString;
		this.centerLat = centerLat;
		this.centerLng = centerLng;
		this.radius = radius;
		this.status = DestinationInputRow.NOT_VALIDATED;
		
		initViews(context);
	}
	
	
	/**
	 * Prepares the view for a single destination entry row.
	 * @param context
	 */
	private void initViews(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.view_destination_add, this);
		
		editText = (EditText) findViewById(R.id.edittext_destination_add);
		editText.setText(addressString);
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				clearValidStatus();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addButton = (Button) findViewById(R.id.button_destination_add);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (editText.getText().toString().length() > 0) { 
					v.setVisibility(INVISIBLE);
					onAddClicked(id);
				}
			}
		});
		
		Button removeButton = (Button) findViewById(R.id.button_destination_remove);
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRemoveClicked(id);
			}
		});
	}
	
	
	/**
	 * Validates the address string and sets the Address field for this view if valid.
	 * 
	 * @param addressString
	 * @return					true if the address string can be geocoded into an Address, false otherwise
	 */
	/*public void validate() {
		if (status == DestinationInputView.INVALID || status == DestinationInputView.NOT_VALIDATED) {
			String addressString = editText.getText().toString();
			Log.v(TAG, "validating: " + addressString);
			
			if (addressString != null && addressString.length() > 0) {
				new ValidateDestinationTask() {
					
					@Override
					public void onResult(List<GooglePlace> results) {
						if (results == null || results.size() < 1) {
							// No results.  Display a message.
							Log.v(TAG, "No places found for query");
							setInvalid();
							showErrorDialog("No places or addresses found for this destination.  Try broadening your search.");
						} else if (results.size() == 1) {
							// Only one result.  Turn it into an address, set it, and set the valid status
							Log.v(TAG, "1 place found for query");
							address = results.get(0).getAddress();
							setValid();
						} else {
							// More than 1 result.  Display the pickable list dialog.
							Log.v(TAG, "More than 1 place found for query -- " + results.size() + " results");
							showPlacePickerDialog(results);
						}
					}

					@Override
					public void onFailure(RoutyException exception) {
						showErrorDialog(exception.getMessage());
					}
				}.execute(new ValidateDestinationRequest(addressString, centerLat, centerLng, radius));
			}
		}
	}*/
	
	
	private void showErrorDialog(String message) {
    	OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.error_message_title), message) {
			@Override
			public void onButtonClicked(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		dialog.show(mContext.getSupportFragmentManager(), TAG);
    }
	
	
	private void showPlacePickerDialog(List<GooglePlace> options) {
		Log.v(TAG, "Show place picker dialog");
		final PlacesListAdapter adapter = new PlacesListAdapter(mContext, options);
		ListPickerDialog dialog = new ListPickerDialog("Select...", adapter) {

			@Override
			public void onSelection(int which) {
				address = ((GooglePlace) adapter.getItem(which)).getAddress();
				setValid();
				Log.v(TAG, "Address: " + address.getLatitude() + ", " + address.getLongitude());
			}
			
		};
		dialog.show(mContext.getSupportFragmentManager(), TAG);
	}
	
	
	public UUID getUUID() {
		return this.id;
	}
	
	
	public String getAddressString() {
		return editText.getText().toString();
	}
	
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	
	public Address getAddress() {
		return address;
	}
	
	
	public void setInvalid() {
		editText.setTextColor(getResources().getColor(R.color.Red));
		status = DestinationInputRow.INVALID;
	}


	public void setValid() {
		editText.setTextColor(getResources().getColor(R.color.White));
		status = DestinationInputRow.VALID;
	}
	
	
	public void clearValidStatus() {
		editText.setTextColor(getResources().getColor(R.color.White));
		status = DestinationInputRow.NOT_VALIDATED;
	}
	
	
	public int getStatus() {
		return status;
	}
	
	
	public void clear() {
		clearValidStatus();
		
		editText.setText("");
		addButton.setVisibility(VISIBLE);
	}
	
	public void resetButtons() {
		addButton.setVisibility(VISIBLE);
	}
}
