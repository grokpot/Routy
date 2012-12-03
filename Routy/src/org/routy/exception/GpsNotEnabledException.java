package org.routy.exception;

@SuppressWarnings("serial")
public class GpsNotEnabledException extends Exception {


	public GpsNotEnabledException() {
		super();
	}
	
	public GpsNotEnabledException(String message) {
		super(message);
	}
}
