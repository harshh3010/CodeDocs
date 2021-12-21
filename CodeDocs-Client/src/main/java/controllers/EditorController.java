package controllers;

import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mainClasses.EditorConnection;
import models.CodeDoc;
import models.Peer;
import response.editorResponse.CompileCodeDocResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.RunCodeDocResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.EditorService;
import utilities.CodeEditor;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable {

    public BorderPane borderPane;
    public TextArea inputTextArea;
    public TextArea outputTextArea;
    public JFXDrawer drawer;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    private EditorConnection editorConnection;
    private FXMLLoader loader;
    private ActiveUserTabController controller;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        drawer.setOnDrawerOpening(event ->
        {
            AnchorPane.setRightAnchor(drawer, 0.0);
            AnchorPane.setLeftAnchor(drawer, 0.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });

        drawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(drawer);
            AnchorPane.setLeftAnchor(drawer, -400.0);
            AnchorPane.setTopAnchor(drawer, 0.0);
            AnchorPane.setBottomAnchor(drawer, 0.0);
        });

    }

    public void setCodeDoc(CodeDoc codeDoc) throws IOException {
        this.codeDoc = codeDoc;

        try {
            editorConnection = new EditorConnection(codeDoc);

            try {
                loader = new FXMLLoader(getClass().getResource("/fxml/active_user_tab.fxml"));
                VBox box = loader.load();
                controller = loader.getController();
                controller.setActiveUserTab(editorConnection);
//                BackgroundFill myBF = new BackgroundFill(Color.BLACK, new CornerRadii(1),
//                        new Insets(0.0,0.0,0.0,0.0));
//                box.setBackground(new Background(myBF));
                drawer.setSidePane(box);
                drawer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // TODO: Fetch content from user in control
            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS) {

                boolean isEditable = editorConnection.getUserInControl().equals(UserApi.getInstance().getId());
                codeEditor = new CodeEditor(codeDoc.getLanguageType(), editorConnection, response.getContent(), isEditable);
                codeEditor.applyContentStyle(getClass().getResource("/css/test.css").toExternalForm(), "#690026");
                editorConnection.setCodeEditor(codeEditor);

                borderPane.setCenter(codeEditor);

            } else {
                alert.setContentText("Cannot load at the moment");
                alert.show();
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            alert.setContentText("Cannot load at the moment");
            alert.show();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        }

    }

    public void saveContent() {
        codeDoc.setFileContent(codeEditor.getText());
        try {
            SaveCodeDocResponse response = EditorService.saveCodeDoc(codeDoc);
            if (response.getStatus() == Status.SUCCESS) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                codeEditor.setDirty(false);
                alert.setContentText("Saved successfully");
            } else {
                alert.setContentText("Cannot save at the moment");
            }
            alert.show();

        } catch (IOException | ClassNotFoundException e) {
            alert.setContentText("Cannot load at the moment");
            alert.show();
            e.printStackTrace();
        }
    }

    public void exitEditor() {
        try {
            editorConnection.closeConnection();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compileCodeDoc() {
        try {
            CompileCodeDocResponse response = EditorService.compileCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS) {
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: black;");
                    outputTextArea.setText("Your code compiled successfully!");
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot compile codedoc at the moment!");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCodeDoc() {
        try {
            String input = inputTextArea.getText();
            RunCodeDocResponse response = EditorService.runCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType(), input);
            if (response.getStatus() == Status.SUCCESS) {
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: black;");
                    outputTextArea.setText(response.getOutput());
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot run codedoc at the moment!");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCompileClicked(ActionEvent actionEvent) {
        if(codeEditor.isDirty()){
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setContentText("You have unsaved changes, please save the CodeDoc first!");
            alert.show();
        }else{
            compileCodeDoc();
        }
    }

    public void onRunClicked(ActionEvent actionEvent) {
        if(codeEditor.isDirty()){
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setContentText("You have unsaved changes, please save the CodeDoc first!");
            alert.show();
        }else{
            runCodeDoc();
        }
    }

    public void onActiveUserClicked(ActionEvent actionEvent) {
        if (drawer.isOpened()) {
            drawer.close();
        } else {
            controller.setActiveUsers();
            drawer.open();
        }
    }
}
