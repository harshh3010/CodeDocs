package services.clientServices;


import mainClasses.ClientConnection;
import mainClasses.CodeDocsServer;
import models.User;
import requests.appRequests.LoginRequest;
import requests.appRequests.SignupRequest;
import requests.appRequests.VerifyUserRequest;
import response.appResponse.LoginResponse;
import response.appResponse.SignupResponse;
import services.AuthTokenService;
import services.EncryptionService;
import services.MailService;
import services.VerificationTokenGenerator;
import utilities.DatabaseConstants;
import utilities.LoginStatus;
import utilities.SignupStatus;
import utilities.Status;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthenticationService {

    /**
     * method to create account for client requesting it.
     * after ensuring any account with same eamil doesn't exist
     * @param signupRequest
     * @return
     */
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
                + "(" + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL
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
                verificationPreparedStatement.setString(1, signupRequest.getUser().getEmail());
                verificationPreparedStatement.setString(2, verificationToken);

                signupPreparedStatement.executeUpdate();
                verificationPreparedStatement.executeUpdate();

                MailService.sendEmail(signupRequest.getUser().getEmail(), " Verify your account ", verificationToken);

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

    /**
     * method to login client requesting it.
     * after password match, check for verified account is made and thereafter corresponding result is returned
     * @param loginRequest
     * @return
     */
    public static LoginResponse loginUser(LoginRequest loginRequest) {

        LoginResponse loginResponse = new LoginResponse();
        String loginQuery = "SELECT " + DatabaseConstants.USER_TABLE_COL_EMAIL +
                ", " + DatabaseConstants.USER_TABLE_COL_FIRSTNAME +
                ", " + DatabaseConstants.USER_TABLE_COL_LASTNAME +
                ", " + DatabaseConstants.USER_TABLE_COL_USERID +
                ", " + DatabaseConstants.USER_TABLE_COL_ISVERIFIED +
                " FROM " + DatabaseConstants.USER_TABLE_NAME +
                " WHERE " + DatabaseConstants.USER_TABLE_COL_EMAIL + "=?" +
                " AND " + DatabaseConstants.USER_TABLE_COL_PASSWORD + "=? ;";

        String verificationQuery = "INSERT INTO " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME
                + "(" + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT
                + ") values(?,?, NOW() + INTERVAL 1 HOUR );";

        try {

            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {

                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(loginQuery);
                preparedStatement.setString(1, loginRequest.getEmail());
                preparedStatement.setString(2, EncryptionService.encrypt(loginRequest.getPassword()));
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {

                    if (resultSet.getString(5).equals("0")) {
                        //user isn't verified
                        String verificationToken = VerificationTokenGenerator.getAlphaNumericString(6);

                        PreparedStatement verificationPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(verificationQuery);
                        verificationPreparedStatement.setString(1, resultSet.getString(1));
                        verificationPreparedStatement.setString(2, verificationToken);
                        verificationPreparedStatement.executeUpdate();

                        MailService.sendEmail(loginRequest.getEmail(), " Verify your account ", verificationToken);
                        CodeDocsServer.databaseConnection.commit();
                        loginResponse.setLoginStatus(LoginStatus.UNVERIFIED_USER);
                        return loginResponse;
                    }
                    //user is verified..send instance of user as response
                    User user = new User();
                    user.setUserID(resultSet.getString(4));
                    user.setEmail(resultSet.getString(1));
                    user.setFirstName(resultSet.getString(2));
                    user.setLastName(resultSet.getString(3));
                    loginResponse.setToken(AuthTokenService.generateAuthToken(user.getUserID()));

                    loginResponse.setUser(user);
                    loginResponse.setLoginStatus(LoginStatus.SUCCESS);

                    return loginResponse;
                }
                loginResponse.setLoginStatus(LoginStatus.WRONG_CREDENTIALS);
                return loginResponse;
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        loginResponse.setLoginStatus(LoginStatus.SERVER_SIDE_ERROR);
        return loginResponse;
    }

    /** method to verify account of user if verification token is valid
     * and the same as sent on his email
     * @param verifyUserRequest
     */
    public static Status verifyUser(VerifyUserRequest verifyUserRequest) {

        String selectQuery = "Select * from " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME +
                " where " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL +
                " = ?  AND " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN +
                " = ? AND " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT + " > NOW()" +
                " ORDER BY "+ DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT +" DESC LIMIT 1;";

        String updateQuery = "UPDATE " + DatabaseConstants.USER_TABLE_NAME + " "
                + " SET " + DatabaseConstants.USER_TABLE_COL_ISVERIFIED + " =  1 "
                + " WHERE " + DatabaseConstants.USER_TABLE_COL_EMAIL
                + " = ?";
        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {

                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(selectQuery);
                preparedStatement.setString(1, verifyUserRequest.getUserEmail());
                preparedStatement.setString(2, verifyUserRequest.getVerificationToken());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, verifyUserRequest.getUserEmail());
                    preparedStatement.executeUpdate();
                    CodeDocsServer.databaseConnection.commit();
                    return Status.SUCCESS;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Status.FAILED;
    }


}
