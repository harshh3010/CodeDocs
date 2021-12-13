package services;

import mainClasses.CodeDocsClient;
import models.User;
import requests.appRequests.GetMeRequest;
import response.appResponse.GetMeResponse;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserService {

    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static GetMeResponse loadMyData(String token) throws IOException, ClassNotFoundException {

        outputStream.writeObject(new GetMeRequest(token));
        outputStream.flush();

        GetMeResponse response = (GetMeResponse) inputStream.readObject();

        if(response.getStatus() == Status.SUCCESS) {
            User user = response.getUser();
            UserApi userApi = UserApi.getInstance();
            userApi.setUserData(user);
        }

        return response;
    }
}
