package services.clientServices;

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

/**
 * This class defines all the functions for handling authentication requests from
 * clients
 */
public class AuthenticationService {

    /**
     * Function to register a new user
     */
    public static SignupResponse registerUser(SignupRequest signupRequest) {

        SignupResponse signupResponse = new SignupResponse();

        // Signup query to store user details in database
        String signupQuery = "INSERT INTO " + DatabaseConstants.USER_TABLE_NAME
                + "(" + DatabaseConstants.USER_TABLE_COL_EMAIL
                + "," + DatabaseConstants.USER_TABLE_COL_PASSWORD
                + "," + DatabaseConstants.USER_TABLE_COL_FIRSTNAME
                + "," + DatabaseConstants.USER_TABLE_COL_LASTNAME
                + "," + DatabaseConstants.USER_TABLE_COL_USERID
                + ") values(?,?,?,?,?);";

        // Verification query to store verification token in database
        String verificationQuery = "INSERT INTO " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME
                + "(" + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT
                + ") values(?,?, NOW() + INTERVAL 1 HOUR );";

        try {
            // Disable autocommit to rollback in case of failure
            CodeDocsServer.databaseConnection.setAutoCommit(false);

            try {

                // Generating a unique user id for client
                String userID = UUID.randomUUID().toString();

                // Generating a verification token
                String verificationToken = VerificationTokenGenerator.getAlphaNumericString(6);

                // Preparing the signup query
                PreparedStatement signupPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(signupQuery);
                signupPreparedStatement.setString(1, signupRequest.getUser().getEmail());
                signupPreparedStatement.setString(2, EncryptionService.encrypt(signupRequest.getUser().getPassword()));
                signupPreparedStatement.setString(3, signupRequest.getUser().getFirstName());
                signupPreparedStatement.setString(4, signupRequest.getUser().getLastName());
                signupPreparedStatement.setString(5, userID);

                // Preparing the verification query
                PreparedStatement verificationPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(verificationQuery);
                verificationPreparedStatement.setString(1, signupRequest.getUser().getEmail());
                verificationPreparedStatement.setString(2, verificationToken);

                // Executing both the queries
                signupPreparedStatement.executeUpdate();
                verificationPreparedStatement.executeUpdate();

                // Sending the verification token on client's email address
                MailService.sendEmail(signupRequest.getUser().getEmail(), " Verify your account ", verificationToken);

                // Committing the changes to database
                CodeDocsServer.databaseConnection.commit();

                // Returning the response to the client
                signupResponse.setSignupStatus(SignupStatus.SUCCESS);
                return signupResponse;

            } catch (SQLException | IOException e) {

                // Rollback in case of any failure
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
     * Function to login a user with specified credentials
     */
    public static LoginResponse loginUser(LoginRequest loginRequest) {

        LoginResponse loginResponse = new LoginResponse();

        // To select user corresponding to credentials
        String loginQuery = "SELECT " + DatabaseConstants.USER_TABLE_COL_EMAIL +
                ", " + DatabaseConstants.USER_TABLE_COL_FIRSTNAME +
                ", " + DatabaseConstants.USER_TABLE_COL_LASTNAME +
                ", " + DatabaseConstants.USER_TABLE_COL_USERID +
                ", " + DatabaseConstants.USER_TABLE_COL_ISVERIFIED +
                " FROM " + DatabaseConstants.USER_TABLE_NAME +
                " WHERE " + DatabaseConstants.USER_TABLE_COL_EMAIL + "=?" +
                " AND " + DatabaseConstants.USER_TABLE_COL_PASSWORD + "=? ;";

        // To check verification status of the user
        String verificationQuery = "INSERT INTO " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME
                + "(" + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN
                + "," + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT
                + ") values(?,?, NOW() + INTERVAL 1 HOUR );";

        try {

            // Disable autocommit to rollback in case of failure
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {

                // Preparing and executing the first query
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(loginQuery);
                preparedStatement.setString(1, loginRequest.getEmail());
                preparedStatement.setString(2, EncryptionService.encrypt(loginRequest.getPassword()));
                ResultSet resultSet = preparedStatement.executeQuery();


                if (resultSet.next()) {

                    // If user corresponding to credentials exists

                    if (resultSet.getString(5).equals("0")) {

                        // If the user is not verified, generate a new verification token
                        String verificationToken = VerificationTokenGenerator.getAlphaNumericString(6);

                        // Insert token in database
                        PreparedStatement verificationPreparedStatement = CodeDocsServer.databaseConnection.prepareStatement(verificationQuery);
                        verificationPreparedStatement.setString(1, resultSet.getString(1));
                        verificationPreparedStatement.setString(2, verificationToken);
                        verificationPreparedStatement.executeUpdate();

                        // Mail the token to user
                        MailService.sendEmail(loginRequest.getEmail(), " Verify your account ", verificationToken);

                        // Commit changes
                        CodeDocsServer.databaseConnection.commit();

                        // Send appropriate response to the user
                        loginResponse.setLoginStatus(LoginStatus.UNVERIFIED_USER);
                        return loginResponse;
                    }

                    // Fetch user's details from the database
                    User user = new User();
                    user.setUserID(resultSet.getString(4));
                    user.setEmail(resultSet.getString(1));
                    user.setFirstName(resultSet.getString(2));
                    user.setLastName(resultSet.getString(3));
                    loginResponse.setUser(user);

                    // Generate a new login token for the user
                    loginResponse.setToken(AuthTokenService.generateAuthToken(user.getUserID()));

                    // Send response to the user
                    loginResponse.setLoginStatus(LoginStatus.SUCCESS);

                    return loginResponse;
                }

                // Wrong credentials response if user was not found
                loginResponse.setLoginStatus(LoginStatus.WRONG_CREDENTIALS);
                return loginResponse;

            } catch (SQLException | IOException e) {
                e.printStackTrace();

                // Rollback in case of failure
                CodeDocsServer.databaseConnection.rollback();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        loginResponse.setLoginStatus(LoginStatus.SERVER_SIDE_ERROR);
        return loginResponse;
    }


    /**
     * Function to verify a user's account
     */
    public static Status verifyUser(VerifyUserRequest verifyUserRequest) {

        // Query to fetch verification token
        String selectQuery = "Select * from " + DatabaseConstants.USER_VERIFICATION_TABLE_NAME +
                " where " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_USER_EMAIL +
                " = ?  AND " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN +
                " = ? AND " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT + " > NOW()" +
                " ORDER BY " + DatabaseConstants.USER_VERIFICATION_TABLE_COL_EXPIRES_AT + " DESC LIMIT 1;";

        // Query to update verification status
        String updateQuery = "UPDATE " + DatabaseConstants.USER_TABLE_NAME + " "
                + " SET " + DatabaseConstants.USER_TABLE_COL_ISVERIFIED + " =  1 "
                + " WHERE " + DatabaseConstants.USER_TABLE_COL_EMAIL
                + " = ?";
        try {

            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {

                // Executing first query
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(selectQuery);
                preparedStatement.setString(1, verifyUserRequest.getUserEmail());
                preparedStatement.setString(2, verifyUserRequest.getVerificationToken());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    // Executing second query if first query gave some result
                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, verifyUserRequest.getUserEmail());
                    preparedStatement.executeUpdate();

                    // Committing the changes
                    CodeDocsServer.databaseConnection.commit();

                    // Returning response
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
