package com.tutorialacademy.rest.model;

public class UserSecurity extends User {
	
	private String password = null;
	private String token = null;
	private String role = null;
	
	public UserSecurity() {}
	
	public UserSecurity( String password, String token ) {
		this.password = password;
		this.token = token;
	}
	
	public UserSecurity( String password, String token, String role ) {
		this.password = password;
		this.token = token;
		this.role = role;
	}
	
	public UserSecurity( String email, String password, String token, String role ) {
		this.setEmail( email );
		this.password = password;
		this.token = token;
		this.role = role;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserSecurity [id="+ this.getId() + ", email=" + this.getEmail() + ", role=" + role + ", password=" + password + ", token=" + token + "]";
	}

}
