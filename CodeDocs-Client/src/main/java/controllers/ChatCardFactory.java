package controllers;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Chat;
import models.Peer;

public class ChatCardFactory implements Callback<ListView<Chat>, ListCell<Chat>> {
    @Override
    public ListCell<Chat> call(ListView<Chat> activeUsersListView) {
        return new ChatCardController();
    }
}
