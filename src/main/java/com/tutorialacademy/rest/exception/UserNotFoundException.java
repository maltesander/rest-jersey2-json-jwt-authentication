package com.tutorialacademy.rest.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8078059893066017473L;
	
	public UserNotFoundException( String user ) {
		super("User not found: " + user );
	}
	
}
