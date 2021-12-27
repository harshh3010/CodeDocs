package services;

import mainClasses.CodeDocsClient;
import models.User;
import requests.appRequests.GetMeRequest;
import response.appResponse.GetMeResponse;
import utilities.AppScreen;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class contains the functions for managing user of our application
 */
public class UserService {

    // IO streams for connecting with the server
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    /**
     * Function to load user's data from the server
     *
     * @param token the login token
     */
    public static GetMeResponse loadMyData(String token) throws IOException, ClassNotFoundException {

        outputStream.writeObject(new GetMeRequest(token));
        outputStream.flush();

        GetMeResponse response = (GetMeResponse) inputStream.readObject();

        if (response.getStatus() == Status.SUCCESS) {
            User user = response.getUser();
            UserApi userApi = UserApi.getInstance();
            userApi.setUserData(user);
        }

        return response;
    }

    /**
     * Function to log out the user from application
     */
    public static void logoutUser() throws IOException {

        // Clear the login token
        StorageService.storeJWT("");
        SceneService.setScene(AppScreen.loginScreen);
    }
}
