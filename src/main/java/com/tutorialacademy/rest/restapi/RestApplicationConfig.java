package com.tutorialacademy.rest.restapi;

import org.glassfish.jersey.server.ResourceConfig;

import com.tutorialacademy.rest.filter.AuthenticationFilter;

/**
 *  set the filter applications manually and not via web.xml
 */
public class RestApplicationConfig extends ResourceConfig {
	
	public RestApplicationConfig() {
        packages( "com.tutorialacademy.rest.filter" );
		register( AuthenticationFilter.class );
	}
}
