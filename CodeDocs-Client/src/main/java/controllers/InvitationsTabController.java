package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import models.CodeDoc;
import response.appResponse.FetchCodeDocResponse;
import response.appResponse.FetchInviteResponse;
import services.CodeDocsService;
import services.CollaborationService;
import utilities.CodeDocRequestType;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class InvitationsTabController implements Initializable {
    public JFXListView<CodeDoc> invitesListView;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorAlert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                System.exit(1);
            }
        });
        fetchInvites();
    }


    private void fetchInvites() {

        try {
            FetchInviteResponse response = CollaborationService.fetchInvites( 10, 0);
            if (response.getStatus() == Status.SUCCESS) {
                invitesListView.setItems(FXCollections.observableArrayList(response.getInvites()));
                invitesListView.setCellFactory(new InvitationCardFactory());
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
