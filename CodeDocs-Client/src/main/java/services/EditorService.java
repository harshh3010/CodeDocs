package services;

import mainClasses.CodeDocsClient;
import models.CodeDoc;
import requests.editorRequests.EditorConnectionRequest;
import requests.editorRequests.LoadEditorRequest;
import requests.editorRequests.SaveCodeDocRequest;
import response.editorResponse.EditorConnectionResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.SaveCodeDocResponse;
import utilities.LanguageType;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EditorService {
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static EditorConnectionResponse establishConnection(String codeDocId, int port) throws IOException, ClassNotFoundException {
        EditorConnectionRequest request = new EditorConnectionRequest();
        request.setUserId(UserApi.getInstance().getId());
        request.setCodeDocId(codeDocId);
        request.setPort(port);

        outputStream.writeObject(request);
        outputStream.flush();

        return (EditorConnectionResponse) inputStream.readObject();
    }

    public static LoadEditorResponse loadEditorContent(String codeDocID, LanguageType languageType) throws IOException, ClassNotFoundException {
        LoadEditorRequest loadEditorRequest = new LoadEditorRequest();
        loadEditorRequest.setCodeDocId(codeDocID);
        loadEditorRequest.setUserId(UserApi.getInstance().getId());
        loadEditorRequest.setLanguageType(languageType);
        outputStream.writeObject(loadEditorRequest);
        outputStream.flush();
        return (LoadEditorResponse) inputStream.readObject();
    }

    public static SaveCodeDocResponse saveCodeDoc(CodeDoc codeDoc) throws IOException, ClassNotFoundException {
        SaveCodeDocRequest request = new SaveCodeDocRequest();
        request.setCodeDoc(codeDoc);
        request.setUserId(UserApi.getInstance().getId());
        outputStream.writeObject(request);
        outputStream.flush();
        return (SaveCodeDocResponse) inputStream.readObject();
    }
}
