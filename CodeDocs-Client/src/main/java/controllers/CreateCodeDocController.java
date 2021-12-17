package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import response.appResponse.CreateCodeDocResponse;
import services.AuthenticationService;
import services.CodeDocsService;
import services.SceneService;
import utilities.AppScreen;
import utilities.LanguageType;
import utilities.LoginStatus;
import utilities.Status;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateCodeDocController implements Initializable {
    @FXML
    public TextField titleTF;
    @FXML
    public TextArea descriptionTF;
    @FXML
    public ComboBox<String> languageSelector;
    @FXML
    public Button createButton;

    ArrayList<LanguageType> languageList = new ArrayList<>();

    private String title;
    private String description;
    private LanguageType languageType;
    Alert a = new Alert(Alert.AlertType.NONE);

    private void getData() {
        title = titleTF.getText().trim();
        description = descriptionTF.getText().trim();
        int languageIndex = languageSelector.getSelectionModel().getSelectedIndex();
        languageType = languageList.get(languageIndex);
    }


    private void createCodeDoc() {
        try {
            CreateCodeDocResponse createCodeDocResponse = CodeDocsService.createCodeDoc(title,description,languageType);
            if (createCodeDocResponse.getStatus() == Status.SUCCESS){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Codedoc created successfully!");
                alert.show();
            }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Cannot create codedoc at the moment!");
                alert.show();
            }
        }catch (Exception e) {

            e.printStackTrace();
        }
    }
    @FXML
    public void onCreateClicked(ActionEvent actionEvent) {
        getData();
        createCodeDoc();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        languageList.add(LanguageType.JAVA);
        languageList.add(LanguageType.PYTHON);
        languageList.add(LanguageType.C);
        languageList.add(LanguageType.CPP);

        ArrayList<String> lang = new ArrayList<>();
        for(LanguageType l : languageList){
            lang.add(l.getLanguage());
        }

        languageSelector.setItems(FXCollections.observableArrayList(lang));

    }
}
