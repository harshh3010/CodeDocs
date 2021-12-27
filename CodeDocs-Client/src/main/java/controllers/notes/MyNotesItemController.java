package controllers.notes;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import mainClasses.CodeDocsClient;
import models.Screenshot;
import services.SceneService;
import utilities.AppScreen;
import utilities.Status;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Controller for notes card
 */
public class MyNotesItemController {

    @FXML
    private Label titleLabel;
    @FXML
    private AnchorPane cardPane;

    private Screenshot screenshot;

    /**
     * Function to set up the notes item
     */
    public void setNotesItem(Screenshot screenshot) {
        this.screenshot = screenshot;
        titleLabel.setText(screenshot.getTitle());

        cardPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                try {

                    // if notes + ss both are fetched then only take the user to note screen
                    // else display alert box
                    if (fetchScreenshotData() == Status.SUCCESS)
                        SceneService.setScene(AppScreen.notesScreen);
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Cannot fetch your notes right now");
                        alert.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Function to read notes content and image from file system
     */
    private Status fetchScreenshotData() throws IOException {

        File file = new File(CodeDocsClient.notesDirectory + screenshot.getFileName() + ".txt");

        BufferedReader bufferedReader;
        BufferedImage img;

        StringBuilder notes = new StringBuilder();

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                notes.append(line).append("\n");
            }
            img = ImageIO.read(new File(CodeDocsClient.screenshotDirectory + screenshot.getFileName() + ".png"));
            Image image = SwingFXUtils.toFXImage(img, null);
            NotesScreenController.setNotes(notes.toString());
            NotesScreenController.setImage(image);
            return Status.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Status.FAILED;
    }
}
