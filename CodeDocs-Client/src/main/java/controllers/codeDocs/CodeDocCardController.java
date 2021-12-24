package controllers.codeDocs;

import controllers.EditorController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.CodeDoc;
import response.appResponse.DeleteCodeDocResponse;
import services.CodeDocsService;
import services.SceneService;
import utilities.AppScreen;
import utilities.Status;

import java.io.*;
import java.util.Objects;

public class CodeDocCardController extends ListCell<CodeDoc> {

    @FXML
    private AnchorPane cardPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Text descText;
    @FXML
    private Label dateLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Button manageButton;
    @FXML
    private ImageView imageView;

    Alert alert = new Alert(Alert.AlertType.NONE);

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

            Image image = new Image(getClass().getResource("/images/" + codeDoc.getLanguageType().getLanguage() + ".png").toExternalForm());//Creating the image viewImageView
            imageView.setImage(image);//Setting the image view parameters
            imageView.setPreserveRatio(false);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        DeleteCodeDocResponse response = CodeDocsService.deleteCodeDoc(codeDoc.getCodeDocId());
                        if (response == null) {
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

            manageButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        ManageCodeDocController.setCodeDoc(codeDoc);
                        SceneService.setScene(AppScreen.manageCodeDocScreen);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            cardPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        try {

                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/codedoc_editor.fxml")));
                            Scene scene = new Scene(loader.load());
                            EditorController editorController = loader.getController();
                            editorController.setCodeDoc(codeDoc);

                            Stage stage = new Stage();
                            stage.setTitle("CodeDoc Editor - " + codeDoc.getTitle());
                            stage.setScene(scene);
                            stage.setOnCloseRequest(windowEvent -> editorController.exitEditor());
                            stage.show();

                        } catch (IOException | ClassNotFoundException e) {
                            alert.setAlertType(Alert.AlertType.ERROR);
                            alert.setContentText("Cannot load CodeDoc! Please try again later.");
                            alert.show();
                        }
                    }
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
