package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import mainClasses.EditorConnection;
import models.ActiveUser;
import models.Peer;
import requests.peerRequests.TakeControlRequest;
import utilities.UserApi;

import java.io.IOException;

public class ActiveUserCardController extends ListCell<ActiveUser> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button menuButton;

    private final EditorConnection editorConnection;

    public ActiveUserCardController(EditorConnection editorConnection) {
        loadFXML();
        this.editorConnection = editorConnection;
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/active_user_card.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to set up the menu to be displayed on click of menuButton
     */
    private void setUpMenuButton(ActiveUser activeUser) {

        // Creating the context menu
        ContextMenu contextMenu = new ContextMenu();

        // Creating items to be displayed in the menu
        boolean isMuted = editorConnection.getConnectedPeers().get(activeUser.getUserId()).isMuted();
        MenuItem item1 = new MenuItem(isMuted ? "Un-mute" : "Mute");
        item1.setOnAction(actionEvent -> {
            editorConnection.getConnectedPeers().get(activeUser.getUserId()).setMuted(!isMuted);
        });
        contextMenu.getItems().add(item1);

        if (activeUser.isHasWritePermissions() && editorConnection.getUserInControl().equals(activeUser.getUserId())) {
            MenuItem item2 = new MenuItem("Request control");
            item2.setOnAction(actionEvent -> {
                try {
                    Peer peer = editorConnection.getConnectedPeers().get(activeUser.getUserId());
                    peer.getOutputStream().writeObject(new TakeControlRequest());
                    peer.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            contextMenu.getItems().add(item2);
        }

        contextMenu.setStyle("-fx-text-fill: black;");

        // Displaying the menu on button click
        menuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(menuButton, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        });
    }


    @Override
    protected void updateItem(ActiveUser activeUser, boolean b) {
        super.updateItem(activeUser, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {

            if (activeUser.isHasWritePermissions()) {
                nameLabel.setStyle("-fx-text-fill: green;");
            } else {
                nameLabel.setStyle("-fx-text-fill: white;");
            }

            nameLabel.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
            emailLabel.setText(activeUser.getEmail());

            if (!activeUser.getUserId().equals(UserApi.getInstance().getId())) {
                setUpMenuButton(activeUser);
            }

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}