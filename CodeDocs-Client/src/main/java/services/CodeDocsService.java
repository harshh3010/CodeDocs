package services;

import mainClasses.CodeDocsClient;
import models.User;
import requests.appRequests.FetchCodeDocRequest;
import requests.appRequests.LoginRequest;
import response.appResponse.FetchCodeDocResponse;
import response.appResponse.LoginResponse;
import utilities.CodeDocRequestType;
import utilities.LoginStatus;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CodeDocsService {

    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static FetchCodeDocResponse fetchCodeDocs(CodeDocRequestType requestType, String codeDocId, int rowCount, int offset) throws IOException, ClassNotFoundException {

        FetchCodeDocRequest fetchCodeDocRequest = new FetchCodeDocRequest();
        fetchCodeDocRequest.setCodeDocRequestType(requestType);
        fetchCodeDocRequest.setUserID(UserApi.getInstance().getId());
        fetchCodeDocRequest.setRowcount(rowCount);
        fetchCodeDocRequest.setOffset(offset);
        fetchCodeDocRequest.setCodeDocID(codeDocId);

        outputStream.writeObject(fetchCodeDocRequest);
        outputStream.flush();

        return (FetchCodeDocResponse) inputStream.readObject();
    }

}
