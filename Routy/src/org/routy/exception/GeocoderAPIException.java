package org.routy.exception;

public class GeocoderAPIException extends Exception {

	
	public GeocoderAPIException() {
		super();
	}
	
	
	public GeocoderAPIException(String message) {
		super(message);
	}
	
	
	public String getMessage() {
		return super.getMessage();
	}
}
