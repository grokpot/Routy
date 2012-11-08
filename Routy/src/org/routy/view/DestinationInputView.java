package org.routy.view;

import java.util.UUID;

import org.routy.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class DestinationInputView extends LinearLayout {

	private final String TAG = "DestinationInputView";

//	private Context context;
	private UUID id;
//	private Destination destination;
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
		
//		this.context = context;
		this.id = UUID.randomUUID();
		this.address = "";
		
		initViews(context);
	}
	
	public DestinationInputView(Context context, String address) {
		super(context);
		
//		this.context = context;
		this.id = UUID.randomUUID();
		this.address = address;
		
		initViews(context);
	}
	
	
	private void initViews(Context context) {
		// Inflate the view for an "add destination" text field and remove button
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.view_destination_add, this);
		
		final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			
			/*@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.v(TAG, "flinged!");
				return true;
			}*/
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.v(TAG, "scrolled! -- velocityX=" + velocityX);
				if (Math.abs(velocityX) >= 40.0) {
					onRemoveClicked(id);
				}
				return true;
			}
			
		});
		
		
		editText = (EditText) findViewById(R.id.edittext_destination_add);
		editText.setText(address);
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setValid();
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
		
		editText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gestureDetector.onTouchEvent(event);
				return true;
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
	
	
	public UUID getUUID() {
		return this.id;
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