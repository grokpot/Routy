package org.routy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.Util;

import android.location.Address;
import android.util.Log;

public class AddressModel {

	private static final String TAG = "AddressModel";
	
	private static final AddressModel addressModel = new AddressModel();
	
	private Address origin;
	private List<Address> destinations;
	
	private AddressModel() {
		super();
	}
	
	public static AddressModel getSingleton() {
		return addressModel;
	}

	public Address getOrigin() {
		return origin;
	}

	public void setOrigin(Address origin) {
		Log.v(TAG, "setting origin to " + (origin.getExtras().getString("formatted_address") == null ? origin.getExtras().getString("address_string") : origin.getExtras().getString("formatted_address")));
		Log.v(TAG, "origin status is " + origin.getExtras().getString("validation_status"));
		this.origin = origin;
	}
	
	public boolean isOriginValid() {
		return origin != null && origin.getExtras() != null && AddressStatus.VALID.toString().equals(origin.getExtras().getString("validation_status"));
	}
	
	public void addDestination(Address destination) {
		destinations.add(destination);
	}

	public List<Address> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Address> destinations) {
		this.destinations = destinations;
	}
	
	public String getOriginJSON() {
		//Return a JSON string that can be used to save/load the Origin
		return Util.writeAddressToJson(origin);
	}
	
	public String getDestinationsJSON() {
		return Util.addressListToJSON(getDestinations());
	}
	
	public void loadModel(String originJson, String destJson) {
		loadOriginJSON(originJson);
		loadDestinationsJSON(destJson);
	}
	
	private void loadOriginJSON(String json) {
		//Parse the JSON string to load an Address into origin
		if (json != null && json.length() > 0) {
			setOrigin(Util.readAddressFromJson(json));
			Log.v(TAG, "origin loaded into AddressModel.");
		} else {
			Log.v(TAG, "no saved origin JSON");
		}
	}
	
	private void loadDestinationsJSON(String json) {
		if (json != null && json.length() > 0) {
			setDestinations(Util.jsonToAddressList(json));
			Log.v(TAG, "destinations loaded into AddressModel.");
		} else {
			Log.v(TAG, "no saved destinations JSON");
			destinations = new ArrayList<Address>();
		}
	}
}
