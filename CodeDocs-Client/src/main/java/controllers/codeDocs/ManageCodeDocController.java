package controllers.codeDocs;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import models.CodeDoc;
import response.appResponse.FetchCollaboratorResponse;
import response.appResponse.InviteCollaboratorResponse;
import response.appResponse.UpdateCodeDocResponse;
import services.CodeDocsService;
import services.CollaborationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageCodeDocController implements Initializable {
    public Label titleLabel;
    public JFXListView collaboratorsListView;
    private static CodeDoc codeDoc;
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    public Button updateButton;
    public Button inviteButton;
    public Button prevButton;
    public Button nextButton;

    private int offset;
    private int rowCount;

    public static CodeDoc getCodeDoc() {
        return codeDoc;
    }

    public static void setCodeDoc(CodeDoc c) {
        codeDoc = c;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorAlert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                System.exit(1);
            }
        });

        titleLabel.setText(codeDoc.getTitle());
        if(!codeDoc.getOwnerID().equals(UserApi.getInstance().getId())){
            updateButton.setVisible(false);
            inviteButton.setVisible(false);
        }

        prevButton.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: rgba(214, 6, 77,0.25);");
        nextButton.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: rgba(214, 6, 77,0.25);");

        offset = 0;
        rowCount = 10;

        fetchCollaborators();
    }

    private void fetchCollaborators() {
        try {
            FetchCollaboratorResponse response = CollaborationService.fetchCollaborator( codeDoc.getCodeDocId(), 10,0);
            if (response.getStatus() == Status.SUCCESS) {
                collaboratorsListView.setItems(FXCollections.observableArrayList(response.getCollaborators()));
                collaboratorsListView.setCellFactory(new CollaboratorCardFactory());
            } else {
                errorAlert.setContentText("An error occurred! Please try again later.");
                errorAlert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }


    public void onBackClicked(ActionEvent actionEvent) {
        try {
            SceneService.setScene(AppScreen.mainScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateClicked(ActionEvent actionEvent) {

        try {
            UpdateCodeDocResponse response = CodeDocsService.updateCodeDoc(codeDoc.getCodeDocId());
            if (response == null) {
                return;
            }
            Alert alert;
            if (response.getStatus() == Status.SUCCESS) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("CodeDoc updated successfully!");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to update CodeDoc!");
            }
            alert.show();
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to update CodeDoc!");
            alert.show();
        }

    }

    public void onInviteClicked(ActionEvent actionEvent) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Invite Collaborator");
        ButtonType inviteButtonType = new ButtonType("Invite", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(inviteButtonType, ButtonType.CANCEL);

        // Create the userID and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userID = new TextField();
        userID.setPromptText("user id");

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().add("Give write permissions");
        choiceBox.getItems().add("Do not give write permissions");

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(userID, 1, 0);
        grid.add(new Label("write access:"), 0, 1);
        grid.add(choiceBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> userID.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == inviteButtonType) {
                return new Pair<>(userID.getText(), (String) choiceBox.getValue());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        Alert alert = new Alert(Alert.AlertType.NONE);;
        result.ifPresent(idChoice -> {

            int permissions = 0;
            if(idChoice.getValue() == "Give write permissions")
                permissions = 1;

            if(idChoice.getKey() == ""){
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Please provide a user id");
                alert.show();
            }
            else{
                try {
                    InviteCollaboratorResponse status = CollaborationService.inviteCollaborator(codeDoc.getCodeDocId(),idChoice.getKey(),permissions);
                    if(status.getStatus() == Status.SUCCESS){

                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setContentText("Invitation sent successfully.");
                    }else{
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setContentText("Cannot invite user at the moment. Please try again later");
                    }
                    alert.show();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("Cannot invite user at the moment. Please try again later");
                    alert.show();
                }
            }
        });


    }

    public void onNextClicked(ActionEvent actionEvent) {

        // Fetching the next batch only if current one is non-empty
        if(collaboratorsListView.getItems().size() == 5){
            offset = offset + rowCount;
            fetchCollaborators();
        }
    }

    public void onPreviousClicked(ActionEvent actionEvent) {

        // Fetching the previous batch only if offset is not 0 (Offset = 0 specifies first batch)
        if (offset > 0) {
            offset = offset - rowCount;
            fetchCollaborators();
        }
    }
}
