package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import models.CodeDoc;
import response.appResponse.DeleteCodeDocResponse;
import response.appResponse.UpdateCodeDocResponse;
import services.CodeDocsService;
import services.clientServices.CodeDocService;
import utilities.Status;

import java.io.*;

public class CodeDocCardController extends ListCell<CodeDoc> {

    @FXML
    private Label titleLabel;
    @FXML
    private Text descText;
    @FXML
    private Label dateLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ImageView imageView;

    public CodeDocCardController() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/codedoc_card.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(CodeDoc codeDoc, boolean b) {
        super.updateItem(codeDoc, b);

        if (b) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {

            titleLabel.setText(codeDoc.getTitle());
            descText.setText(codeDoc.getDescription());
            dateLabel.setText(codeDoc.getCreatedAt().toString());

            // TODO: change path
            InputStream stream = null;
            try {
                stream = new FileInputStream("F:\\third_year_Softa\\CodeDocs\\CodeDocs-Client\\src\\main\\resources\\images\\" + codeDoc.getLanguageType().getLanguage() + ".png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Image image = new Image(stream);//Creating the image viewImageView
            imageView.setImage(image);//Setting the image view parameters
            imageView.setPreserveRatio(false);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            updateButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        UpdateCodeDocResponse response = CodeDocsService.updateCodeDoc(codeDoc.getCodeDocId());
                        if(response == null){
                            return;
                        }
                        Alert alert;
                        if(response.getStatus() == Status.SUCCESS) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("CodeDoc updated successfully!");
                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Failed to update CodeDoc!");
                        }
                        alert.show();
                    } catch (IOException | ClassNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Failed to update CodeDoc!");
                        alert.show();
                    }
                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        DeleteCodeDocResponse response = CodeDocsService.deleteCodeDoc(codeDoc.getCodeDocId());
                        if(response == null){
                            return;
                        }
                        Alert alert;
                        if (response.getStatus() == Status.SUCCESS) {
                            alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("CodeDoc deleted successfully!");
                        } else {
                            alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Failed to delete CodeDoc!");
                        }
                        alert.show();
                    } catch (IOException | ClassNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Failed to delete CodeDoc!");
                        alert.show();
                    }
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}