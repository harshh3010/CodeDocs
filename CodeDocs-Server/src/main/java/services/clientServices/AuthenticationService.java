package services.clientServices;


import mainClasses.CodeDocsServer;
import requests.appRequests.SignupRequest;
import response.appResponse.SignupResponse;
import services.EncryptionService;
import services.MailService;
import services.VerificationTokenGenerator;
import utilities.DatabaseConstants;
import utilities.SignupStatus;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AuthenticationService {

    public static SignupResponse registerUser(SignupRequest signupRequest) {

        SignupResponse signupResponse = new SignupResponse();

        String signupQuery = "INSERT INTO " + DatabaseConstants.USER_TABLE_NAME
                + "(" + DatabaseConstants.USER_TABLE_COL_EMAIL
                + "," + DatabaseConstants.USER_TABLE_COL_PASSWORD
                + "," + DatabaseConstants.USER_TABLE_COL_FIRSTNAME
                + "," + DatabaseConstants.USER_TABLE_COL_LASTNAME
                + "," + DatabaseConstants.USER_TABLE_COL_USERID
                + ") values(?,?,?,?,?);";

        String verificationQuery = "INSERT INTO " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME
                + "(" + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_ID
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT
                + ") values(?,?, NOW() + INTERVAL 1 HOUR );";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);

            try {
                String userID = UUID.randomUUID().toString();
                String verificationToken = VerificationTokenGenerator.getAlphaNumericString(6);

                PreparedStatement signupPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(signupQuery);
                signupPreparedStatement.setString(1, signupRequest.getUser().getEmail());
                signupPreparedStatement.setString(2, EncryptionService.encrypt(signupRequest.getUser().getPassword()));
                signupPreparedStatement.setString(3, signupRequest.getUser().getFirstName());
                signupPreparedStatement.setString(4, signupRequest.getUser().getLastName());
                signupPreparedStatement.setString(5, userID);

                PreparedStatement verificationPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(verificationQuery);
                verificationPreparedStatement.setString(1, userID);
                verificationPreparedStatement.setString(2, verificationToken);

                signupPreparedStatement.executeUpdate();
                verificationPreparedStatement.executeUpdate();

                MailService.sendEmail(signupRequest.getUser().getEmail()," Verify your account ",verificationToken);

                CodeDocsServer.databaseConnection.commit();
                signupResponse.setSignupStatus(SignupStatus.SUCCESS);
                return signupResponse;
            } catch (SQLException | IOException e) {
                CodeDocsServer.databaseConnection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        signupResponse.setSignupStatus(SignupStatus.FAILED);
        return signupResponse;
    }
}
