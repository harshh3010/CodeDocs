package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import mainClasses.EditorConnection;
import models.Peer;
import models.User;
import java.util.ArrayList;
import java.util.List;

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
        user.setEmail("email@gmail.com");
        peer.setUser(user);
        peer.setHasWritePermissions(false);
        peers.add(peer);
        peers.add(peer);
        activeUsersListView.setItems(FXCollections.observableArrayList(peers));
        activeUsersListView.setCellFactory(new ActiveUserCardFactory());
    }

}
