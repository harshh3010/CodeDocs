package services;

import mainClasses.CodeDocsClient;
import models.User;

import requests.appRequests.LoginRequest;
import requests.appRequests.SignupRequest;
import response.appResponse.LoginResponse;
import response.appResponse.SignupResponse;
import utilities.LoginStatus;
import utilities.SignupStatus;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthenticationService {

    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static LoginStatus loginUser(String email, String password) throws IOException, ClassNotFoundException {

        outputStream.writeObject(new LoginRequest(email, password));
        outputStream.flush();

        LoginResponse response = (LoginResponse) inputStream.readObject();
        if(response.getLoginStatus() == LoginStatus.SUCCESS) {

            User user = response.getUser();
            String token = response.getToken();

            StorageService.storeJWT(token);
            UserApi userApi = UserApi.getInstance();
            userApi.setUserData(user);
        }

        return response.getLoginStatus();
    }

    public static SignupStatus signupUser(User user) throws IOException, ClassNotFoundException {

        outputStream.writeObject(new SignupRequest(user));
        outputStream.flush();

        SignupResponse response = (SignupResponse) inputStream.readObject();

        return response.getSignupStatus();
    }

}