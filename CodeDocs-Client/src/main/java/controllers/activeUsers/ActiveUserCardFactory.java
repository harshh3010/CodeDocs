package controllers.activeUsers;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import mainClasses.editor.EditorConnection;
import models.ActiveUser;

public class ActiveUserCardFactory implements Callback<ListView<ActiveUser>, ListCell<ActiveUser>> {

    private final EditorConnection editorConnection;

    public ActiveUserCardFactory(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }

    @Override
    public ListCell<ActiveUser> call(ListView<ActiveUser> activeUsersListView) {
        return new ActiveUserCardController(editorConnection);
    }
}