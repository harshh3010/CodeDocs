package services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

/**
 * This class contains all the functions that will be used for managing codedocs
 * in the application
 */
public class CodeDocsService {

    // IO streams for sending requests to server and reading responses from the server
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    /**
     * Function to fetch codedocs from the server
     *
     * @param requestType the type of codedoc list to be fetched
     * @param codeDocId   id in case a specific codedoc is to be fetched
     * @param rowCount    for pagination
     * @param offset      for pagination
     */
    public static FetchCodeDocResponse fetchCodeDocs(CodeDocRequestType requestType, String codeDocId, int rowCount, int offset) throws IOException, ClassNotFoundException {

        // Generating a request to send to server
        FetchCodeDocRequest fetchCodeDocRequest = new FetchCodeDocRequest();
        fetchCodeDocRequest.setCodeDocRequestType(requestType);
        fetchCodeDocRequest.setUserID(UserApi.getInstance().getId());
        fetchCodeDocRequest.setRowcount(rowCount);
        fetchCodeDocRequest.setOffset(offset);
        fetchCodeDocRequest.setCodeDocID(codeDocId);

        // Sending a request to the server
        outputStream.writeObject(fetchCodeDocRequest);
        outputStream.flush();

        // Returning the response from the server
        return (FetchCodeDocResponse) inputStream.readObject();
    }

    /**
     * Function to delete a specified codedoc
     *
     * @param codeDocId id of the codedoc
     */
    public static DeleteCodeDocResponse deleteCodeDoc(String codeDocId) throws IOException, ClassNotFoundException {

        // Showing confirmation dialog to the user
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setContentText("Are you sure?");
        Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

        if (pressedButton.get() == ButtonType.OK) {

            // If user is sure to delete a codedoc, then send a request to the server

            // Generating the request
            DeleteCodeDocRequest request = new DeleteCodeDocRequest();
            request.setCodeDocID(codeDocId);
            request.setUserID(UserApi.getInstance().getId());

            // Writing the request
            outputStream.writeObject(request);
            outputStream.flush();

            // Returning the response
            return (DeleteCodeDocResponse) inputStream.readObject();
        }

        // Return null on delete cancelled
        return null;
    }

    /**
     * Function to create a codedoc
     */
    public static CreateCodeDocResponse createCodeDoc() throws IOException, ClassNotFoundException {

        // Displaying creation dialog to the user
        Dialog<CreateCodeDocResult> dialog = new Dialog<>();

        dialog.setTitle("Create CodeDoc Dialog");
        dialog.setHeaderText("Specify details for your codeDoc");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleTF = new TextField();
        titleTF.setPromptText("Enter title of CodeDoc");

        TextArea descTA = new TextArea("Description");
        descTA.setPromptText("Enter description of CodeDoc");

        ObservableList<LanguageType> options = FXCollections.observableArrayList(LanguageType.values());
        ComboBox<LanguageType> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();

        GridPane gridPane = new GridPane();

        gridPane.add(new Label("Title"), 0, 0);
        gridPane.add(titleTF, 1, 0);

        gridPane.add(new Label("Description"), 0, 1);
        gridPane.add(descTA, 1, 1);

        gridPane.add(new Label("Language"), 0, 2);
        gridPane.add(comboBox, 1, 2);

        gridPane.setVgap(10);

        dialogPane.setContent(gridPane);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new CreateCodeDocResult(titleTF.getText(), descTA.getText(), comboBox.getValue());
            }
            return null;
        });

        final String[] title = {""};
        final String[] description = {""};
        final LanguageType[] languageType = new LanguageType[1];
        final boolean[] isCancelled = {true};

        Optional<CreateCodeDocResult> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((CreateCodeDocResult results) -> {
            title[0] = results.title;
            description[0] = results.description;
            languageType[0] = results.languageType;
            isCancelled[0] = false;
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
        if (description[0].isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Description cannot be empty!");
            alert.show();
            return null;
        }

        CodeDoc codeDoc = new CodeDoc();
        codeDoc.setTitle(title[0]);
        codeDoc.setLanguageType(languageType[0]);
        codeDoc.setDescription(description[0]);
        codeDoc.setOwnerID(UserApi.getInstance().getId());
        codeDoc.setFileContent("");
        CreateCodeDocRequest request = new CreateCodeDocRequest(codeDoc);

        outputStream.writeObject(request);
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
        result.ifPresent(stringStringPair -> {
            title[0] = titleTF.getText().trim();
            desc[0] = descTA.getText().trim();
            isCancelled[0] = false;
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

    /**
     * Utility for create codedoc function
     */
    private static class CreateCodeDocResult {

        String title;
        String description;
        LanguageType languageType;

        public CreateCodeDocResult(String title, String description, LanguageType languageType) {
            this.title = title;
            this.description = description;
            this.languageType = languageType;
        }
    }

}
