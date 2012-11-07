package org.routy.view;

import java.util.UUID;

import org.routy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class DestinationInputView extends LinearLayout {

	
	private UUID id;
	private String address;
	private EditText editText;
	
	
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
		
		this.id = UUID.randomUUID();
		this.address = "";
		
		initViews(context);
	}
	
	public DestinationInputView(Context context, String address) {
		super(context);
		
		this.id = UUID.randomUUID();
		this.address = address;
		
		initViews(context);
	}
	
	
	private void initViews(Context context) {
		// Inflate the view for an "add destination" text field and remove button
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.view_destination_add, this);
		
		editText = (EditText) findViewById(R.id.edittext_destination_add);
		editText.setText(address);
		
		Button removeButton = (Button) findViewById(R.id.button_destination_remove);
		removeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRemoveClicked(id);
			}
		});
	}
	
	
	public UUID getUUID() {
		return this.id;
	}
	
	
	public void setInvalid() {
		// TODO do what's necessary visually to show that this destination is invalid
		editText.setTextColor(getResources().getColor(R.color.Red));
//		editText.setBackgroundColor(getResources().getColor(R.color.Pink));
	}
	
	
}
