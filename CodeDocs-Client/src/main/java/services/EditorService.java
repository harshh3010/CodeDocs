package services;

import mainClasses.CodeDocsClient;
import models.CodeDoc;
import requests.editorRequests.*;
import response.editorResponse.*;
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
    public static void destroyConnection(String codeDocID,String userInControl) throws IOException {
        EditorCloseRequest request = new EditorCloseRequest();
        request.setUserInControl(userInControl);
        request.setCodeDocId(codeDocID);
        request.setUserId(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();
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
        //System.out.println(codeDoc.getFileContent());
        request.setCodeDoc(codeDoc);
        request.setUserId(UserApi.getInstance().getId());
        outputStream.writeObject(request);
        outputStream.flush();
        outputStream.reset();
        return (SaveCodeDocResponse) inputStream.readObject();
    }

    public static CompileCodeDocResponse compileCodeDoc(String codeDocID, LanguageType languageType) throws IOException, ClassNotFoundException {

        CompileCodeDocRequest request = new CompileCodeDocRequest();
        request.setLanguageType(languageType);
        request.setUserID(UserApi.getInstance().getId());
        request.setCodeDocID(codeDocID);

        outputStream.writeObject(request);
        outputStream.flush();

        return (CompileCodeDocResponse)inputStream.readObject();
    }

    public static RunCodeDocResponse runCodeDoc(String codeDocID,LanguageType languageType, String input) throws IOException, ClassNotFoundException {

        RunCodeDocRequest request = new RunCodeDocRequest();
        request.setInput(input);
        request.setLanguageType(languageType);
        request.setUserID(UserApi.getInstance().getId());
        request.setCodeDocID(codeDocID);

        outputStream.writeObject(request);
        outputStream.flush();

        return (RunCodeDocResponse) inputStream.readObject();
    }


}
