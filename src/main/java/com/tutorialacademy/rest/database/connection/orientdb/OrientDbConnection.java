package com.tutorialacademy.rest.database.connection.orientdb;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tutorialacademy.rest.config.DbConfig;
import com.tutorialacademy.rest.database.connection.Connection;
import com.tutorialacademy.rest.database.dao.gremlin.GremlinDAOSpec;
import com.tutorialacademy.rest.security.PasswordSecurity;

public class OrientDbConnection implements Connection {

	final static Logger logger = Logger.getLogger( OrientDbConnection.class );
	
	private OrientGraphFactory factory = null;

	public Object get() {
		return factory.getTx();
	}

	public boolean open() {
		String path = null;
		
		if( DbConfig.getDbPath() != null )
			path = DbConfig.getDbPath();
		else if( DbConfig.getDbName() != null ) {
			// get local memory
			path =  "memory:" + DbConfig.getDbName();
		}
		else {
			path = "memory:tutorial-academy-db";
		}
		
		factory = new OrientGraphFactory( path, DbConfig.getDbUser(), DbConfig.getDbPassword() ).setupPool( 1, DbConfig.getDbPool() );
		
		setSettings();
		
		return true;
	}

	public boolean close() {
		if( factory != null ) {
			factory.close();
		}
		return true;
	}
	
	private void createAdminUser() {
		OrientGraph tx = factory.getTx();
		try{
			tx = factory.getTx();
			
			String email = "contact@tutorial-academy.com";
			// check if already existing
			Iterable<Vertex> iterable = tx.getVertices( GremlinDAOSpec.USER_PROPERTY_EMAIL, email );
			Iterator<Vertex> it = iterable.iterator();
			
			if( !it.hasNext() ) {
				// create admin user for test purpose
				Vertex v = tx.addVertex( null );
				// type user
				v.setProperty( GremlinDAOSpec.UNIVERSAL_PROPERTY_TYPE, GremlinDAOSpec.USER_CLASS );
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_FIRST_NAME, "tutorial" );
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_LAST_NAME, "academy" );
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_EMAIL, email );
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_PASSWORD, PasswordSecurity.generateHash( "secret" ) );
				v.setProperty( GremlinDAOSpec.USER_PROPERTY_ROLE, "admin" );
			}
			
		} catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		} catch ( InvalidKeySpecException e ) {
			e.printStackTrace();
		}
		finally {
			tx.shutdown();
		}
	}
	
	private void setSettings() {
		// settings
		factory.setUseLightweightEdges( true );
		factory.setUseClassForEdgeLabel( false );
		factory.setUseVertexFieldsForEdgeLabels( false );
		
		OrientGraphNoTx noTx = null;
		
		try{
			noTx = factory.getNoTx();

			// TODO: add admin user for testing
			createAdminUser();
		}
		finally {
			if( noTx != null ) 
				noTx.shutdown();
		}
	}
	
}
