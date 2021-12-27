package services;

import java.util.prefs.Preferences;

/**
 * This class contains functions for storing login token in registry
 */
public class StorageService {

    private static final String keyJWT = "jwt_token";

    /**
     * Function to store json web token
     *
     * @param token
     */
    public static void storeJWT(String token) {
        Preferences preferences = Preferences.userNodeForPackage(StorageService.class);
        preferences.put(keyJWT, token);
    }

    /**
     * Function to fetch json web token
     */
    public static String fetchJWT() {
        Preferences preferences = Preferences.userNodeForPackage(StorageService.class);
        return preferences.get(keyJWT, "");
    }

}