package org.routy.exception;

public class NoNetworkConnectionException extends Exception {

	public NoNetworkConnectionException() {
		super();
	}
	
	
	public NoNetworkConnectionException(String message) {
		super(message);
	}
	
	
	public String getMessage() {
		return super.getMessage();
	}
}
