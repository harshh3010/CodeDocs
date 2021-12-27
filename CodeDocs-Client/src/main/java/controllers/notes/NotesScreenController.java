package controllers.notes;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import services.SceneService;
import services.UserService;
import utilities.AppScreen;
import utilities.UserApi;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class NotesScreenController implements Initializable {

    // Components to display notes info
    public ImageView imageView;
    public TextArea notesTA;
    public Text firstNameLabel;
    public Text emailLabel;

    // Content and image of notes
    private static String notes;
    private static Image image;

    // Alert dialog to display errors
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    /**
     * Getter for notes
     */
    public static String getNotes() {
        return notes;
    }

    /**
     * Setter for notes
     */
    public static void setNotes(String notes) {
        NotesScreenController.notes = notes;
    }

    /**
     * Getter for image
     */
    public static Image getImage() {
        return image;
    }

    /**
     * Getter for image
     */
    public static void setImage(Image image) {
        NotesScreenController.image = image;
    }

    /**
     * Actions to be performed on load
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorAlert.setOnCloseRequest(dialogEvent -> System.exit(1));

        // Setting up the notes screen
        setNotesScreen();
    }

    /**
     * Function to set up the notes screen
     */
    private void setNotesScreen() {

        // Displaying user info
        firstNameLabel.setText("Welcome " + UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());

        // Displaying notes image and content
        imageView.setImage(getImage());
        notesTA.setText(getNotes());
        notesTA.setEditable(false);
    }

    /**
     * Action for logout button
     */
    public void onClickLogout() {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }

    /**
     * Action for back button
     */
    public void onClickBack(ActionEvent actionEvent) {
        try {
            SceneService.setScene(AppScreen.mainScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
