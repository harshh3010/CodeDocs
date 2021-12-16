package services;

import mainClasses.CodeDocsClient;
import models.CodeDoc;
import models.CodeEditor;
import requests.editorRequests.LoadEditorRequest;
import requests.editorRequests.SaveCodeDocRequest;
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
