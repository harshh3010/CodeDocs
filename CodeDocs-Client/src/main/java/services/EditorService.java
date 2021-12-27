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

/**
 * This class contains functions to handle working of the editor
 */
public class EditorService {

    // IO streams to connect to server
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    /**
     * Function to establish a new editor connection
     *
     * @param codeDocId id of codedoc
     * @param port      port for starting editor server
     * @param audioPort for starting audio receiving server
     */
    public static EditorConnectionResponse establishConnection(String codeDocId, int port, int audioPort) throws IOException, ClassNotFoundException {
        EditorConnectionRequest request = new EditorConnectionRequest();
        request.setUserId(UserApi.getInstance().getId());
        request.setCodeDocId(codeDocId);
        request.setPort(port);
        request.setAudioPort(audioPort);

        outputStream.writeObject(request);
        outputStream.flush();

        return (EditorConnectionResponse) inputStream.readObject();
    }

    /**
     * Function to destroy editor connection
     *
     * @param codeDocID id of codedoc
     */
    public static void destroyConnection(String codeDocID) throws IOException {
        EditorCloseRequest request = new EditorCloseRequest();
        request.setCodeDocId(codeDocID);
        request.setUserId(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();
    }

    /**
     * Function to load editor content from the server
     *
     * @param codeDocID    id of codedoc
     * @param languageType programming language
     */
    public static LoadEditorResponse loadEditorContent(String codeDocID, LanguageType languageType) throws IOException, ClassNotFoundException {

        LoadEditorRequest loadEditorRequest = new LoadEditorRequest();
        loadEditorRequest.setCodeDocId(codeDocID);
        loadEditorRequest.setUserId(UserApi.getInstance().getId());
        loadEditorRequest.setLanguageType(languageType);

        outputStream.writeObject(loadEditorRequest);
        outputStream.flush();

        return (LoadEditorResponse) inputStream.readObject();
    }

    /**
     * Function to save the contents of the codedoc
     *
     * @param codeDoc instance of codedoc
     */
    public static SaveCodeDocResponse saveCodeDoc(CodeDoc codeDoc) throws IOException, ClassNotFoundException {

        SaveCodeDocRequest request = new SaveCodeDocRequest();
        request.setCodeDoc(codeDoc);
        request.setUserId(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();
        outputStream.reset();

        return (SaveCodeDocResponse) inputStream.readObject();
    }

    /**
     * Function to compile a codedoc
     *
     * @param codeDocID    id of codedoc
     * @param languageType programming language
     */
    public static CompileCodeDocResponse compileCodeDoc(String codeDocID, LanguageType languageType) throws IOException, ClassNotFoundException {

        CompileCodeDocRequest request = new CompileCodeDocRequest();
        request.setLanguageType(languageType);
        request.setUserID(UserApi.getInstance().getId());
        request.setCodeDocID(codeDocID);

        outputStream.writeObject(request);
        outputStream.flush();

        return (CompileCodeDocResponse) inputStream.readObject();
    }

    /**
     * Function to run code in the codedoc
     *
     * @param codeDocID    id of codedoc
     * @param languageType programming language
     * @param input        user input to the code
     */
    public static RunCodeDocResponse runCodeDoc(String codeDocID, LanguageType languageType, String input) throws IOException, ClassNotFoundException {

        RunCodeDocRequest request = new RunCodeDocRequest();
        request.setInput(input);
        request.setLanguageType(languageType);
        request.setUserID(UserApi.getInstance().getId());
        request.setCodeDocID(codeDocID);

        outputStream.writeObject(request);
        outputStream.flush();

        return (RunCodeDocResponse) inputStream.readObject();
    }

    /**
     * Function to transfer editor control
     *
     * @param codeDocId id of codedoc
     * @param userId    new user in control
     */
    public static void transferControl(String codeDocId, String userId) throws IOException {

        TransferControlRequest request = new TransferControlRequest();
        request.setCodeDocId(codeDocId);
        request.setUserId(userId);

        outputStream.writeObject(request);
        outputStream.flush();
    }
}
