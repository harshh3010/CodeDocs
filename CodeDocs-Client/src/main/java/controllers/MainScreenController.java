package controllers;

import controllers.codeDocs.MyLibraryTabController;
import controllers.codeDocs.PersonalCodeDocsTabController;
import controllers.invitations.InvitationsTabController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import response.appResponse.CreateCodeDocResponse;
import services.CodeDocsService;
import services.UserService;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the main app screen
 */
public class MainScreenController implements Initializable {

    // Components to display user info
    public Text firstNameLabel;
    public Text emailLabel;

    // Navigation tabs
    public Tab myLibraryTab;
    public Tab personalCodeDocsTab;
    public Tab invitationsTab;
    public Tab notesTab;

    // Alert dialog to display errors
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    /**
     * Actions to be performed when the fxml loads
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Display user info
        firstNameLabel.setText("Welcome " + UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());

        // Setup navigation bar
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

    /**
     * Displays user's codedocs in a separate tab
     */
    private void setupMyLibrary() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my_library_tab.fxml"));
        AnchorPane pane = loader.load();
        MyLibraryTabController controller = loader.getController();
        controller.setupTab();
        myLibraryTab.setContent(pane);
    }

    /**
     * Displays user's personal codedocs in a separate tab
     */
    private void setupPersonalCodeDocs() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personal_codedocs_tab.fxml"));
        AnchorPane pane = loader.load();
        PersonalCodeDocsTabController controller = loader.getController();
        controller.setupTab();
        personalCodeDocsTab.setContent(pane);
    }

    /**
     * Displays user's invitations in a separate tab
     */
    private void setupInvitations() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invitations_tab.fxml"));
        AnchorPane pane = loader.load();
        InvitationsTabController controller = loader.getController();
        controller.setupTab();
        invitationsTab.setContent(pane);
    }

    /**
     * Displays user's notes in a separate tab
     */
    private void setupMyNotes() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/my_notes_tab.fxml"));
        AnchorPane pane = loader.load();
        notesTab.setContent(pane);
    }

    /**
     * Action to be performed on click of create button
     */
    public void onClickCreate() {
        Alert alert;
        try {

            // Send codedoc creation request to the server
            CreateCodeDocResponse response = CodeDocsService.createCodeDoc();

            // No response implies user cancelled creation process
            if (response == null) {
                return;
            }

            // Display alert dialog according to the response received
            if (response.getStatus() == Status.SUCCESS) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Codedoc created successfully!");
                alert.show();
            } else {
                throw new IOException();
            }
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot create codedoc at the moment!");
            alert.show();
        }
    }

    /**
     * Action to be performed on click of logout button
     */
    public void onClickLogout() {
        try {

            // Logout the user from app
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }
}
