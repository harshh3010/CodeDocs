package controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import models.CodeEditor;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable {

    public BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        borderPane.setCenter(new CodeEditor("").getTextArea());
    }
}
