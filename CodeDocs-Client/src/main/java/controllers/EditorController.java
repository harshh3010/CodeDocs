package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
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

public class EditorController {

    public BorderPane borderPane;
    public TextArea inputTextArea;
    public TextArea outputTextArea;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    private EditorConnection editorConnection;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public void setCodeDoc(CodeDoc codeDoc) {
        this.codeDoc = codeDoc;

        try {
            editorConnection = new EditorConnection(codeDoc.getCodeDocId());

            // TODO: Fetch content from user in control
            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS) {

                codeEditor = new CodeEditor(codeDoc.getLanguageType(), editorConnection, response.getContent());
                codeEditor.setEditable(editorConnection.getUserInControl().equals(UserApi.getInstance().getId()));
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

//        codeDoc.setFileContent(codeEditor.getTextArea().getText());

        codeDoc.setFileContent(codeEditor.getText());

        try {
            SaveCodeDocResponse response = EditorService.saveCodeDoc(codeDoc);
            if (response.getStatus() == Status.SUCCESS) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
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
            String userInControl = editorConnection.getUserInControl();
            if (userInControl.equals(UserApi.getInstance().getId())) {
                userInControl = null;
                for (Peer peer : editorConnection.getConnectedPeers().values()) {
                    if (peer.isHasWritePermissions()) {
                        userInControl = peer.getUser().getUserID();
                    }
                }
                // TODO: Transfer control
                System.out.println("User in control leaving... Control transfer to " + userInControl);
            }
            EditorService.destroyConnection(codeDoc.getCodeDocId(), userInControl);
            editorConnection.closeConnection();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compileCodeDoc() {
        try {
            saveContent();
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
            saveContent();
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
        compileCodeDoc();
    }

    public void onRunClicked(ActionEvent actionEvent) {
        runCodeDoc();
    }
}
