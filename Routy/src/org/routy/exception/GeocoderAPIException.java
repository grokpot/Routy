package org.routy.exception;

@SuppressWarnings("serial")
public class GeocoderAPIException extends Exception {

	
	public GeocoderAPIException() {
		super();
	}
	
	
	public GeocoderAPIException(String message) {
		super(message);
	}
	
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
