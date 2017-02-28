package com.tutorialacademy.rest.database.dao;

import com.tutorialacademy.rest.config.DbConfig;
import com.tutorialacademy.rest.database.connection.Connection;
import com.tutorialacademy.rest.database.connection.ConnectionFactory;
import com.tutorialacademy.rest.database.dao.gremlin.GremlinUserDAO;
import com.tutorialacademy.rest.database.dao.sqlite.SqlUserDAO;

public class UserDAOFactory {
	
	public static UserDAO getUserDAO() {
		// get connection
		Connection connection = ConnectionFactory.getConnection();
		
		// use driver specified according to database
		switch( DbConfig.getDbType() ) {
			case ORIENTDB:
				return new GremlinUserDAO( connection );
			case SQLITE:
				return new SqlUserDAO( connection );
			default:
				// should not happen: we test for correct input in DbConfig.java
				return null;
		}
	}
}
