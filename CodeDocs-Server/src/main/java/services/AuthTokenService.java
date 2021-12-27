package services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;

/**
 * This class defines functions for managing auth tokens
 */
public class AuthTokenService {

    /**
     * Function to get secret auth key from files
     */
    private static Key getSecretKey() throws IOException {

        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/auth.properties");
        properties.load(fileReader);

        final String secretString = properties.getProperty("JWT_SECRET");

        byte[] secret = Base64.getDecoder().decode(secretString);
        return Keys.hmacShaKeyFor(secret);
    }

    /**
     * Function to generate a new auth token for given user id
     */
    public static String generateAuthToken(String userId) throws IOException {
        return Jwts.builder()
                .claim("id", userId)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Function to decode the auth token and return the user id corresponding to it
     */
    public static String decodeAuthToken(String token) throws IOException {
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(token);
        return result.getBody().get("id").toString();
    }

}