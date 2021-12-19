package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainClasses.EditorConnection;
import models.CodeDoc;
import models.CodeEditor;
import models.Peer;
import response.appResponse.CreateCodeDocResponse;
import response.editorResponse.CompileCodeDocResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.RunCodeDocResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.CodeDocsService;
import services.EditorService;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;

public class EditorController {

    public BorderPane borderPane;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    private EditorConnection editorConnection;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public void setCodeDoc(CodeDoc codeDoc) {
        this.codeDoc = codeDoc;

        try {
            editorConnection = new EditorConnection(codeDoc.getCodeDocId());

            try {

                System.out.println("Control: " + editorConnection.getUserInControl());
                System.out.println("Current: " + UserApi.getInstance().getId());

                // TODO: Fetch content from user in control
                LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
                if (response.getStatus() == Status.SUCCESS) {
                    codeEditor = new CodeEditor(response.getContent(),
                            codeDoc.getLanguageType(),
                            editorConnection.isHasWritePermissions(),
                            editorConnection.getUserInControl().equals(UserApi.getInstance().getId()));

                    EditorConnection.textArea = codeEditor.getTextArea();
                    borderPane.setCenter(codeEditor.getTextArea());

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

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveContent(ActionEvent actionEvent) {
        codeDoc.setFileContent(codeEditor.getTextArea().getText());
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
                for (Peer peer : EditorConnection.connectedPeers.values()) {
                    if (peer.isHasWritePermissions()) {
                        userInControl = peer.getUser().getUserID();
                    }
                }
                // TODO: Transfer control
                System.out.println("User in control leaving... Control transfer to " + userInControl);
            }
            EditorService.destroyConnection(codeDoc.getCodeDocId(), userInControl);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compileCodeDoc() {
        try {
            CompileCodeDocResponse response = EditorService.compileCodeDoc(codeDoc.getCodeDocId(),codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS){
                if(response.getError()==""){
                    //TODO: set input text area text= Your code compiled successfully
                    System.out.println("No error");
                } else{
                    System.out.println(response.getError());
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot compile codedoc at the moment!");
                alert.show();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCodeDoc() {
        try {
            //TODO: set input = inputTextArea.getText()
            RunCodeDocResponse response = EditorService.runCodeDoc(codeDoc.getCodeDocId(),codeDoc.getLanguageType(),"10 20");
            if (response.getStatus() == Status.SUCCESS){
                if(response.getError()==""){
                    //TODO: set output text area text response.getOutput
                    System.out.println(response.getOutput());
                } else{
                    System.out.println(response.getError());
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot run codedoc at the moment!");
                alert.show();
            }
        }catch (Exception e) {
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
