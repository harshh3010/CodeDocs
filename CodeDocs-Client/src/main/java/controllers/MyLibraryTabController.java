package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import models.CodeDoc;
import response.appResponse.FetchCodeDocResponse;
import services.CodeDocsService;
import utilities.CodeDocRequestType;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyLibraryTabController implements Initializable {

    public JFXListView<CodeDoc> codeDocsListView;
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    public Button prevButton;
    public Button nextButton;

    private int offset;
    private int rowCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        errorAlert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                System.exit(1);
            }
        });

        prevButton.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: rgba(214, 6, 77,0.25);");
        nextButton.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: rgba(214, 6, 77,0.25);");

        offset = 0;
        rowCount = 10;

        fetchCodeDocs();
    }

    private void fetchCodeDocs() {

        try {
            FetchCodeDocResponse response = CodeDocsService.fetchCodeDocs(CodeDocRequestType.ACCESSIBLE_CODEDOCS, null, rowCount, offset);
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


    public void onNextClicked(ActionEvent actionEvent) {

        // Fetching the next batch only if current one is non-empty
        if(codeDocsListView.getItems().size() == 5){
            offset = offset + rowCount;
            fetchCodeDocs();
        }
    }

    public void onPreviousClicked(ActionEvent actionEvent) {

        // Fetching the previous batch only if offset is not 0 (Offset = 0 specifies first batch)
        if (offset > 0) {
            offset = offset - rowCount;
            fetchCodeDocs();
        }
    }
}
