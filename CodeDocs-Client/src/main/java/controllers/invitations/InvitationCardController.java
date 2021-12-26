package controllers.invitations;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.CodeDoc;
import response.appResponse.AcceptInviteResponse;
import response.appResponse.RejectInviteResponse;
import services.CollaborationService;
import utilities.Status;

import java.io.IOException;

public class InvitationCardController extends ListCell<CodeDoc> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Text descText;
    @FXML
    private Label ownerLabel;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;

    public InvitationCardController() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invitation_card.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void updateItem(CodeDoc codeDoc, boolean b) {
        super.updateItem(codeDoc, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {

            titleLabel.setText(codeDoc.getOwnerName() + " invited you!");
            descText.setText(codeDoc.getOwnerName() + " invited you to join the CodeDoc: " + codeDoc.getTitle() + "\n" + codeDoc.getDescription());
            ownerLabel.setText("");

            acceptButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        AcceptInviteResponse response = CollaborationService.acceptInvite(codeDoc.getCodeDocId());
                        if (response == null) {
                            return;
                        }
                        Alert alert;
                        if (response.getStatus() == Status.SUCCESS) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("You are added as a collaborator to this CodeDoc!");
                        } else {
                            alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Cannot accept the invitation at the moment. Try again later!");
                        }
                        alert.show();
                    } catch (IOException | ClassNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Cannot accept the invitation at the moment. Try again later!");
                        alert.show();
                    }
                }
            });

            rejectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        RejectInviteResponse response = CollaborationService.rejectInvite(codeDoc.getCodeDocId());
                        if (response == null) {
                            return;
                        }
                        Alert alert;
                        if (response.getStatus() == Status.SUCCESS) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Invitation rejected");
                        } else {
                            alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Cannot reject the invitation at the moment. Try again later!");
                        }
                        alert.show();
                    } catch (IOException | ClassNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Cannot reject the invitation at the moment. Try again later!");
                        alert.show();
                    }
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
