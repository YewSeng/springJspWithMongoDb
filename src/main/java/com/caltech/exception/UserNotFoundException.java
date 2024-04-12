package com.caltech.exception;

public class UserNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1701325076325003614L;

	public UserNotFoundException(String message) {
		super(message);
	}
}
