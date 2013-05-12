package org.routy.exception;

@SuppressWarnings("serial")
public class NoInternetConnectionException extends Exception {

	public NoInternetConnectionException() {
		super();
	}
	
	
	public NoInternetConnectionException(String message) {
		super(message);
	}
	
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
