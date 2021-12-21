package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import mainClasses.EditorConnection;
import models.CodeDoc;
import models.Peer;
import models.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ActiveUserTabController{
    public JFXListView<Peer> activeUsersListView;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private EditorConnection editorConnection;

    public void setActiveUserTab(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }

    public void setActiveUsers() {

        List<Peer>  peers = new ArrayList<>();
        Peer peer = new Peer();
        User user = new User();
        user.setFirstName("HEllo");
        peer.setUser(user);
        peer.setHasWritePermissions(false);
        peers.add(peer);
        System.out.println("[[[[[[[[[");
        activeUsersListView.setItems(FXCollections.observableArrayList(peers));
        activeUsersListView.setCellFactory(new ActiveUserCardFactory());
    }

}
