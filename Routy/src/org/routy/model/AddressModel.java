package org.routy.model;

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
		Log.v(TAG, "setting origin to " + (origin.getMaxAddressLineIndex() > -1 ? origin.getAddressLine(0) : origin.getSubThoroughfare()));
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
	
	public String getJSON() {
		//TODO Return a JSON string that can be used to save/load the AddressModel
		return null;
	}
	
	//XXX this can be incorporated into #getJSON()
	public String getOriginJSON() {
		//Return a JSON string that can be used to save/load the Origin
		return Util.writeAddressToJson(origin);
	}
	
	public void loadModel(String json) {
		//TODO
		loadOriginJSON(json);
	}
	
	private void loadOriginJSON(String json) {
		//Parse the JSON string to load an Address into origin
		Address newOrigin = Util.readAddressFromJson(json);
		origin = newOrigin;
		
		Log.v(TAG, "origin loaded.  address: " + origin.getAddressLine(0) + " -- status: " + origin.getExtras().getString("validation_status", "no status found"));
	}
}
