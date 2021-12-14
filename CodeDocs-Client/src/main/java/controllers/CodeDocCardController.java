package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import models.CodeDoc;

import java.io.*;
import java.util.Objects;

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
                stream = new FileInputStream("D:\\Softablitz\\CodeDocs\\CodeDocs-Client\\src\\main\\resources\\images\\" + codeDoc.getLanguageType().getLanguage() + ".png");
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
                    System.out.println("Update " + codeDoc.getCodeDocId());
                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("Delete " + codeDoc.getCodeDocId());
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
