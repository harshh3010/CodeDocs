package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import models.Peer;
import java.io.IOException;

public class ActiveUserCardController extends ListCell<Peer> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label nameLabel;

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
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}