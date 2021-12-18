package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.CodeDoc;
import response.appResponse.DeleteCodeDocResponse;
import response.appResponse.InviteCollaboratorResponse;
import response.appResponse.UpdateCodeDocResponse;
import services.AuthenticationService;
import services.CodeDocsService;
import services.CollaborationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.Status;
import utilities.UserApi;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

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
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button inviteButton;
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

            // TODO: change path
            InputStream stream = null;
            try {
                stream = new FileInputStream("F:\\third_year_Softa\\trial 2\\CodeDocs\\CodeDocs-Client\\src\\main\\resources\\images\\" + codeDoc.getLanguageType().getLanguage() + ".png");
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
                        if (response == null) {
                            return;
                        }
                        Alert alert;
                        if (response.getStatus() == Status.SUCCESS) {
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
            //System.out.println(codeDoc.getOwnerID() +"   *********  \n"+ UserApi.getInstance().getId());
            if(codeDoc.getOwnerID().equals(UserApi.getInstance().getId())){
                inviteButton.setVisible(true);
                inviteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Dialog<Pair<String, String>> dialog = new Dialog<>();
                        dialog.setTitle("Invite Collaborator");
                        //dialog.setHeaderText("Look, a Custom Login Dialog");

                        ButtonType inviteButtonType = new ButtonType("Invite", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(inviteButtonType, ButtonType.CANCEL);

                        // Create the userID and password labels and fields.
                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        TextField userID = new TextField();
                        userID.setPromptText("user id");
                        PasswordField password = new PasswordField();
                        password.setPromptText("Password");
                        ChoiceBox choiceBox = new ChoiceBox();
                        choiceBox.getItems().add("Give write permissions");
                        choiceBox.getItems().add("Do not give write permissions");

                        grid.add(new Label("User ID:"), 0, 0);
                        grid.add(userID, 1, 0);
                        grid.add(new Label("write access:"), 0, 1);
                        grid.add(choiceBox, 1, 1);

                        dialog.getDialogPane().setContent(grid);

                        Platform.runLater(() -> userID.requestFocus());

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == inviteButtonType) {
                                return new Pair<>(userID.getText(), (String) choiceBox.getValue());
                            }
                            return null;
                        });

                        Optional<Pair<String, String>> result = dialog.showAndWait();

                        result.ifPresent(idChoice -> {

                            int permissions = 0;
                            if(idChoice.getValue() == "Give write permissions")
                                permissions = 1;

                            if(idChoice.getKey() == ""){
                                alert.setAlertType(Alert.AlertType.ERROR);
                                alert.setContentText("Please provide a user id");
                                alert.show();
                            }
                            else{
                                try {
                                    InviteCollaboratorResponse status = CollaborationService.inviteCollaborator(codeDoc.getCodeDocId(),idChoice.getKey(),permissions);
                                    if(status.getStatus() == Status.SUCCESS){

                                        alert.setAlertType(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Invitation sent successfully.");
                                    }else{
                                        alert.setAlertType(Alert.AlertType.ERROR);
                                        alert.setContentText("Cannot invite user at the moment. Please try again later");
                                    }
                                    alert.show();
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });

            }else{
                inviteButton.setVisible(false);
            }

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
                            stage.setMaximized(true);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
