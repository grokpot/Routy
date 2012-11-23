package org.routy.exception;

/**
 * An exception that Routy cannot recover from and that the 
 * user cannot fix.  Log the causing error and notify the user 
 * that something went wrong and it's not their fault.
 * 
 * @author jtran
 *
 */
@SuppressWarnings("serial")
public class RoutyException extends Exception {

	public RoutyException() {
		super();
	}
	
	public RoutyException(String message) {
		super(message);
	}
	
	@Override
  public String getMessage() {
		return super.getMessage();
	}
}
