package com.tutorialacademy.rest.exception;

public class UserExistingException extends RuntimeException {
	
	private static final long serialVersionUID = 2481381224355081751L;

	public UserExistingException( String user ) {
		super( "User already registered: " + user );
	}
}
