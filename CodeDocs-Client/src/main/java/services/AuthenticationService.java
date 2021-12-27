package services;

import mainClasses.CodeDocsClient;
import models.User;

import requests.appRequests.LoginRequest;
import requests.appRequests.SignupRequest;
import requests.appRequests.VerifyUserRequest;
import response.appResponse.LoginResponse;
import response.appResponse.SignupResponse;
import utilities.LoginStatus;
import utilities.SignupStatus;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class contains all the functions that can be utilized within the application
 * for performing authentication activities
 */
public class AuthenticationService {

    // IO streams for sending requests to server and reading responses from the server
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    /**
     * Function to log-in the application using specified credentials
     * @param email
     * @param password
     */
    public static LoginStatus loginUser(String email, String password) throws IOException, ClassNotFoundException {

        // Writing login request to the server
        outputStream.writeObject(new LoginRequest(email, password));
        outputStream.flush();

        // Receiving login response
        LoginResponse response = (LoginResponse) inputStream.readObject();
        if(response.getLoginStatus() == LoginStatus.SUCCESS) {

            // If login was successful, then store login token for auto login in future
            // and store the user details in UserApi

            User user = response.getUser();
            String token = response.getToken();

            StorageService.storeJWT(token);
            UserApi userApi = UserApi.getInstance();
            userApi.setUserData(user);
        }

        // Return login status for updating UI
        return response.getLoginStatus();
    }

    /**
     * Function to register a new user in application
     * @param user object generated in controller
     */
    public static SignupStatus signupUser(User user) throws IOException, ClassNotFoundException {

        // Writing the request to the server
        outputStream.writeObject(new SignupRequest(user));
        outputStream.flush();

        // Reading the response
        SignupResponse response = (SignupResponse) inputStream.readObject();

        // Returning the status to update UI
        return response.getSignupStatus();
    }

    /**
     * Function to verify our account
     * @param otp received on email
     * @param userEmail user's email address
     */
    public static Status verifyUser(String otp,String userEmail) throws IOException, ClassNotFoundException {

        // Writing the request to the server
        outputStream.writeObject(new VerifyUserRequest(userEmail,otp));
        outputStream.flush();

        // Returning the response from the server
        return (Status) inputStream.readObject();
    }

}