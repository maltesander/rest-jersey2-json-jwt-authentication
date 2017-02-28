package com.tutorialacademy.rest.database.connection;

import com.tutorialacademy.rest.config.DbConfig;
import com.tutorialacademy.rest.database.connection.orientdb.OrientDbConnection;
import com.tutorialacademy.rest.database.connection.sqlite.SQLiteConnection;

public class ConnectionFactory {
	
	private static Connection connection = null;
	
	public static Connection getConnection() {
		if( connection != null ) return connection;
		
		switch( DbConfig.getDbType() ) {
			case ORIENTDB:
				connection = new OrientDbConnection();
			case SQLITE:
				connection = new SQLiteConnection();
			default:
				break;
		}

		// open connection
		connection.open();
		
		return connection;
	}
}
