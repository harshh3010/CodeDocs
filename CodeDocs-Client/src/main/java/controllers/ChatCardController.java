package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Chat;
import models.User;
import utilities.UserApi;

import java.io.IOException;

public class ChatCardController extends ListCell<Chat> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private TextField messageField;
    @FXML
    private Label usernameLabel;
    @FXML
    private VBox vBox;

    public ChatCardController() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat_card.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Chat chat, boolean b) {

        super.updateItem(chat, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            messageField.setText(chat.getMessage());
            usernameLabel.setText(chat.getFirstName());
            if(chat.getUserID().equals(UserApi.getInstance().getId())){
                vBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }else{
                vBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
