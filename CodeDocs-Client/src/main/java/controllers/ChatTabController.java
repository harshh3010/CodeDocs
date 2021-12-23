package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import mainClasses.EditorConnection;
import models.Chat;
import models.Peer;
import utilities.UserApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatTabController {
    public JFXListView chatListView;
    public List<Chat> chats = new ArrayList<>();
    public TextArea msg;
    private EditorConnection editorConnection;
    public ComboBox receiverComboBox;

    //to set editorConnection instance to current open editor
    public void setEditorConnection(EditorConnection editorConnection) {
        msg.setWrapText(true);
        this.editorConnection = editorConnection;
        setReceiverComboBox();
    }

    private void setReceiverComboBox(){
        receiverComboBox.getItems().add("Everyone");
        HashMap<String, Peer> connectedPeers = editorConnection.getConnectedPeers();
        for (HashMap.Entry<String, Peer> set :
                connectedPeers.entrySet()) {
            receiverComboBox.getItems().add(set.getKey());

        }
        receiverComboBox.getSelectionModel().selectFirst();
    }

    //to set the current message in chat drawer
    public void setChatInDrawer(Chat chat) {
        Label messageLabel =new Label();
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
        usernameLabel.setText(chat.getFirstName());
        vBox.getChildren().add(usernameLabel);
        vBox.getChildren().add(messageLabel);

        //styling
        //if this is peer's own chat
        if(chat.getUserID().equals(UserApi.getInstance().getId())){
            messageLabel.setTextAlignment(TextAlignment.RIGHT);
            vBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            messageLabel.setStyle("-fx-background-color: rgba(214, 6, 77,0.3);" +
                    "-fx-text-fill: white;\n" +
                    "    -fx-padding: 10px;\n" +
                    "    -fx-font-size: 16px;\n" +
                    "    -fx-background-radius:0 20 20 20 ;");
        }else{
            //if it is someone else msg
            messageLabel.setTextAlignment(TextAlignment.LEFT);
            messageLabel.setStyle("-fx-background-color: rgba(214, 6, 77,0.7);" +
                    "-fx-text-fill: white;\n" +
                    "    -fx-padding: 10px;\n" +
                    "    -fx-font-size: 16px;\n" +
                    "    -fx-background-radius:0 20 20 20;");
            vBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }
        chatListView.getItems().add(vBox);

    }

    /**
     * whenever a peer sends a message ... send it to all the connected peers
     * in current editorConnection
     * and after that set current msg in chatDrawer for current peer
     * @param mouseEvent
     */
    public void onSendClicked(MouseEvent mouseEvent) {
        Chat chat = new Chat();
        chat.setMessage(msg.getText());
        msg.setText("");
        chat.setUserID(UserApi.getInstance().getId());
        chat.setFirstName(UserApi.getInstance().getFirstName());
        String receiver = (String) receiverComboBox.getValue();
        System.out.println(receiver);
        chats.add(chat);

        //TODO: if reciver== everyone --iterate for all peers ; else fo the selected userID
        setChatInDrawer(chat);
    }
}
