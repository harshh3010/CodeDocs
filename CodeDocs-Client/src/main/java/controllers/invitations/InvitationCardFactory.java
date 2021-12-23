package controllers.invitations;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.CodeDoc;

public class InvitationCardFactory implements Callback<ListView<CodeDoc>, ListCell<CodeDoc>> {
    @Override
    public ListCell<CodeDoc> call(ListView<CodeDoc> codeDocListView) {
        return new InvitationCardController();
    }
}