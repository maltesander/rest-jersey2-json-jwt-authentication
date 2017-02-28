package com.tutorialacademy.rest.config;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tutorialacademy.rest.database.def.DataBaseType;


public class DbConfig {

	private static final String XML_TAG_CONFIGURATION = "Configuration";
	private static final String XML_TAG_DB_TYPE 	  = "dbType";
	private static final String XML_TAG_DB_NAME 	  = "dbName";
	private static final String XML_TAG_DB_PATH 	  = "dbPath";
	private static final String XML_TAG_DB_HOST 	  = "dbHost";
	private static final String XML_TAG_DB_PORT 	  = "dbPort";
	private static final String XML_TAG_DB_USER 	  = "dbUser";
	private static final String XML_TAG_DB_PASSWORD   = "dbPassword";
	private static final String XML_TAG_DB_POOL 	  = "dbPool";
	
	private static DataBaseType dbType = null;
	private static String dbName = null;
	private static String dbPath = null;
	private static String dbHost = null;
	private static String dbPort = null; 
	private static String dbUser = null;
	private static String dbPassword = null;
	private static int    dbPool = 10;
	
	static {
		// should be read via console
		parseXML( getRelativeResourcePath( "DbConfig.xml" ) );
	}
	
	private static String getRelativeResourcePath( String resource ) {
		if( resource == null || resource.equals("") ) return null;
		
		return DbConfig.class.getClassLoader().getResource(resource).getPath();
	}
	
	private static String getSingleElement( Element element, String tagName ) {
		Node tagNameNode = element.getElementsByTagName( tagName ).item(0);
		
		if( tagNameNode == null ) 
			return null;
		
		NodeList tagNameNodeChildren = tagNameNode.getChildNodes();
		
		if( tagNameNodeChildren == null || tagNameNodeChildren.getLength() == 0 )
			return null;
		
		return tagNameNodeChildren.item(0).getNodeValue().toLowerCase(); 
	}
	
	private static DataBaseType parseDataBaseType( String dataBaseType ) {
		if( dataBaseType.equalsIgnoreCase( DataBaseType.ORIENTDB.toString() ) ) {
			return DataBaseType.ORIENTDB;
		}
		else if( dataBaseType.equalsIgnoreCase( DataBaseType.SQLITE.toString() ) ) {
			return DataBaseType.SQLITE;
		}
		else {
			throw new  IllegalArgumentException("Not a valid supported dataBase: " + dataBaseType );
		}
	}
	
	private static void parseXML( String path )  {
		Document doc = null;
		try {
			
			File fXmlFile = new File( path );
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		NodeList nodeList = doc.getElementsByTagName( XML_TAG_CONFIGURATION );
		
		for( int i = 0; i < nodeList.getLength(); i++ ) {
			Node node = nodeList.item(i);

			if ( node.getNodeType() == Node.ELEMENT_NODE ) 
			{
				Element element = (Element) node;
				
				// TODO: parse options
				dbType = parseDataBaseType( getSingleElement( element, XML_TAG_DB_TYPE ) );
				dbName = getSingleElement( element, XML_TAG_DB_NAME );
				
				dbPath = getSingleElement( element, XML_TAG_DB_PATH ); 
				dbHost = getSingleElement( element, XML_TAG_DB_HOST  ); 
				dbPort = getSingleElement( element, XML_TAG_DB_PORT  ); 
				dbUser = getSingleElement( element, XML_TAG_DB_USER  ); 
				dbPassword = getSingleElement( element, XML_TAG_DB_PASSWORD );
				// ignore if not passed in xml
				try {
					dbPool = Integer.parseInt( getSingleElement( element, XML_TAG_DB_POOL ) );
				} catch (NumberFormatException e) {}
				
			}
		}
		
	}
	
	public static DataBaseType getDbType() {
		return dbType;
	}
	
	public static String getDbName() {
		return dbName;
	}
	
	public static String getDbPath() {
		return dbPath;
	}
	
	public static String getDbHost() {
		return dbHost;
	}

	public static String getDbPort() {
		return dbPort;
	}

	public static String getDbUser() {
		return dbUser;
	}

	public static String getDbPassword() {
		return dbPassword;
	}
	
	public static int getDbPool() {
		return dbPool;
	}

}
