package com.tutorialacademy.rest.database.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tutorialacademy.rest.database.dao.UserDAO;
import com.tutorialacademy.rest.exception.UserExistingException;
import com.tutorialacademy.rest.exception.UserNotFoundException;
import com.tutorialacademy.rest.model.User;
import com.tutorialacademy.rest.model.UserSecurity;

public class SqlUserDAO implements UserDAO {
	final static Logger logger = Logger.getLogger( SqlUserDAO.class );
	
	private Connection connection = null;
	
	public SqlUserDAO( com.tutorialacademy.rest.database.connection.Connection connection ) {
		this.connection = (Connection) connection.get();
	}

	@Override
	public boolean createUser( UserSecurity user ) throws UserExistingException {
		logger.debug( "createUser: " + user.getEmail() );
		
		PreparedStatement stmt = null;
		
	    try {
	    	
			// check if user already registered
			try {
				if( getUserIdByEmail( user.getEmail() ) != null ) {
					throw new UserExistingException( user.getEmail() );
				}
			}
			// continue if no user found
			catch( UserNotFoundException e) {}
	    	
	    	stmt = connection.prepareStatement( "INSERT INTO USER(" + 
	    									    "email,firstname,lastname,password,role) VALUES" +
	    									    "(?,?,?,?,?)" );
	    	stmt.setString( 1, user.getEmail() );
	    	stmt.setString( 2, user.getFirstname() );
	    	stmt.setString( 3, user.getLastname() );
	    	stmt.setString( 4, user.getPassword() );
	    	stmt.setString( 5, user.getRole() );
		    stmt.executeUpdate();
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return true;
	}

	@Override
	public String getUserIdByEmail(String email) throws UserNotFoundException {
		logger.debug( "getUserIdByEmail: " + email );
		
		String id = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
	    try {
	    	stmt = connection.prepareStatement( "SELECT id FROM USER WHERE email=?;" );
	    	stmt.setString(1, email);
		    rs = stmt.executeQuery();
		    
		    if( rs.next() ) {
		    	id = String.valueOf( rs.getInt("id") );
		    }
		    else {
		    	throw new UserNotFoundException( email );
		    }
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return id;
	}

	@Override
	public User getUser(String id) throws UserNotFoundException {
		logger.debug( "getUser: " + id );
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		User user = null;
		
	    try {
	    	stmt = connection.prepareStatement( "SELECT id, firstname, lastname, email FROM USER WHERE id=?;" );
	    	stmt.setString(1, id);
		    rs = stmt.executeQuery();
		    
		    if( rs.next() ) {
		    	String userId = String.valueOf( rs.getInt("id") );
		    	String email = rs.getString("email");
		    	String firstname = rs.getString("firstname");
		    	String lastname = rs.getString("lastname");
		    	
		    	user = new User(userId, email, firstname, lastname );
		    }
		    else {
		    	throw new UserNotFoundException( id );
		    }
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return user;
	}

	@Override
	public List<User> getAllUsers() {
		logger.debug( "getAllUsers" );
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<User> user = new ArrayList<User>();
		
	    try {
	    	stmt = connection.prepareStatement( "SELECT id, firstname, lastname, email FROM USER;" );
		    rs = stmt.executeQuery();
		    
		    while( rs.next() ) {
		    	String userId = String.valueOf( rs.getInt("id") );
		    	String email = rs.getString("email");
		    	String firstname = rs.getString("firstname");
		    	String lastname = rs.getString("lastname");
		    	
		    	user.add( new User( userId, email, firstname, lastname ) );
		    }
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return user;
	}

	@Override
	public UserSecurity getUserAuthentication( String id ) throws UserNotFoundException {
		logger.debug( "getUserAuthentication: " + id );
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserSecurity userSecurity = null;
		
	    try {
	    	stmt = connection.prepareStatement( "SELECT email, password, token, role FROM USER WHERE id=?;" );
	    	stmt.setString(1, id);
		    rs = stmt.executeQuery();
		    
		    if( rs.next() ) {
		    	String email = rs.getString("email");
		    	String password = rs.getString("password");
		    	String token = rs.getString("token");
		    	String role = rs.getString("role");
		    	
		    	userSecurity = new UserSecurity( email, password, token, role );
		    }
		    else {
		    	throw new UserNotFoundException( id );
		    }
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return userSecurity;
	}

	@Override
	public boolean setUserAuthentication( UserSecurity user ) throws UserNotFoundException {
		logger.debug( "setUserAuthentication: " + user.getId() );
		
		PreparedStatement stmt = null;
		
	    try {
	    	// prepare query
	    	StringBuffer query = new StringBuffer();
	    	query.append( "UPDATE USER SET " );
	    	
	    	boolean comma = false;
	    	List<String> prepare = new ArrayList<String>();
	    	if( user.getPassword() != null ) {
	    		query.append( "password=?" );
	    		comma = true;
	    		prepare.add( user.getPassword() );
	    	}
	    	
	    	if( user.getToken() != null ) {
	    		if( comma ) query.append(",");
	    		query.append( "token=?" );
	    		comma = true;
	    		prepare.add( user.getToken() );
	    	}
	    	
	    	if( user.getRole() != null ) {
	    		if( comma ) query.append(",");
	    		query.append( "role=?" );
	    		prepare.add( user.getRole() );
	    	}
	    	
	    	query.append(" WHERE id=?");
	    	stmt = connection.prepareStatement( query.toString() );
	    	
	    	for( int i = 0; i < prepare.size(); i++ ) {
	    		stmt.setString( i+1, prepare.get(i) );
	    	}
	    	
	    	stmt.setInt( prepare.size() + 1, Integer.parseInt( user.getId() ) );
	    	
	    	stmt.executeUpdate();
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return true;
	}

	@Override
	public boolean updateUser( User user ) throws UserNotFoundException {
		logger.debug( "updateUser: " + user.getId() );
		
		PreparedStatement stmt = null;
		
	    try {
	    	// prepare query
	    	StringBuffer query = new StringBuffer();
	    	query.append( "UPDATE USER SET " );
	    	
	    	boolean comma = false;
	    	List<String> prepare = new ArrayList<String>();
	    	if( user.getFirstname() != null ) {
	    		query.append( "firstname=?" );
	    		comma = true;
	    		prepare.add( user.getFirstname() );
	    	}
	    	
	    	if( user.getLastname() != null ) {
	    		if( comma ) query.append(",");
	    		query.append( "lastname=?" );
	    		comma = true;
	    		prepare.add( user.getLastname() );
	    	}
	    	
	    	if( user.getEmail() != null ) {
	    		if( comma ) query.append(",");
	    		query.append( "email=?" );
	    		prepare.add( user.getEmail() );
	    	}
	    	
	    	query.append(" WHERE id=?");
	    	stmt = connection.prepareStatement( query.toString() );
	    	
	    	for( int i = 0; i < prepare.size(); i++ ) {
	    		stmt.setString( i+1, prepare.get(i) );
	    	}
	    	
	    	stmt.setInt( prepare.size() + 1, Integer.parseInt( user.getId() ) );
	    	
	    	stmt.executeUpdate();
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
		
		return true;
	}

	@Override
	public boolean deleteUser( String id ) throws UserNotFoundException {
		logger.debug( "deleteUser: " + id );
		
		PreparedStatement stmt = null;
		
	    try {
	    	
	    	stmt = connection.prepareStatement( "DELETE FROM USER WHERE id=?" );
	    	stmt.setString( 1, id );
	    	
		    stmt.executeUpdate();
		    
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	    
	    return true;
	}
}
