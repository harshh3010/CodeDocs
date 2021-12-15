package services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mainClasses.CodeDocsClient;
import requests.appRequests.DeleteCodeDocRequest;
import requests.appRequests.FetchCodeDocRequest;
import response.appResponse.DeleteCodeDocResponse;
import response.appResponse.FetchCodeDocResponse;
import utilities.CodeDocRequestType;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

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

    public static DeleteCodeDocResponse deleteCodeDoc(String codeDocId) throws IOException, ClassNotFoundException {

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setContentText("Are you sure?");
        Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

        if (pressedButton.get() == ButtonType.OK) {
            DeleteCodeDocRequest request = new DeleteCodeDocRequest();
            request.setCodeDocID(codeDocId);
            request.setUserID(UserApi.getInstance().getId());
            outputStream.writeObject(request);
            outputStream.flush();

            return (DeleteCodeDocResponse) inputStream.readObject();
        }

        return null;
    }

}
