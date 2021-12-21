package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import mainClasses.EditorConnection;
import models.Chat;
import utilities.UserApi;
import java.util.ArrayList;
import java.util.List;

public class ChatTabController {
    public JFXListView<Chat> chatListView;
    public List<Chat> chats = new ArrayList<>();
    public TextField msg;
    private EditorConnection editorConnection;

    public void setEditorConnection(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }



    public void setChatDrawer() {
       
        chatListView.setItems(FXCollections.observableArrayList(chats));
        chatListView.setCellFactory(new ChatCardFactory());
    }

    public void onSendClicked(ActionEvent actionEvent) {
        Chat chat = new Chat();
        chat.setMessage(msg.getText());
        chat.setUserID(UserApi.getInstance().getId());
        chat.setFirstName(UserApi.getInstance().getFirstName());
        //chat.setDate();
        //TODO: set it from bottom to top
        chats.add(chat);
        //TODO: iterate for peers :(
    }
}
