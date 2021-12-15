package services;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import mainClasses.CodeDocsClient;
import models.CodeDoc;
import requests.appRequests.CreateCodeDocRequest;
import requests.appRequests.DeleteCodeDocRequest;
import requests.appRequests.FetchCodeDocRequest;
import requests.appRequests.UpdateCodeDocRequest;
import response.appResponse.CreateCodeDocResponse;
import response.appResponse.DeleteCodeDocResponse;
import response.appResponse.FetchCodeDocResponse;
import response.appResponse.UpdateCodeDocResponse;
import utilities.CodeDocRequestType;
import utilities.LanguageType;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.function.Consumer;

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

    public static CreateCodeDocResponse createCodeDoc(String title, String description, LanguageType languageType) throws IOException, ClassNotFoundException {

        CodeDoc codeDoc = new CodeDoc();
        codeDoc.setTitle(title);
        codeDoc.setDescription(description);
        codeDoc.setLanguageType(languageType);
        codeDoc.setOwnerID(UserApi.getInstance().getId());
        codeDoc.setFileContent("");
        outputStream.writeObject(new CreateCodeDocRequest(codeDoc));
        outputStream.flush();
        return (CreateCodeDocResponse) inputStream.readObject();

    }

    public static UpdateCodeDocResponse updateCodeDoc(String codeDocId) throws IOException, ClassNotFoundException {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update CodeDoc");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();

        TextField titleTF = new TextField();
        titleTF.setPromptText("Enter new title");

        TextArea descTA = new TextArea();
        descTA.setPromptText("Enter new description");

        gridPane.add(new Label("Title"), 0, 0);
        gridPane.add(titleTF, 1, 0);

        gridPane.add(new Label("Description"), 0, 1);
        gridPane.add(descTA, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(titleTF.getText(), descTA.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        final String[] title = {""};
        final String[] desc = {""};
        final boolean[] isCancelled = {true};
        result.ifPresent(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> stringStringPair) {
                title[0] = titleTF.getText().trim();
                desc[0] = descTA.getText().trim();
                isCancelled[0] = false;
            }
        });

        if (isCancelled[0]) {
            return null;
        }

        if (title[0].isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Title cannot be empty!");
            alert.show();
            return null;
        }

        UpdateCodeDocRequest request = new UpdateCodeDocRequest();
        request.setCodeDocID(codeDocId);
        request.setTitle(title[0]);
        request.setDescription(desc[0]);
        request.setUserID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (UpdateCodeDocResponse) inputStream.readObject();
    }
}
