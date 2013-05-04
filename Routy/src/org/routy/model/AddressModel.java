package org.routy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.routy.Util;
import org.routy.callback.ValidateAddressCallback;

import android.location.Address;
import android.util.Log;

public class AddressModel {

	private static final String TAG = "AddressModel";
	
	private static final AddressModel addressModel = new AddressModel();
	
	private RoutyAddress origin;
	private List<RoutyAddress> destinations;
	private String unvalidatedDestEntry;
	
	private AddressModel() {
		super();
	}
	
	public static AddressModel getSingleton() {
		return addressModel;
	}

	public RoutyAddress getOrigin() {
		return origin;
	}

	public void setOrigin(RoutyAddress origin) {
		if (origin != null) {
			Log.v(TAG, "setting origin to " + (origin.getExtras().getString("formatted_address") == null ? origin.getExtras().getString("address_string") : origin.getExtras().getString("formatted_address")));
			Log.v(TAG, "origin status is " + origin.getStatus().toString());
		}
		this.origin = origin;
	}
	
	public boolean isOriginValid() {
		return origin != null && origin.isValid();
	}
	
	public void addDestination(RoutyAddress destination) {
		destinations.add(destination);
	}

	public List<RoutyAddress> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<RoutyAddress> destinations) {
		this.destinations = destinations;
	}
	
	public String getUnvalidatedDestEntry() {
		return unvalidatedDestEntry;
	}

	public void setUnvalidatedDestEntry(String unvalidatedDestEntry) {
		this.unvalidatedDestEntry = unvalidatedDestEntry;
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
			destinations = new ArrayList<RoutyAddress>();
		}
	}

	public void removeDestination(int indexInLayout) {
		if (indexInLayout >= 0 && indexInLayout < destinations.size()) {
			destinations.remove(indexInLayout);
		}
	}

	public void setDestinationAt(int indexInLayout, RoutyAddress validatedAddress) {
		if (indexInLayout >= 0 && indexInLayout < destinations.size() && validatedAddress != null) {
			destinations.set(indexInLayout, validatedAddress);
		}
	}

	public boolean hasDestinations() {
		return getDestinations() != null && getDestinations().size() > 0;
	}
}
