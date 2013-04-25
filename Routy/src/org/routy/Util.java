package org.routy;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.model.AddressStatus;
import org.routy.view.DestinationRowView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

public class Util {
	
	public static final String TAG = "Util";
	
	/**
	 * Encodes a list of {@link Address} objects into a JSON string.
	 * @param addresses
	 * @return
	 */
	public static String addressListToJSON(List<Address> addresses) {
		if (addresses == null) {
			return null;
		}
		
		if (addresses.size() == 0) {
			return "";
		}
		
		try {
			StringWriter sWriter = new StringWriter();
			JsonWriter jWriter = new JsonWriter(sWriter);
			
			jWriter.beginObject();
			jWriter.name("persisted_addresses");
			jWriter.beginArray();
			
			for (Address a : addresses) {
				writeAddress(jWriter, a);
			}
			
			jWriter.endArray();
			jWriter.endObject();
			jWriter.close();
			
			String json = sWriter.toString();
			sWriter.close();
			
			return json;
		} catch (IOException e) {
			Log.e(TAG, "couldn't encode the address list to a JSON string");
		}
		
		return null;
	}
	
	
	public static String writeAddressToJson(Address address) {
		StringWriter sWriter = new StringWriter();
		JsonWriter jWriter = new JsonWriter(sWriter);
		
		writeAddress(jWriter, address);
		
		String output = sWriter.toString();
		
		try {
			jWriter.close();
			sWriter.close();
		} catch (IOException e) {
			Log.e(TAG, "IOException closing writer(s)");
		}
		
		return output;
	}

	
	/**
	 * Encodes a single {@link Address} into a JSON string.
	 * 
	 * @param jWriter
	 * @param address
	 */
	static void writeAddress(JsonWriter jWriter, Address address) {
		try {
			jWriter.beginObject();
			jWriter.name("persisted_address");
			
			jWriter.beginObject();
			jWriter.name("feature_name").value(address.getFeatureName() == null ? "" : address.getFeatureName());
			try {
				jWriter.name("latitude");
				jWriter.value(address.getLatitude());
			} catch (IllegalStateException e) {
				jWriter.nullValue();
			}
			
			try {
				jWriter.name("longitude");
				jWriter.value(address.getLongitude());
			} catch (IllegalStateException e) {
				jWriter.nullValue();
			}
			
			Bundle extras = address.getExtras();
			if (extras != null) {
				String formattedAddress = extras.getString("formatted_address");
				
				if (formattedAddress != null) {
					jWriter.name("formatted_address").value(formattedAddress);
				}
				
				String addressString = extras.getString("address_string");
				if (addressString != null) {
					jWriter.name("address_string").value(addressString);
				}
				
				// Gotta save the validation status so we know if we have to re-validate or not
				jWriter.name("valid_status").value(extras.getInt("valid_status", DestinationRowView.NOT_VALIDATED));
				
				//NEW valid status field
				jWriter.name("validation_status").value(extras.getString("validation_status", AddressStatus.NOT_VALIDATED.toString()));
			}
			jWriter.endObject();
			
			jWriter.endObject();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() + " -- IOException");
		}
	}
	
	
	/**
	 * Decodes a JSON string into a list of {@link Address} objects.  NOTICE: the 
	 * JSON string needs to have been encoded by <code>Util.addressListToJSON(...)</code> 
	 * to guaranty the decoding will work.  Don't go using it willy-nilly on JSON 
	 * responses from Google APIs or anything...
	 * 
	 * @param json
	 * @return
	 */
	public static List<Address> jsonToAddressList(String json) {
		List<Address> addresses = new ArrayList<Address>();
		
		try {
			StringReader sReader = new StringReader(json);
			JsonReader jReader = new JsonReader(sReader);
			
			jReader.beginObject();
			String rootName = jReader.nextName();
			if (rootName.equalsIgnoreCase("persisted_addresses") && jReader.peek() != JsonToken.NULL) {
				Log.v(TAG, "reading addresses array");
				jReader.beginArray();
				
				while (jReader.hasNext()) {
					addresses.add(readAddress(jReader));
				}
				
				jReader.endArray();
			}
			jReader.endObject();
			
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() + "-- IOException reading JSON address list");
		}
		
		return addresses;
	}
	
	
	public static Address readAddressFromJson(String json) {
		StringReader sReader = new StringReader(json);
		JsonReader jReader = new JsonReader(sReader);
		
		return readAddress(jReader);
	}
	

	//TODO Fill out the entire address object...or figure out how to
	static Address readAddress(JsonReader jReader) {
		if (jReader == null) {
			return null;
		}
		
		Address address = new Address(Locale.getDefault());
		try {
			jReader.beginObject();
			String persistedAddress = jReader.nextName();
			Log.v(TAG, "persistedAddress=" + persistedAddress);
			if (persistedAddress.equalsIgnoreCase("persisted_address")) {
				jReader.beginObject();
				while (jReader.hasNext()) {
					String name = jReader.nextName();
					Log.v(TAG, "reading name: " + name);
					
					if (name.equalsIgnoreCase("feature_name")) {
						address.setFeatureName(jReader.nextString());
					} else if (name.equalsIgnoreCase("latitude")) {
						address.setLatitude(jReader.nextDouble());
					} else if (name.equalsIgnoreCase("longitude")) {
						address.setLongitude(jReader.nextDouble());
					} else if (name.equalsIgnoreCase("formatted_address")) {
						if (address.getExtras() == null) {
							address.setExtras(new Bundle());
						}
						address.getExtras().putString("formatted_address", jReader.nextString());
					} else if (name.equalsIgnoreCase("validation_status")) {
						if (address.getExtras() == null) {
							address.setExtras(new Bundle());
						}
						address.getExtras().putString("validation_status", jReader.nextString());
					} else if (name.equalsIgnoreCase("address_string")) {
						if (address.getExtras() == null) {
							address.setExtras(new Bundle());
						}
						address.getExtras().putString("address_string", jReader.nextString());
					} else {
						jReader.skipValue();
					}
				}
				jReader.endObject();
			}
			jReader.endObject();
			
			return address;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() + "-- IOException reading JSON address");
		}
		return null;
	}
	
	
	public static Drawable getItemizedPin(int index, Context context){
		Drawable drawable = null;
		switch(index){
			case 0: 	drawable = context.getResources().getDrawable(R.drawable.pinhome2);
					break;
			case 1: 	drawable = context.getResources().getDrawable(R.drawable.pin1);
					break;
			case 2: 	drawable = context.getResources().getDrawable(R.drawable.pin2);
					break;
			case 3: 	drawable = context.getResources().getDrawable(R.drawable.pin3);
					break;
			case 4: 	drawable = context.getResources().getDrawable(R.drawable.pin4);
					break;
			case 5: 	drawable = context.getResources().getDrawable(R.drawable.pin5);
					break;
		}
		return drawable;
	}
	
	
	public static int getItemizedPinId(int index) {
		int id = -1;
		switch(index){
			case 0: 	id = R.drawable.pinhome2;
					break;
			case 1: 	id = R.drawable.pin1;
					break;
			case 2: 	id = R.drawable.pin2;
					break;
			case 3: 	id = R.drawable.pin3;
					break;
			case 4: 	id = R.drawable.pin4;
					break;
			case 5: 	id = R.drawable.pin5;
					break;
		}
		return id;
	}
	
	
	public static Drawable getItemizedTag(int index, Context context){
		Drawable drawable = null;
		switch(index){
			case 0: 	drawable = context.getResources().getDrawable(R.drawable.taghome2);
					break;
			case 1: 	drawable = context.getResources().getDrawable(R.drawable.tag1);
					break;
			case 2: 	drawable = context.getResources().getDrawable(R.drawable.tag2);
					break;
			case 3: 	drawable = context.getResources().getDrawable(R.drawable.tag3);
					break;
			case 4: 	drawable = context.getResources().getDrawable(R.drawable.tag4);
					break;
			case 5: 	drawable = context.getResources().getDrawable(R.drawable.tag5);
					break;
		}
		return drawable;
	}
	
	public static void formatAddress(Address result) {
		Bundle extras = new Bundle();
		StringBuffer formattedAddress = new StringBuffer();
		
		if (result.getMaxAddressLineIndex() > -1) {
			formattedAddress.append(result.getAddressLine(0));
		}
		
		formattedAddress.append(result.getLocality() == null ? "" : (" " + result.getLocality()));
		formattedAddress.append(result.getAdminArea() == null ? "" : (" " + result.getAdminArea()));
		
		if (formattedAddress.length() == 0) {
			formattedAddress.append(result.getLatitude());
			formattedAddress.append(", ");
			formattedAddress.append(result.getLongitude());
		}
		
		extras.putString("formatted_address", formattedAddress.toString());
		result.setExtras(extras);
		
		if (result.getExtras() == null) {
			Log.e(TAG, "result extras is null");
		}
	}
	
	
	public static String getAddressText(Address address) {
		// Hacky "if" statement that displays the address if it's not a Google Place
		String addressText = address.getFeatureName();
		if (address.getThoroughfare() != null) {
			addressText = address.getThoroughfare();
			if (address.getSubThoroughfare() != null) {
				addressText = address.getSubThoroughfare() + " " + addressText;
			}
		}
		return addressText;
	}
}
