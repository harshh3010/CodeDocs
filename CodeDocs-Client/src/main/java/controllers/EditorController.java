package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.CodeDoc;
import models.CodeEditor;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.EditorService;
import utilities.LanguageType;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorController {

    public BorderPane borderPane;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public void setCodeDoc(CodeDoc codeDoc){
        this.codeDoc = codeDoc;
        System.out.println(codeDoc.getTitle());
        try{
            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(),codeDoc.getLanguageType());
            if(response.getStatus() == Status.SUCCESS){
                codeEditor = new CodeEditor(response.getContent(), codeDoc.getLanguageType());
                borderPane.setCenter(codeEditor.getTextArea());
            }else {
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
}
