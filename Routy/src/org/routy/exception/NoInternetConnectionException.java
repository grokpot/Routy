package org.routy.exception;

public class NoInternetConnectionException extends Exception {

	public NoInternetConnectionException() {
		super();
	}
	
	
	public NoInternetConnectionException(String message) {
		super(message);
	}
	
	
	public String getMessage() {
		return super.getMessage();
	}
}
