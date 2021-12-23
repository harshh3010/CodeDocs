package controllers.notes;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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


public class MyNotesItemController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ImageView img;
    @FXML
    private AnchorPane cardPane;

    private Screenshot screenshot;

    public void setNotesItem(Screenshot screenshot) {
        this.screenshot = screenshot;
        titleLabel.setText(screenshot.getTitle());
        //dateLabel.setText(screenshot.getCreatedAt().toString());
        cardPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    try {
                        // if notes + ss both are fetched then only take the user to note screen
                        // else display alert box
                        if(fetchScreenshotData() == Status.SUCCESS)
                            SceneService.setScene(AppScreen.notesScreen);
                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Cannot fetch your notes right now");
                            alert.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //utility to read data from .png and notes from .txt file
    private Status fetchScreenshotData() throws IOException {

        File file = new File(CodeDocsClient.notesDirectory + screenshot.getFileName() + ".txt");
        BufferedReader bufferedReader = null;
        BufferedImage img = null;
        String notes = "";
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                notes = notes + line + "\n";
            }
            img = ImageIO.read(new File(CodeDocsClient.screenshotDirectory + screenshot.getFileName() + ".png"));
            Image image = SwingFXUtils.toFXImage(img,null);
            NotesScreenController.setNotes(notes);
            NotesScreenController.setImage(image);
            return Status.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.FAILED;
    }
}
