package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import models.CodeDoc;
import response.appResponse.FetchCodeDocResponse;
import services.CodeDocsService;
import utilities.CodeDocRequestType;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PersonalCodeDocsTabController implements Initializable {
    public JFXListView<CodeDoc> codeDocsListView;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorAlert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                System.exit(1);
            }
        });
        fetchCodeDocs();
    }

    private void fetchCodeDocs() {

        try {
            FetchCodeDocResponse response = CodeDocsService.fetchCodeDocs(CodeDocRequestType.PERSONAL_CODEDOCS, null, 10, 0);
            if (response.getStatus() == Status.SUCCESS) {
                codeDocsListView.setItems(FXCollections.observableArrayList(response.getCodeDocs()));
                codeDocsListView.setCellFactory(new CodeDocCardFactory());
            } else {
                errorAlert.setContentText("An error occurred! Please try again later.");
                errorAlert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }

    }
}
