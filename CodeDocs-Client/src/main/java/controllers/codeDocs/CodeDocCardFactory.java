package controllers.codeDocs;

import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.control.ListView;
import models.CodeDoc;

public class CodeDocCardFactory implements Callback<ListView<CodeDoc>, ListCell<CodeDoc>> {
    @Override
    public ListCell<CodeDoc> call(ListView<CodeDoc> codeDocListView) {
        return new CodeDocCardController();
    }
}
