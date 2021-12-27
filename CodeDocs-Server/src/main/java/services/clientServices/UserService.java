package services.clientServices;

import mainClasses.CodeDocsServer;
import models.User;
import requests.appRequests.GetMeRequest;
import response.appResponse.GetMeResponse;
import services.AuthTokenService;
import utilities.DatabaseConstants;
import utilities.Status;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class defines all functions for handling client requests for managing users
 */
public class UserService {

    /**
     * Function to fetch a user's data
     */
    public static GetMeResponse getUserData(GetMeRequest request) {

        GetMeResponse response = new GetMeResponse();

        String userId;

        try {
            userId = AuthTokenService.decodeAuthToken(request.getToken());
        } catch (IOException e) {
            System.out.println("Unable to decode user auth token!");
            System.out.println("Error: " + e.getMessage());
            response.setStatus(Status.FAILED);
            return response;
        }

        String query = "SELECT " + DatabaseConstants.USER_TABLE_COL_USERID
                + ", " + DatabaseConstants.USER_TABLE_COL_EMAIL
                + ", " + DatabaseConstants.USER_TABLE_COL_FIRSTNAME
                + ", " + DatabaseConstants.USER_TABLE_COL_LASTNAME
                + " FROM " + DatabaseConstants.USER_TABLE_NAME
                + " WHERE " + DatabaseConstants.USER_TABLE_COL_USERID + " = ?";

        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, userId);

            User user = null;
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String userID = resultSet.getString(1);
                String email = resultSet.getString(2);
                String firstName = resultSet.getString(3);
                String lastName = resultSet.getString(4);

                user = new User();
                user.setUserID(userID);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
            }

            if (user != null) {
                response.setUser(user);
                response.setStatus(Status.SUCCESS);
            } else {
                response.setStatus(Status.FAILED);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response;
    }

}
