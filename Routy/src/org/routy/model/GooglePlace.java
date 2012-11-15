package org.routy.model;

/**
 * Data container for results obtained from calls to the Google Places API.
 * 
 * @author jtran
 *
 */
public class GooglePlace {

	private String name;
	private String address;
	
	
	public GooglePlace() {
		super();
	}
	
	
	public String getPlaceName() {
		return name;
	}
	
	public String getPlaceAddress() {
		return address;
	}

}
