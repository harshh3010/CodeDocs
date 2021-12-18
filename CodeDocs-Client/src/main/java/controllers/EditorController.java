package controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mainClasses.EditorConnection;
import models.CodeDoc;
import models.CodeEditor;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.EditorService;
import utilities.Status;

import java.io.IOException;

public class EditorController {

    public BorderPane borderPane;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public void setCodeDoc(CodeDoc codeDoc){
        this.codeDoc = codeDoc;

        try {
            EditorConnection editorConnection = new EditorConnection(codeDoc.getCodeDocId());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

//        try{
//            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(),codeDoc.getLanguageType());
//            if(response.getStatus() == Status.SUCCESS){
//                codeEditor = new CodeEditor(response.getContent(), codeDoc.getLanguageType());
//                borderPane.setCenter(codeEditor.getTextArea());
//
//            }else {
//                alert.setContentText("Cannot load at the moment");
//                alert.show();
//                Stage stage = (Stage) borderPane.getScene().getWindow();
//                stage.close();
//
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//            alert.setContentText("Cannot load at the moment");
//            alert.show();
//            Stage stage = (Stage) borderPane.getScene().getWindow();
//            stage.close();
//        }
    }


    public void saveContent(ActionEvent actionEvent) {
        codeDoc.setFileContent(codeEditor.getTextArea().getText());
        try{
            SaveCodeDocResponse response = EditorService.saveCodeDoc(codeDoc);
            if(response.getStatus() == Status.SUCCESS){
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Saved successfully");
            }else {
                alert.setContentText("Cannot save at the moment");
            }
            alert.show();
        } catch (IOException | ClassNotFoundException e) {
            alert.setContentText("Cannot load at the moment");
            alert.show();
            e.printStackTrace();
        }
    }

    public void exitEditor(ActionEvent actionEvent) {

        try{
            //TODO: send user in control
            EditorService.destroyConnection(codeDoc.getCodeDocId(),null);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
