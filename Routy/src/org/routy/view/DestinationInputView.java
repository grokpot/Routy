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
	
	private Context context;
	private UUID id;
//	private Destination destination;
	private Address address;
	private String addressString;
	private EditText editText;
	
	
	/**
	 * Called when the "remove" button is clicked for a DestinationAddView
	 * @param id		the {@link UUID} of the DestinationAddView to remove
	 */
	public abstract void onRemoveClicked(UUID id);
	
	
	/**
	 * Called when the EditText inside this DestinationInputView loses focus.
	 * @param id		the {@link UUID} of the DestinationAddView to remove
	 */
	/*public abstract void onLostFocus(UUID id);*/
	
	
	public DestinationInputView(Context context) {
		this(context, "");
	}
	
	public DestinationInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = "";
		
		initViews(context);
	}
	
	public DestinationInputView(Context context, String addressString) {
		super(context);
		
		this.context = context;
		this.id = UUID.randomUUID();
		this.address = null;
		this.addressString = addressString;
		
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
				setValid();
				address = null;
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
					if (address == null) {
						address = validate(((EditText) v).getText().toString());
						
						if (address == null) {
							setInvalid();
						}
					}
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
	private Address validate(String addressString) {
		Log.v(TAG, "validating: " + addressString);
		AddressService addressService = new AddressService(new Geocoder(context, Locale.getDefault()), false);
		try {
			return addressService.getAddressForLocationString(addressString);
		} catch (RoutyException e) {
			// TODO How to handle this?
			Log.e(TAG, "RoutyException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO How to handle this?
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public UUID getUUID() {
		return this.id;
	}
	
	
	public Address getAddress() {
		return address;
	}
	
	
	public void setInvalid() {
		// TODO do what's necessary visually to show that this destination is invalid
		editText.setTextColor(getResources().getColor(R.color.Red));
//		editText.setBackgroundColor(getResources().getColor(R.color.Pink));
	}


	public void setValid() {
		// TODO Undo whatever setInvalid() does
		editText.setTextColor(getResources().getColor(R.color.White));
	}
	
	
}
