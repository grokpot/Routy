package org.routy.exception;

public class AmbiguousAddressException extends Exception {

	public AmbiguousAddressException() {
		super();
	}
	
	
	public AmbiguousAddressException(String message) {
		super(message);
	}
	
	
	public String getMessage() {
		return super.getMessage();
	}
}
