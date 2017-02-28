package com.tutorialacademy.rest.security;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class TokenSecurity {
	
	private static RsaJsonWebKey rsaJsonWebKey = null;
	private static String issuer = "tutorial-academy.com";
	private static int timeToExpire = 30;
	
	// 	Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
	static {
		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		} catch (JoseException e) {
			e.printStackTrace();
		}
	}
	
	public static String generateJwtToken( String id ) throws JoseException {
	    // Give the JWK a Key ID (kid), which is just the polite thing to do
	    rsaJsonWebKey.setKeyId("k1");

	    // Create the Claims, which will be the content of the JWT
	    JwtClaims claims = new JwtClaims();
	    // who creates the token and signs it
	    claims.setIssuer( issuer );  
	    // time when the token will expire (timeToExpire minutes from now)
	    claims.setExpirationTimeMinutesInTheFuture( timeToExpire ); 
	    // a unique identifier for the token
	    claims.setGeneratedJwtId(); 
	    // when the token was issued/created (now)
	    claims.setIssuedAtToNow();  
	    // time before which the token is not yet valid (2 minutes ago)
	    claims.setNotBeforeMinutesInThePast(2); 
	    // transmit the user id for later authentication
	    claims.setClaim( "id", id ); 

	    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
	    // In this example it is a JWS so we create a JsonWebSignature object.
	    JsonWebSignature jws = new JsonWebSignature();
	    // The payload of the JWS is JSON content of the JWT Claims
	    jws.setPayload( claims.toJson() );
	    // The JWT is signed using the private key
	    jws.setKey( rsaJsonWebKey.getPrivateKey() );

	    // Set the Key ID (kid) header because it's just the polite thing to do.
	    // We only have one key in this example but a using a Key ID helps
	    // facilitate a smooth key rollover process
	    jws.setKeyIdHeaderValue( rsaJsonWebKey.getKeyId() );

	    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
	    jws.setAlgorithmHeaderValue( AlgorithmIdentifiers.RSA_USING_SHA256 );

	    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
	    // representation, which is a string consisting of three dot ('.') separated
	    // base64url-encoded parts in the form Header.Payload.Signature
	    // If you wanted to encrypt it, you can simply set this jwt as the payload
	    // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
	    String jwt = jws.getCompactSerialization();

	    // Now you can do something with the JWT. Like send it to some other party
	    return jwt;
	}
	
	public static String validateJwtToken( String jwt ) throws InvalidJwtException {
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	    		// the JWT must have an expiration time
	            .setRequireExpirationTime() 
	            // but the  expiration time can't be too crazy
	            .setMaxFutureValidityInMinutes( 300 ) 
	            // allow some leeway in validating time based claims to account for clock skew
	            .setAllowedClockSkewInSeconds( 30 ) 
	            // whom the JWT needs to have been issued by
	            .setExpectedIssuer( issuer ) 
	            // verify the signature with the public key
	            .setVerificationKey( rsaJsonWebKey.getKey() )
	            .build(); 

        //  Validate the JWT and process it to the Claims
        JwtClaims jwtClaims = jwtConsumer.processToClaims( jwt );
        System.out.println( "JWT validation succeeded! " + jwtClaims ); 
        
        // validate and return the encoded user id
        return jwtClaims.getClaimsMap().get("id").toString();
	}
}
