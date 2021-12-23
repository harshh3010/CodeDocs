package controllers.invitations;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import models.CodeDoc;
import response.appResponse.FetchInviteResponse;
import services.CollaborationService;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class InvitationsTabController {
    public JFXListView<CodeDoc> invitesListView;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    public Button prevButton;
    public Button nextButton;

    private int offset;
    private int rowCount;
    private ArrayList<CodeDoc> codeDocs;

    public void setupTab() {
        offset = 0;
        rowCount = 10;
        fetchInvites();
    }

    public void setupTab(InvitationsTabController controller) {
        offset = controller.getOffset();
        rowCount = controller.getRowCount();
        invitesListView.setItems(FXCollections.observableArrayList(controller.getCodeDocs()));
        invitesListView.setCellFactory(new InvitationCardFactory());
    }

    private void fetchInvites() {

        try {
            FetchInviteResponse response = CollaborationService.fetchInvites( rowCount, offset);
            if (response.getStatus() == Status.SUCCESS) {
                codeDocs = (ArrayList<CodeDoc>) response.getInvites();
                invitesListView.setItems(FXCollections.observableArrayList(codeDocs));
                invitesListView.setCellFactory(new InvitationCardFactory());
            } else {
                errorAlert.setContentText("An error occurred! Please try again later.");
                errorAlert.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }

    }

    public void onNextClicked(ActionEvent actionEvent) {

        // Fetching the next batch only if current one is non-empty
        if(invitesListView.getItems().size() == 5){
            offset = offset + rowCount;
            fetchInvites();
        }
    }

    public void onPreviousClicked(ActionEvent actionEvent) {

        // Fetching the previous batch only if offset is not 0 (Offset = 0 specifies first batch)
        if (offset > 0) {
            offset = offset - rowCount;
            fetchInvites();
        }
    }

    public int getOffset() {
        return offset;
    }

    public int getRowCount() {
        return rowCount;
    }

    public ArrayList<CodeDoc> getCodeDocs() {
        return codeDocs;
    }
}
