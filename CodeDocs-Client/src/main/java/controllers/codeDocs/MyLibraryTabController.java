package controllers.codeDocs;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import models.CodeDoc;
import response.appResponse.FetchCodeDocResponse;
import services.CodeDocsService;
import utilities.CodeDocRequestType;
import utilities.Status;

import java.io.IOException;
import java.util.ArrayList;

public class MyLibraryTabController {

    public Button prevButton;
    public Button nextButton;
    public JFXListView<CodeDoc> codeDocsListView;

    private int offset;
    private int rowCount;
    private ArrayList<CodeDoc> codeDocs;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    public void setupTab() {
        offset = 0;
        rowCount = 10;
        fetchCodeDocs();
    }

    public void setupTab(MyLibraryTabController controller) {
        offset = controller.getOffset();
        rowCount = controller.getRowCount();
        codeDocsListView.setItems(FXCollections.observableArrayList(controller.getCodeDocs()));
        codeDocsListView.setCellFactory(new CodeDocCardFactory());
    }

    private void fetchCodeDocs() {
        try {
            FetchCodeDocResponse response = CodeDocsService.fetchCodeDocs(CodeDocRequestType.ACCESSIBLE_CODEDOCS, null, rowCount, offset);
            if (response.getStatus() == Status.SUCCESS) {
                codeDocs = (ArrayList<CodeDoc>) response.getCodeDocs();
                codeDocsListView.setItems(FXCollections.observableArrayList(codeDocs));
                codeDocsListView.setCellFactory(new CodeDocCardFactory());
            } else {
                errorAlert.setContentText("An error occurred! Please try again later.");
                errorAlert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }


    public void onNextClicked() {

        // Fetching the next batch only if current one is non-empty
        if (codeDocsListView.getItems().size() == 10) {
            offset = offset + rowCount;
            fetchCodeDocs();
        }
    }

    public void onPreviousClicked() {

        // Fetching the previous batch only if offset is not 0 (Offset = 0 specifies first batch)
        if (offset > 0) {
            offset = offset - rowCount;
            fetchCodeDocs();
        }
    }

    public ArrayList<CodeDoc> getCodeDocs() {
        return codeDocs;
    }

    public int getOffset() {
        return offset;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void onRefreshClicked(ActionEvent actionEvent) {
        setupTab();
    }
}
