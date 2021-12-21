package controllers;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Peer;

public class ActiveUserCardFactory implements Callback<ListView<Peer>, ListCell<Peer>> {
    @Override
    public ListCell<Peer> call(ListView<Peer> activeUsersListView) {
        return new ActiveUserCardController();
    }
}