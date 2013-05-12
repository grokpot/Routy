package org.routy.exception;

@SuppressWarnings("serial")
public class NoLocationProviderException extends Exception {

	public NoLocationProviderException(String message) {
		super(message);
	}
	
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
