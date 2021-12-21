package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import mainClasses.EditorConnection;
import models.ActiveUser;
import models.Peer;

import java.util.ArrayList;

public class ActiveUserTabController{

    public JFXListView<ActiveUser> activeUsersListView;
    private EditorConnection editorConnection;

    public void setActiveUserTab(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }

    public void setActiveUsers() {

        ArrayList<ActiveUser> activeUsers = new ArrayList<>();
        for (Peer peer: editorConnection.getConnectedPeers().values()) {
            ActiveUser activeUser = new ActiveUser();

            activeUser.setUserId(peer.getUser().getUserID());
            activeUser.setEmail(peer.getUser().getEmail());
            activeUser.setFirstName(peer.getUser().getFirstName());
            activeUser.setLastName(peer.getUser().getLastName());
            activeUser.setHasWritePermissions(peer.isHasWritePermissions());

            activeUsers.add(activeUser);
        }

        activeUsersListView.setItems(FXCollections.observableArrayList(activeUsers));
        activeUsersListView.setCellFactory(new ActiveUserCardFactory(editorConnection));
    }

}
