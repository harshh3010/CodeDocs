package controllers.notes;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.SceneService;
import services.UserService;
import utilities.AppScreen;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class NotesScreenController implements Initializable {

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    public ImageView imageView;
    public TextArea notesTA;
    private static String notes;
    private static Image image;

    public static String getNotes() {
        return notes;
    }

    public static void setNotes(String notes) {
        NotesScreenController.notes = notes;
    }

    public static Image getImage() {
        return image;
    }

    public static void setImage(Image image) {
        NotesScreenController.image = image;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorAlert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent dialogEvent) {
                System.exit(1);
            }
        });
        setNotesScreen();
    }

    //setting up controls in notes_screen.fxml
    private void setNotesScreen(){
        imageView.setImage(getImage());
        notesTA.setText(getNotes());
        notesTA.setEditable(false);
    }

    //when logout button is pressed ..
    public void onClickLogout(ActionEvent actionEvent) {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }

    //when back button clicked...take the user to main screen
    public void onClickBack(ActionEvent actionEvent) {
        try {
            SceneService.setScene(AppScreen.mainScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
