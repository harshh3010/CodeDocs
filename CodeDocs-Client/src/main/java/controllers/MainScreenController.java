package controllers;

import controllers.codeDocs.MyLibraryTabController;
import controllers.codeDocs.PersonalCodeDocsTabController;
import controllers.invitations.InvitationsTabController;
import controllers.notes.MyNotesTabController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import response.appResponse.CreateCodeDocResponse;
import services.CodeDocsService;
import services.UserService;
import utilities.Status;
import utilities.TabController;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    public Text firstNameLabel;
    public Text emailLabel;

    public Tab myLibraryTab;
    public Tab personalCodeDocsTab;
    public Tab invitationsTab;
    public Tab notesTab;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        firstNameLabel.setText("Welcome " + UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());

        try {
            setupMyLibrary();
            setupPersonalCodeDocs();
            setupInvitations();
            setupMyNotes();
        } catch (IOException e) {
            errorAlert.setContentText("Unable to fetch data from the server! Please try again later.");
            errorAlert.show();
        }
    }

    private void setupMyLibrary() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my_library_tab.fxml"));
        AnchorPane pane = loader.load();
        if (TabController.getInstance().getMyLibraryTabController() == null) {
            TabController.getInstance().setMyLibraryTabController(loader.getController());
            TabController.getInstance().getMyLibraryTabController().setupTab();
        } else {
            MyLibraryTabController controller = loader.getController();
            controller.setupTab(TabController.getInstance().getMyLibraryTabController());
        }
        myLibraryTab.setContent(pane);
    }

    private void setupPersonalCodeDocs() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personal_codedocs_tab.fxml"));
        AnchorPane pane = loader.load();
        if (TabController.getInstance().getPersonalCodeDocsTabController() == null) {
            TabController.getInstance().setPersonalCodeDocsTabController(loader.getController());
            TabController.getInstance().getPersonalCodeDocsTabController().setupTab();
        } else {
            PersonalCodeDocsTabController controller = loader.getController();
            controller.setupTab(TabController.getInstance().getPersonalCodeDocsTabController());
        }
        personalCodeDocsTab.setContent(pane);
    }

    private void setupInvitations() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invitations_tab.fxml"));
        AnchorPane pane = loader.load();
        if (TabController.getInstance().getInvitationsTabController() == null) {
            TabController.getInstance().setInvitationsTabController(loader.getController());
            TabController.getInstance().getInvitationsTabController().setupTab();
        } else {
            InvitationsTabController controller = loader.getController();
            controller.setupTab(TabController.getInstance().getInvitationsTabController());
        }
        invitationsTab.setContent(pane);
    }

    private void setupMyNotes() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my_notes_tab.fxml"));
        AnchorPane pane = loader.load();
        notesTab.setContent(pane);
    }

    public void onClickCreate() {

        try {
            CreateCodeDocResponse response = CodeDocsService.createCodeDoc();
            if (response == null) {
                return;
            }
            Alert alert;
            if (response.getStatus() == Status.SUCCESS){
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Codedoc created successfully!");
            }else{
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot create codedoc at the moment!");
            }
            alert.show();
        }catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void onClickLogout() {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }
}
