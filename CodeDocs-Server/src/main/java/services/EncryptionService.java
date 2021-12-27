package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class defines the functions for encryption of data
 */
public class EncryptionService {


    /**
     * Function to generate an SHA-256 hash for specified string
     */
    public static String encrypt(String str) {

        String encryptedString = null;
        try {

            // Create MessageDigest instance for MD5
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // Add string bytes to digest
            messageDigest.update(str.getBytes());

            // Get the hash's bytes
            byte[] bytes = messageDigest.digest();

            // bytes[] has bytes in decimal format, convert it to hexadecimal format

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed string in hex format
            encryptedString = stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encryptedString;
    }
}

