package com.tutorialacademy.rest.database.dao;

import java.util.List;

import com.tutorialacademy.rest.exception.UserExistingException;
import com.tutorialacademy.rest.exception.UserNotFoundException;
import com.tutorialacademy.rest.model.User;
import com.tutorialacademy.rest.model.UserSecurity;

public interface UserDAO {
	public boolean createUser( UserSecurity user ) throws UserExistingException;
	
	public String getUserIdByEmail( String email ) throws UserNotFoundException;
	public User getUser( String id ) throws UserNotFoundException;
	
	public List<User> getAllUsers();
	
	public UserSecurity getUserAuthentication( String id ) throws UserNotFoundException;
	public boolean setUserAuthentication( UserSecurity user ) throws UserNotFoundException;
	
	public boolean updateUser( User user ) throws UserNotFoundException;
	public boolean deleteUser( String id ) throws UserNotFoundException;
}
