package org.routy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.JsonWriter;
import android.util.Log;

public class UtilTest extends AndroidTestCase {

	private final String TAG = "UtilTest";
	
	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void testAddressToJson() {
		Address dummy = new Address(Locale.getDefault());
		dummy.setFeatureName("Dummy Address");
		dummy.setLatitude(30.12345);
		dummy.setLongitude(-97.12345);
		
		Bundle extras = new Bundle();
		extras.putString("formatted_address", "Formatted, pretty Dummy Address");
		dummy.setExtras(extras);
		
		StringWriter sWriter = new StringWriter();
		JsonWriter jWriter = new JsonWriter(sWriter);
		Util.writeAddress(jWriter, dummy);
		String json = sWriter.toString();
		try {
			sWriter.close();
		} catch (IOException e) {
			fail("IOException when closing the StringWriter");
		}
		assertNotNull(json);
		System.out.println(json);
	}
	
	public void testAddressToJsonExternal() {
		Address dummy = new Address(Locale.getDefault());
		dummy.setFeatureName("Dummy Address");
		dummy.setLatitude(30.12345);
		dummy.setLongitude(-97.12345);
		
		Bundle extras = new Bundle();
		extras.putString("formatted_address", "Formatted, pretty Dummy Address");
		dummy.setExtras(extras);
		
		String json = Util.writeAddressToJson(dummy);
//		System.out.println(json);
		Log.v(TAG, "single address: " + json);
	}
	
	public void testAddressListToJson() {
		Address dummy1 = new Address(Locale.getDefault());
		dummy1.setFeatureName("Dummy Address");
		dummy1.setLatitude(30.12345);
		dummy1.setLongitude(-97.12345);
		
		Bundle extras = new Bundle();
		extras.putString("formatted_address", "Formatted, pretty Dummy Address");
		dummy1.setExtras(extras);
		
		Address dummy2 = new Address(Locale.getDefault());
		dummy2.setFeatureName("Dummy Address TWO");
		dummy2.setLatitude(31.54321);
		dummy2.setLongitude(-98.54321);
		
		Bundle extras2 = new Bundle();
		extras2.putString("formatted_address", "Another pretty Dummy Address");
		dummy2.setExtras(extras2);
		
		List<Address> addresses = new ArrayList<Address>();
		addresses.add(dummy1);
		addresses.add(dummy2);
		
		StringWriter sWriter = new StringWriter();
		String json = Util.addressListToJSON(addresses);
		try {
			sWriter.close();
		} catch (IOException e) {
			fail("IOException when closing the StringWriter");
		}
		System.out.println(json);
	}
	
	public void testJsonToAddressExternal() {
		String json = "{\"persisted_address\":{\"feature_name\":\"Dummy Address\",\"latitude\":30.12345,\"longitude\":-97.12345,\"formatted_address\":\"Formatted, pretty Dummy Address\",\"valid_status\":2}}";
		
		Address dummy1 = Util.readAddressFromJson(json);
		assertEquals("feature name mismatch", "Dummy Address", dummy1.getFeatureName());
		assertEquals("latitude mismatch", 30.12345, dummy1.getLatitude());
		assertEquals("longitude mismatch", -97.12345, dummy1.getLongitude());
		
		Bundle extras = dummy1.getExtras();
		assertNotNull(extras);
		assertEquals("formatted_address mismatch", "Formatted, pretty Dummy Address", extras.getString("formatted_address"));
	}
	
	public void testJsonToAddressList() {
		String json = "{\"persisted_addresses\":[{\"persisted_address\":{\"feature_name\":\"Dummy Address\",\"latitude\":30.12345,\"longitude\":-97.12345,\"formatted_address\":\"Formatted, pretty Dummy Address\"}},{\"persisted_address\":{\"feature_name\":\"Dummy Address TWO\",\"latitude\":31.54321,\"longitude\":-98.54321,\"formatted_address\":\"Another pretty Dummy Address\"}}]}";
		
		List<Address> addresses = Util.jsonToAddressList(json);
		assertEquals("address list size mismatch", 2, addresses.size());
		
		Address dummy1 = addresses.get(0);
		assertEquals("feature name mismatch", "Dummy Address", dummy1.getFeatureName());
		assertEquals("latitude mismatch", 30.12345, dummy1.getLatitude());
		assertEquals("longitude mismatch", -97.12345, dummy1.getLongitude());
		
		Bundle extras = dummy1.getExtras();
		assertNotNull(extras);
		assertEquals("formatted_address mismatch", "Formatted, pretty Dummy Address", extras.getString("formatted_address"));
		
		
		Address dummy2 = addresses.get(1);
		assertEquals("feature name mismatch", "Dummy Address TWO", dummy2.getFeatureName());
		assertEquals("latitude mismatch", 31.54321, dummy2.getLatitude());
		assertEquals("longitude mismatch", -98.54321, dummy2.getLongitude());
		
		Bundle extras2 = dummy2.getExtras();
		assertNotNull(extras2);
		assertEquals("formatted_address mismatch", "Another pretty Dummy Address", extras2.getString("formatted_address"));
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
