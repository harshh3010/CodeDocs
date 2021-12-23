package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainClasses.CodeDocsClient;
import models.CodeDoc;
import javafx.scene.control.Alert;
import services.UserService;
import utilities.UserApi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    public Text firstNameLabel;
    @FXML
    public Text emailLabel;
    @FXML
    public JFXListView<CodeDoc> codeDocsListView;

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameLabel.setText(UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());
        File file =new File(CodeDocsClient.screenshotDirectory);
        if (!file.exists()) {
            file.mkdir();
            System.out.print("SS Folder created");
        }
        file = new File(CodeDocsClient.notesDirectory);
        if (!file.exists()) {
            file.mkdir();
            System.out.print("Notes Folder created");
        }

    }
    public void onClickCreate(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/create_codedoc.fxml"));
            Parent root1 = (Parent)fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Create CodeDoc");
            stage.setScene(new Scene(root1));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onClickLogout(ActionEvent actionEvent) {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }
}
