package com.tutorialacademy.rest.database.dao.gremlin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tutorialacademy.rest.database.connection.Connection;
import com.tutorialacademy.rest.database.dao.UserDAO;
import com.tutorialacademy.rest.exception.UserExistingException;
import com.tutorialacademy.rest.exception.UserNotFoundException;
import com.tutorialacademy.rest.model.User;
import com.tutorialacademy.rest.model.UserSecurity;

public class GremlinUserDAO implements UserDAO {

	final static Logger logger = Logger.getLogger( GremlinUserDAO.class );
	
	private Connection connection = null;
	
	public GremlinUserDAO( Connection connection ) {
		this.connection = connection;
	}
	
	public boolean createUser( UserSecurity user ) {
		Graph graph = (Graph) connection.get();
		logger.debug("createUser: " + user.getEmail() );
		
		try {
			// check if user already registered
			try {
				if( getUserIdByEmail( user.getEmail() ) != null ) {
					throw new UserExistingException( user.getEmail() );
				}
			}
			// continue if no user found
			catch( UserNotFoundException e) {}
			// create user vertex
			Vertex v = graph.addVertex(null);
			
			// type user
			v.setProperty( GremlinDAOSpec.UNIVERSAL_PROPERTY_TYPE, GremlinDAOSpec.USER_CLASS );
			v.setProperty( GremlinDAOSpec.USER_PROPERTY_FIRST_NAME, user.getFirstname() );
			v.setProperty( GremlinDAOSpec.USER_PROPERTY_LAST_NAME, user.getLastname() );
			v.setProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL, user.getEmail() );
			v.setProperty( GremlinDAOSpec.USER_PROPERTY_PASSWORD, user.getPassword() );
			v.setProperty( GremlinDAOSpec.USER_PROPERTY_ROLE, user.getRole() );
			
			return true;			
		}
		finally {
			graph.shutdown();
		}
	}
	
	public String getUserIdByEmail( String email ) {
		Graph graph = (Graph) connection.get();
		logger.debug("getUserIdByEmail: " + email );
		
		try {
		
			Iterable<Vertex> iterable = graph.getVertices( GremlinDAOSpec.USER_PROPERTY_EMAIL, email );
			Iterator<Vertex> it = iterable.iterator();
			
			if( it.hasNext() ) {
				return it.next().getId().toString();
			}
			else {
				throw new UserNotFoundException( email );
			}
		}
		finally {
			graph.shutdown();
		}
	}

	public User getUser( String id ) {
		Graph graph = (Graph) connection.get();
		logger.debug("getUser: " + id );
		
		try {
			Vertex v = graph.getVertex( id );
			
			if( v == null ) {
				throw new UserNotFoundException( id );
			}

			User user = new User( v.getId().toString(),
								  v.getProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL ),
								  v.getProperty( GremlinDAOSpec.USER_PROPERTY_FIRST_NAME ).toString(),
								  v.getProperty( GremlinDAOSpec.USER_PROPERTY_LAST_NAME ).toString() 
								);
			return user;
		}
		finally {
			graph.shutdown();
		}
	}
	
	@Override
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		Graph graph = (Graph) connection.get();
		logger.debug("getAllUsers" );
		
		try {
			Iterable<Vertex> iterable = graph.getVertices( GremlinDAOSpec.UNIVERSAL_PROPERTY_TYPE, GremlinDAOSpec.USER_CLASS );
			Iterator<Vertex> it = iterable.iterator();
			
			while( it.hasNext() ) {
				Vertex v = it.next();
				User user = new User( v.getId().toString(),
									  v.getProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL ),
									  v.getProperty( GremlinDAOSpec.USER_PROPERTY_FIRST_NAME ).toString(),
									  v.getProperty( GremlinDAOSpec.USER_PROPERTY_LAST_NAME ).toString() 
									);
				users.add(user);
			}

			return users;
		}
		finally {
			graph.shutdown();
		}
	}
	

	@Override
	public UserSecurity getUserAuthentication( String id ) throws UserNotFoundException {
		Graph graph = (Graph) connection.get();
		logger.debug("getUserAuthentication: " + id );
		
		try {
			Vertex v = graph.getVertex( id );
			
			if( v == null ) throw new UserNotFoundException( id );
			
			Object oEmail = v.getProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL );
			Object oPassword = v.getProperty( GremlinDAOSpec.USER_PROPERTY_PASSWORD );
			Object oToken = v.getProperty( GremlinDAOSpec.USER_PROPERTY_TOKEN );
			Object oRole = v.getProperty( GremlinDAOSpec.USER_PROPERTY_ROLE );
			
			String email = null;
			String password = null;
			String token = null;
			String role = null;
			
			if( oEmail != null )
				email = oEmail.toString();
			if( oPassword != null )
				password = oPassword.toString();
			if( oToken != null )
				token = oToken.toString();
			if( oRole != null )
				role = oRole.toString();
			
			UserSecurity user = new UserSecurity( email, password, token, role );
			
			return user;
		}
		finally {
			graph.shutdown();
		}
	}
	
	@Override
	public boolean setUserAuthentication( UserSecurity user ) throws UserNotFoundException {
		Graph graph = (Graph) connection.get();
		logger.debug("setUserAuthentication: " + user.getId() );
		
		try {
			Vertex v = graph.getVertex( user.getId() );
			
			if( v == null ) throw new UserNotFoundException( user.getId() );

			if( user.getPassword() != null ) {
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_PASSWORD,  user.getPassword() );
			}
			
			if( user.getToken() != null ) {
				v.setProperty(GremlinDAOSpec.USER_PROPERTY_TOKEN, user.getToken() );
			}
			
			if( user.getRole() != null ) {
				v.setProperty(GremlinDAOSpec.USER_PROPERTY_ROLE, user.getRole() );
			}
			
			return true;
		}
		finally {
			graph.shutdown();
		}
	}

	public boolean updateUser( User user ) {
		Graph graph = (Graph) connection.get();
		logger.debug("updateUser: " + user.getId() );
		
		try{
			Vertex v = graph.getVertex( user.getId() );
			
			if( v== null ) throw new UserNotFoundException( user.getEmail() );
			
			if( user.getEmail() != null )
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL, user.getEmail() );
			if( user.getFirstname() != null )
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_FIRST_NAME, user.getFirstname() );
			if( user.getLastname() != null )
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_LAST_NAME, user.getLastname() );
			
			return true;
		}
		finally {
			graph.shutdown();
		}
	}

	public boolean deleteUser( String id ) {
		Graph graph = (Graph) connection.get();
		logger.debug("deleteUser: " + id );
		
		try {
			Vertex v = graph.getVertex( id );
			
			if( v== null ) throw new UserNotFoundException( id );
			
			v.remove();
			return true;
		}
		finally {
			graph.shutdown();
		}
	}

}
