package services;

/**
 * This class defines functions for generating verification tokens
 */
public class VerificationTokenGenerator {

    /**
     * Function to generate a random token of specified length
     */
    public static String getAlphaNumericString(int length) {

        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (str.length() * Math.random());
            stringBuilder.append(str.charAt(index));
        }

        return stringBuilder.toString();
    }

}