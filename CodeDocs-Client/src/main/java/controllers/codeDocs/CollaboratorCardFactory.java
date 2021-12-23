package controllers.codeDocs;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Collaborator;

public class CollaboratorCardFactory implements Callback<ListView<Collaborator>, ListCell<Collaborator>> {
    @Override
    public ListCell<Collaborator> call(ListView<Collaborator> collaboratorListView) {
        return new CollaboratorCardController();
    }
}