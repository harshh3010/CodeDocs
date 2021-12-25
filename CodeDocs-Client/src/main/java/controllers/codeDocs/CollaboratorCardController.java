package controllers.codeDocs;

import controllers.codeDocs.ManageCodeDocController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import models.Collaborator;
import response.appResponse.ChangeCollaboratorRightsResponse;
import response.appResponse.RemoveCollaboratorResponse;
import services.CollaborationService;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.util.Optional;

public class CollaboratorCardController extends ListCell<Collaborator> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button removeButton;
    @FXML
    private Button changeRightButton;

    public CollaboratorCardController() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/collaborator_card.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void updateItem(Collaborator collaborator, boolean b) {

        super.updateItem(collaborator, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            if(collaborator.getWritePermissions() == 1){
                usernameLabel.setTextFill(Color.rgb(214, 6, 77));
            }

            usernameLabel.setText(collaborator.getUser().getFirstName() + " " + collaborator.getUser().getLastName());
            emailLabel.setText(collaborator.getUser().getEmail());

            if (ManageCodeDocController.getCodeDoc().getOwnerID().equals(UserApi.getInstance().getId())) {
                removeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {

                            RemoveCollaboratorResponse response = CollaborationService.removeCollaborator(
                                    ManageCodeDocController.getCodeDoc().getCodeDocId(),
                                    collaborator.getUser().getUserID());
                            if (response == null) {
                                return;
                            }
                            Alert alert;
                            if (response.getStatus() == Status.SUCCESS) {
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setContentText("Collaborator Removed");
                                //TODO: refresh screen
                            } else {
                                alert = new Alert(Alert.AlertType.WARNING);
                                alert.setContentText("Cannot remove the collaborator at the moment. Try again later!");
                            }
                            alert.show();
                        } catch (IOException | ClassNotFoundException e) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Cannot remove the collaborator at the moment. Try again later!");
                            alert.show();
                            ;
                        }
                    }
                });

                changeRightButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Dialog<Pair<String, String>> dialog = new Dialog<>();
                        dialog.setTitle("Change rights of Collaborator");
                        ButtonType changeRightButtonType = new ButtonType("Change Right", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(changeRightButtonType, ButtonType.CANCEL);

                        // Create the userID and password labels and fields.
                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        TextField userID = new TextField();
                        userID.setText(collaborator.getUser().getUserID());
                        userID.setEditable(false);

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
                            if (dialogButton == changeRightButtonType) {
                                return new Pair<>(userID.getText(), (String) choiceBox.getValue());
                            }
                            return null;
                        });

                        Optional<Pair<String, String>> result = dialog.showAndWait();
                        Alert alert = new Alert(Alert.AlertType.NONE);
                        ;
                        result.ifPresent(idChoice -> {

                            int permissions = 0;
                            if (idChoice.getValue() == "Give write permissions")
                                permissions = 1;

                            try {
                                ChangeCollaboratorRightsResponse status = CollaborationService.changeCollaboratorRights(
                                        ManageCodeDocController.getCodeDoc().getCodeDocId(),
                                        idChoice.getKey(),
                                        permissions);
                                if (status.getStatus() == Status.SUCCESS) {

                                    alert.setAlertType(Alert.AlertType.INFORMATION);
                                    alert.setContentText("Collaborator rights updated successfully!");
                                } else {
                                    alert.setAlertType(Alert.AlertType.ERROR);
                                    alert.setContentText("Cannot update at the moment. Please try again later");
                                }
                                alert.show();
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                                alert.setAlertType(Alert.AlertType.ERROR);
                                alert.setContentText("Cannot update at the moment. Please try again later");
                                alert.show();
                            }
                        });
                    }
                });

            } else {
                removeButton.setVisible(false);
                changeRightButton.setVisible(false);
            }
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}