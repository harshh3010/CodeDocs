package controllers.chat;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import mainClasses.editor.EditorConnection;
import models.Chat;
import models.Peer;
import models.User;
import requests.peerRequests.SendMessageRequest;
import utilities.UserApi;

import java.io.IOException;
import java.util.ArrayList;

public class ChatTabController {

    public JFXListView<VBox> chatListView;
    public TextArea msg;
    private EditorConnection editorConnection;
    public ComboBox<User> receiverComboBox;

    // To set editorConnection instance to current open editor
    public void setEditorConnection(EditorConnection editorConnection) {
        msg.setWrapText(true);
        this.editorConnection = editorConnection;
    }

    private void setReceiverComboBox() {

        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setFirstName("Everyone");
        user.setLastName("");
        users.add(user);
        for (Peer peer : editorConnection.getConnectedPeers().values()) {
            users.add(peer.getUser());
        }

        receiverComboBox.setItems(FXCollections.observableArrayList(users));

        receiverComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User object) {
                return object.getFirstName() + " " + object.getLastName();
            }

            @Override
            public User fromString(String string) {
                return receiverComboBox.getItems().stream().filter(user ->
                        (user.getFirstName() + " " + user.getLastName()).equals(string)).findFirst().orElse(null);
            }
        });
    }

    //to set the current message in chat drawer
    private void setChatInDrawer(Chat chat) {
        Label messageLabel = new Label();
        Label usernameLabel = new Label();
        VBox vBox = new VBox();
        vBox.setPrefWidth(382.0);
        messageLabel.setPrefWidth(286.0);

        usernameLabel.setPrefHeight(27.0);
        usernameLabel.setStyle("-fx-font-size: 12px");
        usernameLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setText(chat.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(250.0);
        String receiver = "you";
        if (chat.getUserID().equals(UserApi.getInstance().getId())) {
            receiver = receiverComboBox.getValue().getFirstName();
        }
        vBox.getChildren().add(usernameLabel);
        vBox.getChildren().add(messageLabel);

        //styling
        //if this is peer's own chat
        if (chat.getUserID().equals(UserApi.getInstance().getId())) {
            usernameLabel.setText(chat.getFirstName() + " to " + (chat.isPrivate() ? receiver : "everyone"));
            messageLabel.setTextAlignment(TextAlignment.RIGHT);
            vBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            messageLabel.setStyle("-fx-background-color: rgba(214, 6, 77,0.3);" +
                    "-fx-text-fill: white;\n" +
                    "    -fx-padding: 10px;\n" +
                    "    -fx-font-size: 16px;\n" +
                    "    -fx-background-radius:0 20 20 20 ;");
        } else {
            //if it is someone else's msg
            usernameLabel.setText(chat.getFirstName() + " to " + (chat.isPrivate() ? receiver : "everyone"));
            messageLabel.setTextAlignment(TextAlignment.LEFT);
            messageLabel.setStyle("-fx-background-color: rgba(214, 6, 77,0.7);"
                    + "-fx-text-fill: white;"
                    + "-fx-padding: 10px;"
                    + "-fx-font-size: 16px;"
                    + "-fx-background-radius:0 20 20 20;");
            vBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }
        chatListView.getItems().add(vBox);
    }

    /**
     * whenever a peer sends a message ... send it to all the connected peers
     * in current editorConnection
     * and after that set current msg in chatDrawer for current peer
     *
     * @param mouseEvent
     */
    public void onSendClicked(MouseEvent mouseEvent) {

        Chat chat = new Chat();
        chat.setMessage(msg.getText());
        chat.setUserID(UserApi.getInstance().getId());
        chat.setFirstName(UserApi.getInstance().getFirstName());

        User receiver = receiverComboBox.getValue();
        if (receiver == null) {
            receiverComboBox.getSelectionModel().selectFirst();
        }
        receiver = receiverComboBox.getValue();

        if (receiver.getUserID() == null) {
            chat.setPrivate(false);
        } else {
            chat.setPrivate(true);
        }

        setChatInDrawer(chat);

        msg.setText("");

        if (receiver.getUserID() == null) {
            SendMessageRequest request = new SendMessageRequest();
            request.setContent(chat.getMessage());
            request.setPrivate(false);
            for (Peer peer : editorConnection.getConnectedPeers().values()) {
                try {
                    peer.getOutputStream().writeObject(request);
                    peer.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (editorConnection.getConnectedPeers().get(receiver.getUserID()) != null) {
                SendMessageRequest request = new SendMessageRequest();
                request.setContent(chat.getMessage());
                request.setPrivate(true);
                try {
                    editorConnection.getConnectedPeers().get(receiver.getUserID()).getOutputStream().writeObject(request);
                    editorConnection.getConnectedPeers().get(receiver.getUserID()).getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateActiveUsers() {
        setReceiverComboBox();
    }

    public void addNewMessage(Chat chat) {
        setChatInDrawer(chat);
    }
}
