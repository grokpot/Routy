package org.routy.view;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import org.routy.R;
import org.routy.exception.RoutyException;
import org.routy.service.AddressService;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class DestinationInputView extends LinearLayout {

	private final String TAG = "DestinationInputView";
	
	public static final int VALID = 0;
	public static final int NOT_VALIDATED = 1;
	public static final int INVALID = 2;
	
	private Context context;
	private UUID id;
	private Address address;
	private String addressString;
	private EditText editText;
	private int status;
	
	
	/**
	 * Called when the "remove" button is clicked for a DestinationAddView
	 * @param id		the {@link UUID} of the DestinationAddView to remove
	 */
	public abstract void onRemoveClicked(UUID id);
	
	
	public DestinationInputView(Context context) {
		this(context, "");
	}
	
	public DestinationInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = "";
		this.status = DestinationInputView.NOT_VALIDATED;
		
		initViews(context);
	}
	
	public DestinationInputView(Context context, String addressString) {
		super(context);
		
		this.context = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = addressString;
		this.status = DestinationInputView.NOT_VALIDATED;
		
		initViews(context);
	}
	
	
	private void initViews(Context context) {
		// Inflate the view for an "add destination" text field and remove button
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
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// NO-OP
				} else {
//					onLostFocus(id);
					Log.v(TAG, "edittext lost focus");
					validate();
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
	public void validate() {
		if (status == DestinationInputView.INVALID || status == DestinationInputView.NOT_VALIDATED) {
			String addressString = editText.getText().toString();
			Log.v(TAG, "validating: " + addressString);
			
			AddressService addressService = new AddressService(new Geocoder(context, Locale.getDefault()), false);
			try {
				address = addressService.getAddressForLocationString(addressString);
				
				if (address == null) {
					setInvalid();
				}
			} catch (RoutyException e) {
				// TODO How to handle this?
				Log.e(TAG, "RoutyException: " + e.getMessage());
				setInvalid();
			} catch (IOException e) {
				// TODO How to handle this?
				Log.e(TAG, "IOException: " + e.getMessage());
				setInvalid();
			}
		}
	}
	
	
	public UUID getUUID() {
		return this.id;
	}
	
	
	public String getAddressString() {
		return editText.getText().toString();
	}
	
	
	public Address getAddress() {
		return address;
	}
	
	
	public void setInvalid() {
		editText.setTextColor(getResources().getColor(R.color.Red));
		status = DestinationInputView.INVALID;
	}


	public void setValid() {
		editText.setTextColor(getResources().getColor(R.color.White));
		status = DestinationInputView.VALID;
	}
	
	
	public void clearValidStatus() {
		editText.setTextColor(getResources().getColor(R.color.White));
		status = DestinationInputView.NOT_VALIDATED;
	}
	
	
	public int getStatus() {
		return status;
	}
	
	
}
