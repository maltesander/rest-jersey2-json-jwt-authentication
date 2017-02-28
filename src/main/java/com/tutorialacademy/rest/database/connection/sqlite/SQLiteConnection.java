package com.tutorialacademy.rest.database.connection.sqlite;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import com.tutorialacademy.rest.config.DbConfig;
import com.tutorialacademy.rest.security.PasswordSecurity;


public class SQLiteConnection implements com.tutorialacademy.rest.database.connection.Connection {

	final static Logger logger = Logger.getLogger( SQLiteConnection.class );
	
	Connection connection = null;
	
	@Override
	public Object get() {
		return connection;
	}

	@Override
	public boolean open() {
		connection = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      
	      SQLiteConfig config = new SQLiteConfig();
	      config.setOpenMode(SQLiteOpenMode.READWRITE);
	      config.setOpenMode(SQLiteOpenMode.CREATE);
	      // serialized mode for concurrent access - TODO: use connection pooling
	      config.setOpenMode(SQLiteOpenMode.FULLMUTEX);
	      
	      String path = null;
	      
	      if( DbConfig.getDbPath() != null ) {
	    	  path = DbConfig.getDbPath();
	      }
	      else if( DbConfig.getDbName() != null ) {
	    	  path = "jdbc:sqlite:" + DbConfig.getDbName();
	      }
	      else {
	    	  path = "jdbc:sqlite:tutorial-academy";
	      }
	      
	      connection = DriverManager.getConnection( path, config.toProperties() );
	      
	    } catch ( Exception e ) {
	      logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    logger.debug("Opened database successfully");
	    
	    // database openend - init database schema
	    createUserSchema();
	    // create admin user
	    createAdminUser();
	    
	    return true;
	}

	@Override
	public boolean close() {
		if( connection != null ) {
			try {
				connection.close();
			} catch ( SQLException e ) {
				logger.debug( "Databank not closed correctly: " + e.getMessage() );
			}
			return true;
		}
		return false;
	}
	
	private boolean checkForUserSchema() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT name FROM sqlite_master WHERE type='table' AND name='USER';" );
			if (rs.next()) {
				return true;
			}
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
			logger.debug( e.getClass().getName() + ": " + e.getMessage() );
		}
		return false;
	}
	
	private void createUserSchema() {
		if( checkForUserSchema() == true ) return;
		
		try {
			Statement stmt = connection.createStatement();
			String sql = "CREATE TABLE USER( " +
					     " ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
					     " EMAIL 		  TEXT NOT NULL, " +
					     " FIRSTNAME      TEXT, " + 
					     " LASTNAME       TEXT, " +
					     " PASSWORD		  TEXT NOT NULL, " + 
					     " TOKEN          TEXT, " + 
					     " ROLE           TEXT)"; 
			stmt.executeUpdate( sql );
			stmt.close();
		} catch ( Exception e ) {
			logger.debug( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	private boolean checkForAdminUser() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT email FROM USER  WHERE email='contact@tutorial-academy.com';" );
			if (rs.next()) {
				return true;
			}
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
			logger.debug( e.getClass().getName() + ": " + e.getMessage() );
		}
		return false;
	}
	
	private void createAdminUser() {
		if( checkForAdminUser() ) return;
		
		PreparedStatement stmt = null;
		
		try {
			stmt = connection.prepareStatement(
	    	 "INSERT INTO USER(" + 
			 "email,firstname,lastname,password,role) VALUES" +
			 "(?,?,?,?,?)" );
			
			stmt.setString( 1, "contact@tutorial-academy.com" );
			stmt.setString( 2, "tutorial" );
			stmt.setString( 3, "academy" );
			stmt.setString( 4, PasswordSecurity.generateHash( "secret" ) );
			stmt.setString( 5, "admin" );
			stmt.executeUpdate();
	    } catch ( SQLException e ) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
	    } catch (NoSuchAlgorithmException e) {
	    	logger.debug( e.getClass().getName() + ": " + e.getMessage() );
		} catch (InvalidKeySpecException e) {
			logger.debug( e.getClass().getName() + ": " + e.getMessage() );
		}
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				logger.warn( e.getMessage() );
			}
	    }
	}

}
