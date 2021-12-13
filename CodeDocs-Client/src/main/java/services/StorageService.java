package services;

import java.util.prefs.Preferences;

public class StorageService {

    private static String keyJWT = "jwt_token";

    public static void storeJWT(String token) {
        Preferences preferences = Preferences.userNodeForPackage(StorageService.class);
        preferences.put(keyJWT, token);
    }

    public static String fetchJWT() {
        Preferences preferences = Preferences.userNodeForPackage(StorageService.class);
        return preferences.get(keyJWT, "");
    }

}