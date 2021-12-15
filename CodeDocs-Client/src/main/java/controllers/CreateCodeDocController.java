package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.AuthenticationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.LanguageType;
import utilities.LoginStatus;
import utilities.Status;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateCodeDocController implements Initializable {
    @FXML
    public TextField titleTF;
    @FXML
    public TextArea descriptionTF;
    @FXML
    public ComboBox languageSelector;
    ObservableList<String> languageList = FXCollections.observableArrayList("JAVA","PYTHON","CPP","C");
    private String title;
    private String description;
    private int languageIndex;
    Alert a = new Alert(Alert.AlertType.NONE);

    private void getData() {
        title = titleTF.getText().trim().toLowerCase();
        description = descriptionTF.getText().trim().toLowerCase();
        languageIndex = languageSelector.getSelectionModel().getSelectedIndex();

    }


    private void createCodeDoc() {
        try {
            //TODO:create ur codedoc man
        }catch (Exception e) {

            e.printStackTrace();
        }
    }
    @FXML
    public void onCreateClicked(ActionEvent actionEvent) {
        getData();
        createCodeDoc();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        languageSelector.setItems(languageList);
    }
}
