package org.routy;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.view.DestinationRowView;

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
			jWriter.name("feature_name").value(address.getFeatureName());
			jWriter.name("latitude").value(address.getLatitude());
			jWriter.name("longitude").value(address.getLongitude());
			
			Bundle extras = address.getExtras();
			if (extras != null) {
				String formattedAddress = extras.getString("formatted_address");
				
				if (formattedAddress != null) {
					jWriter.name("formatted_address").value(formattedAddress);
				}
				
				// Gotta save the validation status so we know if we have to re-validate or not
				jWriter.name("valid_status").value(extras.getInt("valid_status", DestinationRowView.NOT_VALIDATED));
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
		// TODO
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
			
			return addresses;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() + "-- IOException reading JSON address list");
		}
		
		return null;
	}
	

	static Address readAddress(JsonReader jReader) {
		// TODO
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
						Bundle extras = new Bundle();
						extras.putString("formatted_address", jReader.nextString());
						address.setExtras(extras);
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
}
