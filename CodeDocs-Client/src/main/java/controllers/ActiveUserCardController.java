package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import models.Peer;
import java.io.IOException;

public class ActiveUserCardController extends ListCell<Peer> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button menuButton;

    public ActiveUserCardController() {
        loadFXML();
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

    /*
    Function to setup the menu to be displayed on click of menuButton
     */
    private void setUpMenuButton(Peer peer) {

        // Creating the context menu
        ContextMenu contextMenu = new ContextMenu();

        // Creating items to be displayed in the menu
        MenuItem item1 = new MenuItem("Mute/Unmute");
        MenuItem item2 = new MenuItem("Chat");
        MenuItem item3 = new MenuItem("Ask for control");


        // Setting action events for menu items
        item1.setOnAction(actionEvent -> {
            try {
                //TODO : do action

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        contextMenu.getItems().add(item1);
        contextMenu.getItems().add(item2);
        contextMenu.getItems().add(item3);

        contextMenu.setStyle("-fx-text-fill: black;");

        // Displaying the menu on button click
        menuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
                contextMenu.show(menuButton, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        });

    }


    @Override
    protected void updateItem(Peer peer, boolean b) {

        super.updateItem(peer, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            if(peer.isHasWritePermissions()){
                //TODO update color agr write permissions h
            }

            nameLabel.setText(peer.getUser().getFirstName());
            emailLabel.setText(peer.getUser().getEmail());
            setUpMenuButton(peer);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}